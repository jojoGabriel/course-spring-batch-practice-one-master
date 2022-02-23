package com.christianoette.batch.practice;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public interface FileHandlingJobExecutionListener extends JobExecutionListener{

	@Override
	default void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	default void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

}
