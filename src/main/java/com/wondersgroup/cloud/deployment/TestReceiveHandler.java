package com.wondersgroup.cloud.deployment;

import java.net.InetSocketAddress;

public class TestReceiveHandler extends AbstractReceiveHandler implements
		IReceiveHandler {

	public TestReceiveHandler(Node node) {
		super(node);
	}

	@Override
	public void handle(String msg, String srcIp) {
		String content = msg.substring(msg.indexOf(","),
				msg.lastIndexOf(",") + 1);
		String[] args = content.split(",");
		String appId = args[0];
		String ipList = args[1];
		// TODO 从IPList列表中找到是否是本地IP
		// 如果是的话 在本地执行 execute相关指令
		try {
			Process proc = Runtime.getRuntime().exec(
					"/root/apache-tomcat-6.0.39/server_test.sh " + appId);
			proc.waitFor();
			node.executeCommand(new DeployCommand(appId, Node.TEST
					| Node.SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			node.executeCommand(new DeployCommand(appId, Node.TEST
					| Node.FAILURE));
		}
	}

}