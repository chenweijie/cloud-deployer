package com.wondersgroup.cloud.deployment.service;

import com.wondersgroup.cloud.deployment.ApplicationStatisticListener;
import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.ICommand;
import com.wondersgroup.cloud.deployment.INodeListener;
import com.wondersgroup.cloud.deployment.Node;

public final class ApplicatioinServiceImpl implements ApplicationService,
		INodeListener {

	private Node node;

	private IAppStatisticService appStatusService;

	public ApplicatioinServiceImpl() {
		this.init();
	}

	@Override
	public boolean deploy(String appId) {
		// TODO: У���Ƿ�worker���������Ѿ���λ
		// node.validate

		ICommand command = new DeployCommand(appId, Node.DEPLOY);
		node.executeCommand(command);
		return true;
	}

	private void init() {
		// ����������
		this.node = new Node();
		// ע����Ϣ���ܵĴ���ʵ�� 1�����ܼ�������2���õ�����ָ�����Ϣ
		// node.registerReceiveHandler(node.JOIN, new JoinReceiveHandler(node));
		// node.registerReceiveHandler(node.CLOSE | node.SUCCESS,
		// new CloseOKReceiveHandler(node));
		// node.registerReceiveHandler(node.CLOSE | node.FAILURE,
		// new CloseFailureReceiveHandler(node));
		// node.registerReceiveHandler(node.DELETE | node.SUCCESS,
		// new DeleteOKReceiveHandler(node));
		// node.registerReceiveHandler(node.DELETE | node.FAILURE,
		// new DeleteFailureReceiveHandler(node));
		// node.registerReceiveHandler(node.TRANSPORT | node.SUCCESS,
		// new TransportOKReceiveHandler(node));
		// node.registerReceiveHandler(node.TRANSPORT | node.FAILURE,
		// new TransportFailureReceiveHandler(node));
		// node.registerReceiveHandler(node.START | node.SUCCESS,
		// new StartOKReceiveHandler(node));
		// node.registerReceiveHandler(node.START | node.FAILURE,
		// new StartFailureReceiveHandler(node));
		// node.registerReceiveHandler(node.TEST | node.SUCCESS,
		// new TestOKReceiveHandler(node));
		// node.registerReceiveHandler(node.TEST | node.FAILURE,
		// new TestFailureReceiveHandler(node));

		appStatusService = new ApplicationStatisticListener(node);
		node.registerNodeListener((INodeListener) appStatusService);
		node.registerNodeListener(this);
		node.run();
	}

	@Override
	public int getAppStatus(String appId) {
		return appStatusService.getStatus(appId);
	}

	@Override
	public void fireNodeEvent(String msg, String srcIp, Object... params) {
		// ���� statis ������close״̬��Ȼ�����ȫ�����ܸ������������͵�״̬����
		// statistic��������״̬ͳ�ƣ�����������״̬����
		// ÿ�η�������Ϣ���� serverA 0close , serverB 1close �����ﶼ��ȥ��statistic��������ѯ
		// ֻ��״̬ȫ����ȷ ������ܼ�������ȥ��Ҳ����˵����
		// ����ǣ�浽�Ⱥ����⣺ �����service�Ƚӵ����󣬶�statistic��ӵ�������ôservice
		// ֻ��һ�飬�ͻ���©����������״̬��Ϣ��
		// ����Ϊ�˽�����������ֻ���ǻ��statitic�������б���״̬��Ϣ����fireNodeEvent���������Լ�������Ϊ׼--->�ǵ��������ﻹ��Ҫά��һ��״̬��������
		// �����ְ취������statistic ������������״̬�󣬷����µ��¼���Ȼ�������ﲶ�� ����ִ��
		if (srcIp == node.getIp()
				&& (Node.runStateOf(node.selectKey(msg)) == Node.NEXT)) {
			int currentState = Integer.valueOf(String.valueOf(params[0]));
			if (currentState < Node.TEST) {
				String content = msg.substring(msg.indexOf(","),
						msg.lastIndexOf(",") + 1);
				String[] args = content.split(",");
				String appId = args[0];

				int nextState = currentState + 1;
				ICommand command = new DeployCommand(appId, nextState);
				node.executeCommand(command);
			}
		}
	}
}
