package com.wondersgroup.cloud.deployment;


public class TransportReceiveHandler extends AbstractReceiveHandler implements
		IReceiveHandler {

	public TransportReceiveHandler(Node node) {
		super(node);
	}

	@Override
	public void handle(String msg, String srcIp) {
		String content = msg.substring(msg.indexOf(","),
				msg.lastIndexOf(",") + 1);
		String[] args = content.split(",");
		final String appId = args[0];
		String ipList = args[1];
		// TODO 从IPList列表中找到是否是本地IP
		// 如果是的话 在本地执行 execute相关指令

		Thread backend = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// "/root/apache-tomcat-6.0.39/server_transport.sh " +
					// appId
					Process proc = Runtime
							.getRuntime()
							.exec("nc -l 12345 > /root/apache-tomcat-6.0.39/webapps/aaa.nima");
					StreamGobbler glober1 = new StreamGobbler(proc
							.getInputStream(), "STDOUT");
					glober1.start();
					StreamGobbler glober2 = new StreamGobbler(proc
							.getErrorStream(), "ERROR");
					glober2.start();

					proc.waitFor();
					System.out.println("长时间等待传输完成22222");
				} catch (Exception e) {
					e.printStackTrace();
					TransportReceiveHandler.this.node
							.executeCommand(new DeployCommand(appId,
									Node.TRANSPORT | Node.SUCCESS));
				}
			}
		});
		backend.setDaemon(true);
		backend.start();
		// 这里先发成功请求代表第一阶段成功
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
				| Node.FAILURE));
	}

}
