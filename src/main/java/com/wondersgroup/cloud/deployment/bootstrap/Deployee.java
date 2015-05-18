package com.wondersgroup.cloud.deployment.bootstrap;

import java.net.SocketException;

import com.wondersgroup.cloud.deployment.CloseReceiveHandler;
import com.wondersgroup.cloud.deployment.DeleteReceiveHandler;
import com.wondersgroup.cloud.deployment.ICommand;
import com.wondersgroup.cloud.deployment.Node;
import com.wondersgroup.cloud.deployment.PlainCommand;
import com.wondersgroup.cloud.deployment.StartReceiveHandler;
import com.wondersgroup.cloud.deployment.TestReceiveHandler;
import com.wondersgroup.cloud.deployment.TransportReceiveHandler;

/**
 * 工作端 1. 申请加盟节点 可以开始工作 2. 已经加盟节点（独占方式，不会同时服务多个deployer）
 */
public class Deployee {

	public static void main(String[] args) throws SocketException {
		// 对外访问入口
		Node node = new Node();
		// 代表每个阶段的开始
		node.registerReceiveHandler(Node.CLOSE, new CloseReceiveHandler(node));
		node.registerReceiveHandler(Node.DELETE, new DeleteReceiveHandler(node));
		node.registerReceiveHandler(Node.TRANSPORT, new TransportReceiveHandler(node));
		node.registerReceiveHandler(Node.START, new StartReceiveHandler(node));
		node.registerReceiveHandler(Node.TEST, new TestReceiveHandler(node));
		node.run();

		ICommand command = new PlainCommand(Node.JOIN);
		node.executeCommand(command);

		while (true) {

		}
	}

}
