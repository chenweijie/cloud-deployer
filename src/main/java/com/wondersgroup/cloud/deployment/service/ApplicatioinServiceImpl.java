package com.wondersgroup.cloud.deployment.service;

import com.wondersgroup.cloud.deployment.ApplicationStatisticListener;
import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.ICommand;
import com.wondersgroup.cloud.deployment.INodeListener;
import com.wondersgroup.cloud.deployment.Node;
import com.wondersgroup.cloud.deployment.file.FileServer;

public final class ApplicatioinServiceImpl implements ApplicationService,
		INodeListener {

	private Node node;

	private IAppStatisticService appStatusService;

	public ApplicatioinServiceImpl() {
		this.init();
	}

	@Override
	public boolean deploy(String appId) {
		// TODO: 校验是否worker服务器都已经就位
		// node.validate

		ICommand command = new DeployCommand(appId, Node.DEPLOY);
		node.executeCommand(command);
		return true;
	}

	private void init() {
		// 对外访问入口
		this.node = new Node();
		// 注册消息接受的处理实现 1，接受加盟请求，2，得到各种指令反馈信息
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
		node.registerNodeListener(new FileServer(node));
		node.run();

		ICommand init_command = new DeployCommand("", Node.INIT);
		node.fireNodeEvent(init_command.toString(), node.getIp(), init_command);
	}

	@Override
	public int getAppStatus(String appId) {
		return appStatusService.getStatus(appId);
	}

	@Override
	public void fireNodeEvent(String msg, String srcIp, Object... params) {
		// 首先 statis 服务都是close状态，然后后面全部接受各个服务器发送的状态数据
		// statistic那里是做状态统计，而这里是做状态更替
		// 每次服务发来信息比如 serverA 0close , serverB 1close ，这里都会去到statistic里面做查询
		// 只有状态全部正确 后面才能继续做下去，也就是说这里
		// ，会牵涉到先后问题： 如果是service先接到请求，而statistic后接到请求，那么service
		// 只查一遍，就会遗漏服务器最新状态信息。
		// 所以为了解决这种情况，只有是活得statitic服务器列表，而状态信息根据fireNodeEvent传过来的自己做解析为准--->那等于在这里还需要维护一张状态表。。。。
		// 还有种办法就是由statistic 服务器更新完状态后，发起新的事件，然后再这里捕获到 继续执行
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
