package com.wondersgroup.cloud.deployment;


public class DeleteReceiveHandler extends AbstractReceiveHandler implements
		IReceiveHandler {

	public DeleteReceiveHandler(Node node) {
		super(node);
	}

	@Override
	public void handle(String msg, String srcIp) {
		String content = msg.substring(msg.indexOf(","),
				msg.lastIndexOf(",") + 1);
		String[] args = content.split(",");
		String appId = args[0];
		String ipList = args[1];
		try {
			Process proc = Runtime.getRuntime().exec(
					"/root/apache-tomcat-6.0.39/server_delete.sh " + appId);
			proc.waitFor();
			node.executeCommand(new DeployCommand(appId, Node.DELETE
					| Node.SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			node.executeCommand(new DeployCommand(appId, Node.DELETE
					| Node.FAILURE));
		}
	}

}
