package com.christianoette.batch.practice.config;

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.christianoette.batch.practice.FileHandlingJobExecutionListener;

@SpringBootTest(classes = {JobConfigurationTest.TestConfig.class,
		JobConfiguration.class})
class JobConfigurationTest {
	
	@Autowired
	private Job job;
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@MockBean
	private FileHandlingJobExecutionListener fileHandlingJobExecutionlistener;

    @Test
    void happyCaseTest() throws Exception {
    	JobParameters jobParameters = new JobParametersBuilder()
    			.addString(AnonymizeJobParameterKeys.INPUT_PATH, "classpath:unitTestData/persons.json")
    			.addString(AnonymizeJobParameterKeys.OUTPUT_PATH, "output/unitTestOutput.json")
    			.addString(AnonymizeJobParameterKeys.ERROR_PATH, "ignored")
    			.addString(AnonymizeJobParameterKeys.UPLOAD_PATH, "ignored")
    			.toJobParameters();
    			
    	JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    	assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    	
    	
    	String outputContent = contentOf(new File("output/unitTestOutput.json"));

    	assertThat(outputContent).contains("Wei Lang");
    	assertThat(outputContent).doesNotContain("Daliah Shah");
    	
    	Mockito.verify(fileHandlingJobExecutionlistener).beforeJob(jobExecution);
    	Mockito.verify(fileHandlingJobExecutionlistener).afterJob(jobExecution);
    }

    
    @Test
    void testInvalidParameters() throws Exception {
    	
    	assertThatThrownBy(() -> jobLauncherTestUtils.launchJob(new JobParameters()))
    		.isInstanceOf(JobParametersInvalidException.class)
    		.hasMessageContaining("The JobParameters do not contain required keys");

    }
    
    
    @Test
    void anonymizeTest() throws Exception {
    	JobParameters jobParameters = new JobParametersBuilder()
    			.addString(AnonymizeJobParameterKeys.INPUT_PATH, "classpath:unitTestData/persons.json")
    			.addString(AnonymizeJobParameterKeys.OUTPUT_PATH, "output/unitTestOutput.json")
    			.addString(AnonymizeJobParameterKeys.ERROR_PATH, "ignored")
    			.addString(AnonymizeJobParameterKeys.UPLOAD_PATH, "ignored")
    			.addString(AnonymizeJobParameterKeys.ANONYMIZE_DATA, "true")    			
    			.toJobParameters();
    			
    	JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    	assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    	
    	
    	String outputContent = contentOf(new File("output/unitTestOutput.json"));

    	assertThat(outputContent).contains("John Doe");
    	assertThat(outputContent).doesNotContain("Wei Lang");
    	assertThat(outputContent).doesNotContain("Wei.Lang@domain.xyz");
    	assertThat(outputContent).doesNotContain("Daliah Shah");
    	assertThat(outputContent).doesNotContain("Daliah.Shah@domain.xyz");
    	
    	Mockito.verify(fileHandlingJobExecutionlistener).beforeJob(jobExecution);
    	Mockito.verify(fileHandlingJobExecutionlistener).afterJob(jobExecution);
    }
    
    
    
    @Configuration
    @EnableBatchProcessing
    static class TestConfig {
    	
    	@Bean
    	public JobLauncherTestUtils jobLauncherTestUtils() {
    		return new JobLauncherTestUtils();
    	}

    }
}
