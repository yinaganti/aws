package com.example.aws.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.example.aws.util.AwsUtil;


@Service
public class S3Service {
	
	private AmazonS3 s3Client;
	
	@Value("${file.download.path}")
	private String path;
	
	@Autowired
	public S3Service(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}

	public Bucket createBucket(String bucketName) {
		Bucket b = null;
		if(s3Client.doesBucketExistV2(bucketName)) {
			System.out.println("already bucket was exist");
			throw new RuntimeException("bucket exist");
		} else {
			try {
				b = s3Client.createBucket(bucketName);
			} catch(AmazonS3Exception ex) {
				ex.printStackTrace();
			}
		}
		return b;
	}
	
	public List<Bucket> getBucketList() {
		return s3Client.listBuckets();
	}
	
	public Bucket getBucketByName(String name) {
		return getBucketList().stream()
				.filter(bucket->bucket.getName().equals(name))
				.findFirst()
				.orElseThrow(()->new RuntimeException("Invalid bucket name"));
	}
	
	/*
	 * For delete a bucket:
	 * first, should delete all objects from bucket i.e; we should
	 * keep bucket as empty
	 * 
	 * second, should delete all objects versions
	 */
	public void deleteBucketByName(String name) {
		//1. delete all objects
		ObjectListing objects = s3Client.listObjects(name);
		objects.getObjectSummaries()
			.forEach(summery -> s3Client.deleteObject(name, summery.getKey()));
		
		/*listObjects() not removed all objects if bucket has lot of objects
		 * it will removed first listing only
		 * to remove next listing of objects
		 */
		if(objects.isTruncated()) 
			objects = s3Client.listNextBatchOfObjects(objects);
		
		//2. delete all objects versions
		VersionListing versions = s3Client.listVersions(new ListVersionsRequest().withBucketName(name));
		versions.getVersionSummaries()
				.forEach(summery-> s3Client.deleteVersion(name, summery.getKey(), summery.getVersionId()));
		if(versions.isTruncated())
			versions = s3Client.listNextBatchOfVersions(versions);
		
		s3Client.deleteBucket(name);
	}
	
	public ObjectMetadata uploadFile(MultipartFile file, String bucketName) throws Exception {
		String fileName = file.getOriginalFilename();
		PutObjectResult result = s3Client.putObject(bucketName, fileName, file.getInputStream(), null);
		return result.getMetadata();
	}
	
	public void downloadFile(String bucket, String object) throws IOException {
		S3Object s3Object = s3Client.getObject(bucket, object);
		S3ObjectInputStream sis = s3Object.getObjectContent();
		
		FileOutputStream fos = new FileOutputStream(new File(AwsUtil.appandPath(object, path)));
		byte[] read_buf = new byte[1024];
		int len = 0;
		while((len = sis.read(read_buf)) > 0) {
			fos.write(read_buf, 0, len);
		}
		sis.close();
		fos.close();
	}
	
	public void deleteObject(String bucket, String object) {
		s3Client.deleteObject(bucket, object);
	}
	
	public List<S3ObjectSummary> getObjectList(String bucketName) {
		ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
		return result.getObjectSummaries();
	}
	
	public CopyObjectResult copyObject(String fromBucket, String toBucket, String objectName) {
		return s3Client.copyObject(fromBucket, objectName, toBucket, objectName);
	}
}
