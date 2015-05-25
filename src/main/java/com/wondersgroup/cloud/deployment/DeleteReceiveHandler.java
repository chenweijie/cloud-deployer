package com.wondersgroup.cloud.deployment;

public class DeleteReceiveHandler extends AbstractReceiveHandler implements
		IReceiveHandler {

	public DeleteReceiveHandler(Node node) {
		super(node);
	}

	@Override
	public void handle(String msg, String srcIp) {
		String[] datas = DeployCommand.toData(msg);
		String appId = datas[0];
		String srcPath = datas[1];
		String ipList = datas[2];
		try {
			Process proc = Runtime.getRuntime().exec(
					"/root/apache-tomcat-6.0.39/server_delete.sh " + appId);
			proc.waitFor();
			node.executeCommand(new DeployCommand(appId, Node.DELETE
					| Node.SUCCESS, srcPath, ipList));
		} catch (Exception e) {
			e.printStackTrace();
			node.executeCommand(new DeployCommand(appId, Node.DELETE
					| Node.FAILURE, srcPath, ipList));
		}
	}

}
