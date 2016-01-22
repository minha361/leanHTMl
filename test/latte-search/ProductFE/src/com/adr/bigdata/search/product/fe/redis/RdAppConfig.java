/**
 * 
 */
package com.adr.bigdata.search.product.fe.redis;

import java.util.Map;

/**
 * @author minhvv2
 *
 */
public class RdAppConfig {
	private int timeToLive = 120;// in seconds
	private int rdPort = 8983;
	private String rdHost = "localhost";
	private int upperWaterMark;
	private int lowerWaterMark;
	private int acceptableSize;
	private int initialSize;

	private Map<String, Integer> maxCacheSizes;

	public Map<String, Integer> getMaxCacheSizes() {
		return maxCacheSizes;
	}

	public void setMaxCacheSizes(Map<String, Integer> maxCacheSizes) {
		this.maxCacheSizes = maxCacheSizes;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public int getRdPort() {
		return rdPort;
	}

	public void setRdPort(int rdPort) {
		this.rdPort = rdPort;
	}

	public String getRdHost() {
		return rdHost;
	}

	public void setRdHost(String rdHost) {
		this.rdHost = rdHost;
	}

	public int getUpperWaterMark() {
		return upperWaterMark;
	}

	public void setUpperWaterMark(int upperWaterMark) {
		this.upperWaterMark = upperWaterMark;
	}

	public int getLowerWaterMark() {
		return lowerWaterMark;
	}

	public void setLowerWaterMark(int lowerWaterMark) {
		this.lowerWaterMark = lowerWaterMark;
	}

	public int getAcceptableSize() {
		return acceptableSize;
	}

	public void setAcceptableSize(int acceptableSize) {
		this.acceptableSize = acceptableSize;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

}
