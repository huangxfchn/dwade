package net.dwade.dao;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * 运行该Main方法，即可生成注释，<em>注意：由于关闭了mapping映射文件的注释，
 * 如果重复生成会追加到xml中，导致mybatis解析异常，重复生成时请将xml文件删除再生成！</em>
 * @author huangxf
 * @date 2017年4月2日
 */
public class GeneratorMain {

	public static void main(String[] args) throws URISyntaxException {

		String path = "F:/JavaWorkspace/myself/normal/mybatis-dao/mybatis-generator/";

		//指定你的配置文件，可以指定一部分
		String[] filenames = new String[]{"generatorConfig-dict.xml", "generatorConfig-log.xml"};

		for ( String fileName : filenames ) {
			File configFile = new File( path + fileName );
			try {
				doGenerate( configFile );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void doGenerate( File configFile ) throws Exception {

		List<String> warnings = new ArrayList<String>();
		ConfigurationParser cp = new ConfigurationParser(warnings);

		boolean overwrite = true;
		DefaultShellCallback callback = new DefaultShellCallback( overwrite );

		//直接获取generatorConfig.xml的文件路径
		Configuration config = cp.parseConfiguration( configFile );

		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate( new SimpleProcessCallback() );

	}

}