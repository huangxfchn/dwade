package net.dwade.dao;

import org.mybatis.generator.internal.NullProgressCallback;

public class SimpleProcessCallback extends NullProgressCallback {

	@Override
	public void generationStarted(int totalTasks) {
		super.generationStarted(totalTasks);
	}

	@Override
	public void startTask(String taskName) {
		super.startTask(taskName);
	}

	@Override
	public void done() {
		System.out.println( "Generator success." );
	}
	
	
}
