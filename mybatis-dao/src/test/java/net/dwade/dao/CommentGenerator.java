package net.dwade.dao;


import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 自定义注释生成器，用于生成中文注释
 * @author huangxf
 * @date 2017年4月1日
 */
public class CommentGenerator extends DefaultCommentGenerator {
	
	/** The properties. */
    private Properties properties;
    
    /** The suppress date. */
    private boolean suppressDate;
    
    /** The suppress all comments. */
    private boolean suppressAllComments;

    /** The addition of table remark's comments.
     * If suppressAllComments is true, this option is ignored*/
    private boolean addRemarkComments;
    
    private SimpleDateFormat dateFormat;
    
    /**
     * 可以在xml中指定生成注释的Author
     */
    private String author;
    
    private final String separator = "\n";
    
	public CommentGenerator() {
		super();
        properties = new Properties();
        suppressDate = false;
        suppressAllComments = false;
        addRemarkComments = false;
        author = "Mybatis Generator";
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
	}
	
	public void addConfigurationProperties(Properties properties) {
        
		this.properties.putAll(properties);
        
        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
        
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

        addRemarkComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));
        
        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        
        author = properties.getProperty( "author" );
         
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

	/**
	 * 不需要在xml中生成注释
	 */
	@Override
	public void addComment(XmlElement xmlElement) {
		return;
	}
	
	@Override
	public void addModelClassComment(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		if (suppressAllComments  || !addRemarkComments) {
            return;
        }

        topLevelClass.addJavaDocLine("/**"); //$NON-NLS-1$

        //添加数据库表上面的注释
        String remarks = introspectedTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
        	remarks = remarks.trim() + "模型";
            String[] remarkLines = remarks.split( separator );  //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine);  //$NON-NLS-1$
            }
        }
        
        /**
         * @author huangxf
         * @date 2017年4月1日
        **/
        topLevelClass.addJavaDocLine( " * @author " + author );
        topLevelClass.addJavaDocLine( " * @date " + dateFormat.format( new Date() ) );
        topLevelClass.addJavaDocLine(" */"); //$NON-NLS-1$
	}
	
	/**
	 * 主要是针对serialVersionUID字段
	 */
	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
		return;
	}

	/**
	 * 字段注解
	 */
	@Override
	public void addFieldComment(Field field,
			IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
            return;
        }

        field.addJavaDocLine("/**"); //$NON-NLS-1$

        String remarks = introspectedColumn.getRemarks();
        
        if ( !StringUtility.stringHasValue(remarks) ) {
        	remarks = field.getName();
        }
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.trim().split( separator );  //$NON-NLS-1$
            StringBuilder sb = new StringBuilder();
            for (String remarkLine : remarkLines) {
            	sb.append( remarkLine ).append( "  " );
            }
            if ( sb.length() > 0 ) {
            	field.addJavaDocLine(" *  " + sb.toString() );
            } else {
            	field.addJavaDocLine(" *  " + remarks.trim() );
            }
        }

        //addJavadocTag(field, false);

        field.addJavaDocLine(" */"); //$NON-NLS-1$
	}
	
	/**
	 * 比如toString方法的注解
	 */
	@Override
	public void addGeneralMethodComment(Method method,
			IntrospectedTable introspectedTable) {
		return;
	}

	/**
	 * 生成getter方法注解
	 */
	@Override
	public void addGetterComment(Method method,
			IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");

        String remarks = introspectedColumn.getRemarks();
        
        //如果是没有remark，则用bean的字段名替代
        if ( !StringUtility.stringHasValue(remarks) ) {
        	String field = method.getName().replace( "get", "" );
        	StringUtils.uncapitalize( field );
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(" * @return ");
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split( separator );  //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
            	sb.append( remarkLine + "  " );
            }
        }
        
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
        
	}

	/**
	 * 生成setter方法注解
	 */
	@Override
	public void addSetterComment(Method method,
			IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
            return;
        }

        String remarks = introspectedColumn.getRemarks();
        
        if ( !StringUtility.stringHasValue(remarks) ) {
        	String field = method.getName().replace( "set", "" );
        	StringUtils.uncapitalize( field );
        }
        StringBuilder sb = new StringBuilder();
        sb.append( " * " );
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split( separator );  //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
            	sb.append( remarkLine + " " );
            }
        }
        
        method.addJavaDocLine("/**");
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */");
	}

	@Override
	public void addClassComment(InnerClass innerClass,
			IntrospectedTable introspectedTable) {
		System.out.println( innerClass );
	}

	@Override
	public void addClassComment(InnerClass innerClass,
			IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		System.out.println( innerClass );
	}
	
}
