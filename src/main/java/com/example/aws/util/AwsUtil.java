package com.example.aws.util;

import java.util.Arrays;
import java.util.Date;

import com.amazonaws.regions.Regions;

public class AwsUtil {

	public static Regions fromName(String regionName) {
		try {
			return Arrays.stream(Regions.values())
					.filter(region->region.getName().equals(regionName))
					.findFirst()
					.orElseThrow(()->new RuntimeException("Invalid region"));
		} catch (Exception e) {
			throw new RuntimeException("Exception occured while read region from AWS Regions");
		}
	}
	
	public static String appandPath(String fullFileName, String path) {
		int index = fullFileName.lastIndexOf(".");
		String fileName = fullFileName.substring(0, index);
		fileName = fileName + "_" + new Date().getTime();
		if(index != -1)
			fileName = fileName + fullFileName.substring(index);
		return path + fileName; 
	}
}
