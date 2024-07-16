package com.example.aws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.aws.properties.AwsS3Properties;
import com.example.aws.util.AwsUtil;

@Configuration
public class S3Config {

	@Autowired
	private AwsS3Properties s3Properties;
	
	@Bean
	public AmazonS3 s3Client() {
		return AmazonS3ClientBuilder.standard()
				.withCredentials(credentials())
				.withRegion(AwsUtil.fromName(s3Properties.getRegion()))
				.build();
	}
	
	private AWSStaticCredentialsProvider credentials() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecret()));
	} 
}
