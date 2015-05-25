package com.wondersgroup.cloud.deployment;


public class StartReceiveHandler extends AbstractReceiveHandler implements
		IReceiveHandler {

	public StartReceiveHandler(Node node) {
		super(node);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(String msg, String srcIp) {
		String[] datas = DeployCommand.toData(msg);
		String appId = datas[0];
		String srcPath = datas[1];
		String ipList = datas[2];

		// TODO 从IPList列表中找到是否是本地IP
		// 如果是的话 在本地执行 execute相关指令
		try {
			Process proc = Runtime.getRuntime().exec(
					"/root/apache-tomcat-6.0.39/server_start.sh " + appId);
			proc.waitFor();
			node.executeCommand(new DeployCommand(appId, Node.START
					| Node.SUCCESS, srcPath, ipList));
		} catch (Exception e) {
			e.printStackTrace();
			node.executeCommand(new DeployCommand(appId, Node.START
					| Node.FAILURE, srcPath, ipList));
		}
	}

}
