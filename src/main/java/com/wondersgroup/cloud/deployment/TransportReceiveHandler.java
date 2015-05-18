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
		// TODO ��IPList�б����ҵ��Ƿ��Ǳ���IP
		// ����ǵĻ� �ڱ���ִ�� execute���ָ��

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
					System.out.println("��ʱ��ȴ��������22222");
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
		// �����ȷ��ɹ���������һ�׶γɹ�
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
				| Node.FAILURE));
	}

}
