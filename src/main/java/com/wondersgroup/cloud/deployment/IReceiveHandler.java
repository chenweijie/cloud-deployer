package com.wondersgroup.cloud.deployment;


public interface IReceiveHandler {

	void handle(String msg, String srcIp);

}
