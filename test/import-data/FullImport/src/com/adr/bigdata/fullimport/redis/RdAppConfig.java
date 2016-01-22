/**
 * 
 */
package com.adr.bigdata.fullimport.redis;

/**
 * @author minhvv2
 *
 */
public class RdAppConfig {
	private int rdPort = 8983;
	private String rdHost = "10.22075.78";

	public RdAppConfig(int rdPort, String rdHost) {
		super();
		this.rdPort = rdPort;
		this.rdHost = rdHost;
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
}
