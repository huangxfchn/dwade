package net.dwade.plugins.lock;

import java.util.Collections;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 基于Redis实现的分布式锁，详见<a href="https://redis.io/commands/eval">Redis Eval命令</a>
 * @author xiaofeng.huang
 */
public final class RedisLock {
	
	private static final String LOCK_SCRIPT = "if (redis.call('setnx', KEYS[1], ARGV[1]) == 1) then return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end";
	
	private static final String UNLOCK_SCRIPT = "if (redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";

	private StringRedisTemplate redisTemplate;
	
	public RedisLock(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	/**
	 * @param key
	 * @param uuid
	 * @param seconds
	 * @return
	 */
	public boolean tryLock( String key, String uuid, long seconds ) {
		// 参数：[key]、[value, ttl]
		RedisScript<Long> script = new DefaultRedisScript<Long>(LOCK_SCRIPT, Long.class);
		Long result = redisTemplate.execute(script, Collections.singletonList( key ), uuid, String.valueOf( seconds ) );
		if ( result == 1L ) {
			return true;
		}
		return false;
	}
	
	public boolean lock( String key, String uuid, long seconds, int tryTimes ) {
		int times = 1;
		do {
			if ( tryLock(key, uuid, seconds) ) {
				return true;
			}
			try {
				Thread.sleep( 20 );
			} catch (InterruptedException e) {
				// ignore exception 
			}
		} while ( times++ <= tryTimes );
		return false;
	}
	
	public boolean unlock( String key, String uuid ) {
		// 参数：[key]、[value]
		RedisScript<Long> script = new DefaultRedisScript<Long>(UNLOCK_SCRIPT, Long.class);
		Long result = redisTemplate.execute( script, Collections.singletonList( key ), uuid );
		
		// 成功删除key返回1
		if ( result == 1L ) {
			return true;
		}
		return false;
	}

}
