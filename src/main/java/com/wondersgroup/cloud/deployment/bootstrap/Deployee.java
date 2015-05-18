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
 * ������ 1. ������˽ڵ� ���Կ�ʼ���� 2. �Ѿ����˽ڵ㣨��ռ��ʽ������ͬʱ������deployer��
 */
public class Deployee {

	public static void main(String[] args) throws SocketException {
		// ����������
		Node node = new Node();
		// ����ÿ���׶εĿ�ʼ
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
