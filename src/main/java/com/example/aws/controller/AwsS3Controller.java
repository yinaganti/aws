package com.example.aws.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.aws.service.S3Service;

@RestController
@RequestMapping("/s3")
public class AwsS3Controller {
	
	private S3Service s3Service;
	
	@Autowired
	public AwsS3Controller(S3Service service) {
		this.s3Service = service;
	}
	
	@PostMapping("/{name}")
	public ResponseEntity<Bucket> createBucket(@PathVariable String name) {
		return new ResponseEntity<Bucket>(s3Service.createBucket(name), HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<List<Bucket>> getBucketList() {
		return new ResponseEntity<List<Bucket>>(s3Service.getBucketList(), HttpStatus.OK);
	}
	
	@GetMapping("/{name}")
	public ResponseEntity<Bucket> getBucketByName(@PathVariable String name) {
		return new ResponseEntity<Bucket>(s3Service.getBucketByName(name), HttpStatus.OK);
	}
	
	@DeleteMapping("/{name}")
	public ResponseEntity<String> deleteBucketByName(@PathVariable String name) {
		s3Service.deleteBucketByName(name);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
	
	@PostMapping("/bucket/object/{name}")
	public ResponseEntity<ObjectMetadata> putObject(@RequestParam("file") MultipartFile file, @PathVariable String name) throws Exception {
		return new ResponseEntity<ObjectMetadata>(s3Service.uploadFile(file, name), HttpStatus.OK);
	}
	
	@GetMapping("/bucket/{bucketName}/object/{objectName}")
	public void getObject(@PathVariable("bucketName") String bucket, @PathVariable("objectName") String object) throws IOException {
		s3Service.downloadFile(bucket, object);
	}
	
	@DeleteMapping("/bucket/{bucketName}/object/{objectName}")
	public ResponseEntity<String> deleteObject(@PathVariable("bucketName") String bucket, @PathVariable("objectName") String object) {
		s3Service.deleteObject(bucket, object);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
	
	@GetMapping("/bucket/{name}/objects")
	public ResponseEntity<List<S3ObjectSummary>> getObjectList(@PathVariable String name) {
		return new ResponseEntity<List<S3ObjectSummary>>(s3Service.getObjectList(name), HttpStatus.OK);
	}
	
	@PostMapping("/bucket/{fromBucket}/{toBucket}/object/{objectName}")
	public ResponseEntity<CopyObjectResult> copyObject(@PathVariable String fromBucket, @PathVariable String toBucket, @PathVariable String objectName) {
		return new ResponseEntity<CopyObjectResult>(s3Service.copyObject(fromBucket, toBucket, objectName), HttpStatus.OK);
	}
}
