package net.dwade.plugins.spring.task;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.ContextLifecycleScheduledTaskRegistrar;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 支持动态添加任务、取消任务、修改执行时间的ScheduledTaskRegistrar
 * @author huangxf
 * @date 2017年5月1日
 */
public class DynamicScheduledTaskRegistrar extends ContextLifecycleScheduledTaskRegistrar {
	
	private static Logger logger = LoggerFactory.getLogger( DynamicScheduledTaskRegistrar.class );

	private ConcurrentMap<CronTask, ScheduledMethodRunnable> taskToMethodRunnable = 
			new ConcurrentHashMap<CronTask, ScheduledMethodRunnable>();
	
	private ConcurrentMap<CronTask, ScheduledTask> cronTaskToScheduledTask = 
			new ConcurrentHashMap<CronTask, ScheduledTask>();

	/**
	 * 添加自定义CronTask 
	 */
	private void addCustomCronTask( CronTask task ) {
		if ( task != null && task.getRunnable() instanceof ScheduledMethodRunnable ) {
			taskToMethodRunnable.putIfAbsent( task, (ScheduledMethodRunnable)task.getRunnable() );
		}
	}

	/**
	 * 重写afterPropertiesSet方法，将CronTask添加到taskToMethodRunnable
	 */
	@Override
	public void afterPropertiesSet() {
		List<CronTask> taskList = this.getCronTaskList();
		for ( CronTask task : taskList ) {
			this.addCustomCronTask( task );
		}
		super.afterPropertiesSet();
	}

	@Override
	public ScheduledTask scheduleCronTask( CronTask task ) {
		ScheduledTask scheduledTask = super.scheduleCronTask(task);
		if ( scheduledTask != null ) {
			cronTaskToScheduledTask.put( task, scheduledTask );
		}
		return scheduledTask;
	}
	
	/**
	 * 注册新的CronTask，在jvm运行时便可以生效，不需要重启应用
	 * @param task
	 * @return void
	 */
	public void registerRuntimeCronTask( CronTask task ) {
		this.addCustomCronTask( task );
		this.scheduleCronTask( task );
	}
	
	/**
	 * 注册新的任务，该任务只会执行一次，如果{@link Date startTime}是过去时间，该任务会马上执行
	 * @param task
	 * @param startTime
	 */
	public void registerRuntimeTask(Runnable task, Date startTime) {
		this.getScheduler().schedule( task, startTime );
	}
	
	/**
	 * 取消定时任务
	 * @return boolean
	 */
	public boolean cancleScheduledTask( Method method ) {
		ScheduledTask scheduledTask = this.findScheduledTaskByMethod( method );
		if ( scheduledTask != null ) {
			scheduledTask.cancel();
			return true;
		}
		logger.warn( "ScheduledTask not found with method:{}", method );
		return false;
	}
	
	/**
	 * 动态修改Cron表达式，在运行的时候改变其执行时机
	 * @param method
	 * @param newCronExpression
	 * @return void
	 */
	public void modifyCronTask( Method method, String newCronExpression ) {
		
		CronTask task = findCronTaskByMethod( method );
		if ( task == null ) {
			throw new IllegalArgumentException( "找不到对应的定时任务!" );
		}
		ScheduledTask scheduledTask = cronTaskToScheduledTask.get( task );
		if ( scheduledTask == null ) {
			throw new IllegalArgumentException( "找不到对应的定时任务!" );
		}
		
		//构造方法里面会判断cron表达式是否正确
		CronTrigger trigger = new CronTrigger( newCronExpression );
		
		//使用反射的方法修改表达式的值，因为future对象都是protected的，不能直接操作
		this.doMofidyCronIfSupported( scheduledTask, trigger );
		logger.info( "Modify cron task success! Old cron:{}, new cron:{}", task.getExpression(), newCronExpression );
		
	}
	
	@SuppressWarnings("rawtypes")
	protected void doMofidyCronIfSupported( ScheduledTask task, CronTrigger trigger ) throws UnsupportedOperationException {
		
		//从ScheduledTask中获取ScheduledFuture对象
		Field futureField = ReflectionUtils.findField( ScheduledTask.class, "future" );
		ReflectionUtils.makeAccessible( futureField );
		
		//future默认是由ReschedulingRunnable，如果不是则抛出异常
		ScheduledFuture future = (ScheduledFuture)getFieldValue( ScheduledTask.class, "future", task );
		String className = future.getClass().getSimpleName();
		if ( !StringUtils.equals( className, "ReschedulingRunnable" ) ) {
			throw new UnsupportedOperationException( "Not allowed to modity: "
					+ "the future of scheduled task is not a ReschedulingRunnable!" );
		}
		
		//对ReschedulingRunnable类里面的trigger进行修改
		Trigger oldTrigger = (Trigger)getFieldValue( future.getClass(), "trigger", future );
		if ( !(oldTrigger instanceof CronTrigger) ) {
			throw new UnsupportedOperationException( "Not allowed to modity: not a cron trigger!" );
		}
		
		//直接修改sequenceGenerator的值
		Field sequenceField = ReflectionUtils.findField( oldTrigger.getClass(), "sequenceGenerator" );
        ReflectionUtils.makeAccessible( sequenceField );
        ReflectionUtils.setField( sequenceField, oldTrigger, new CronSequenceGenerator( trigger.getExpression() ) );
		
        //重新执行schedule方法，确保修改后的cron表达式生效
		Method scheduleMethod = ReflectionUtils.findMethod( future.getClass(), "schedule" );
        ReflectionUtils.makeAccessible( scheduleMethod );
        ReflectionUtils.invokeMethod( scheduleMethod, future );
        
	}
	
	/**
	 * 根据Method寻找CronTask，找不到则返回null
	 * @return CronTask
	 */
	protected CronTask findCronTaskByMethod( Method method ) {
		for ( Entry<CronTask, ScheduledMethodRunnable> entry : taskToMethodRunnable.entrySet() ) {
			ScheduledMethodRunnable runnable = entry.getValue();
			// 我们定义的bean有可能被动态代理了，所以要获取原生的Method
			Class<?> userClass = ClassUtils.getUserClass( runnable.getMethod().getDeclaringClass() );
			Method userMethod = ClassUtils.getMethod( userClass, runnable.getMethod().getName() );
			
			boolean matched = userMethod.equals( method );
			if ( matched ) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	/**
	 * 根据方法名查找对应的ScheduledTask
	 * @return ScheduledTask
	 */
	protected ScheduledTask findScheduledTaskByMethod( Method method ) {
		CronTask task = this.findCronTaskByMethod( method );
		if ( task == null ) {
			return null;
		}
		return cronTaskToScheduledTask.get( task );
	}
	
	/**
	 * 获取对象的属性值
	 * @return Object
	 */
	@SuppressWarnings("rawtypes")
	private Object getFieldValue( Class targetClass, String fieldName, Object target ) {
		Field field = ReflectionUtils.findField( targetClass, fieldName );
		ReflectionUtils.makeAccessible( field );
		return ReflectionUtils.getField( field, target );
	}
	
}
