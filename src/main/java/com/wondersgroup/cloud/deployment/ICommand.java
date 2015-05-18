package com.wondersgroup.cloud.deployment;

public interface ICommand {
	
	int getKey();

	String getAppId();

	String[] getDoingIPs();

}
