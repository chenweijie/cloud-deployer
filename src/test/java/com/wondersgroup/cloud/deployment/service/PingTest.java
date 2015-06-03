package com.wondersgroup.cloud.deployment.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingTest {

	public static void main(String[] args) {
//		Pinger p = new Pinger("10.1.65.105", 1, 5000);
//		System.out.println("ping::::" + p.isReachable());
		
		String line = "64 bytes from 10.1.65.105: icmp_seq=1 ttl=64 time=0.269 ms";
		//logger.info("Pinger detail:::" + line);
		// "(\\d+ms)(\\s+)(TTL=\\d+)"
		Pattern pattern = Pattern.compile("(TTL=\\d+)(\\s+)(TIME=)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			System.out.println("OK");
		}
		
	}

}
