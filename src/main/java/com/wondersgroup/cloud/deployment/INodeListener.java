package com.wondersgroup.cloud.deployment;


public interface INodeListener {

	void fireNodeEvent(String msg, String srcIp, Object... params);

}
