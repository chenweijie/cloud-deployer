package com.wondersgroup.cloud.deployment.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.EventListener;
import com.wondersgroup.cloud.deployment.ICommand;
import com.wondersgroup.cloud.deployment.INodeListener;
import com.wondersgroup.cloud.deployment.Node;
import com.wondersgroup.cloud.deployment.ScheduleDeployCommand;
import com.wondersgroup.cloud.deployment.file.FileServer;

public final class ApplicationServiceImpl implements ApplicationService,
		INodeListener {

	private Log logger = LogFactory.getLog(ApplicationServiceImpl.class);

	private Node node;

	private IAppStatisticService appStatusService;

	public ApplicationServiceImpl() {
		this.init();
	}

	@Override
	public boolean deploy(String appId, Map extraParams) {
		// TODO: 校验是否worker服务器都已经就位
		// validateApp
		logger.info("aaaaaa1----"
				+ Node.runStateOf(this.appStatusService.getStatus(appId)));
		logger.info("aaaaaa2----"
				+ (Node.compareOf(
						Node.runStateOf(this.appStatusService.getStatus(appId)),
						Node.DEPLOY) >= 0));
		if (Node.runStateOf(this.appStatusService.getStatus(appId)) == 0
				|| Node.compareOf(
						Node.runStateOf(this.appStatusService.getStatus(appId)),
						Node.DEPLOY) >= 0) {
			ICommand command = new DeployCommand(appId, Node.DEPLOY,
					extraParams);
			node.executeCommand(command);
			return true;
		}
		return false;
	}

	@Override
	public boolean scheduleDeploy(String appId, Map extraParams) {
		// TODO: 校验是否worker服务器都已经就位
		// validateApp
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = sdf.parse(String.valueOf(extraParams
					.get("deployStart")));
			ICommand command = new ScheduleDeployCommand(appId,
					Node.DEPLOY_SCHEDULE, extraParams, startDate);
			// node.executeCommand(command);
			node.fireNodeEvent(command.toString(), node.getIp(), command);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return true;
	}

	private void init() {
		// 对外访问入口
		this.node = new Node();
		appStatusService = new ApplicationStatisticListener(node);
		node.registerNodeListener((INodeListener) appStatusService);
		node.registerNodeListener(this);
		node.registerNodeListener(new FileServer(node));
		node.registerNodeListener(new BackendScheduler(node));
		node.run();

		ICommand init_command = new DeployCommand(Node.INIT);
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
				&& (Node.runStateOf(node.selectKey(msg)) == Node.DEPLOY)) {
			int currentState = Node.DEPLOY;

			String[] datas = DeployCommand.toData(msg);
			String appId = datas[0];
			String srcPath = datas[1];
			String ipList = datas[2];
			int nextState = Node.incrementState(currentState);

			logger.info("state record:1::" + appId);
			logger.info("state record:2::" + Node.debugState(nextState));
			logger.info("state record:3::" + srcPath);
			logger.info("state record:4::" + ipList);
			ICommand command = new DeployCommand(appId, nextState, srcPath,
					ipList);
			node.executeCommand(command);
		}
		logger.info("service impl:1_" + (srcIp == node.getIp()));
		logger.info("service impl:1_"
				+ Node.debugState((Node.runStateOf(node.selectKey(msg)))));
		if (srcIp == node.getIp()
				&& (Node.runStateOf(node.selectKey(msg)) == Node.NEXT)) {
			int currentState = Integer.valueOf(String.valueOf(params[0]));
			logger.info("state record:::" + Node.debugState(currentState));
			String[] datas = DeployCommand.toData(msg);
			String appId = datas[0];
			String srcPath = datas[1];
			String ipList = datas[2];
			if (evenMap.containsKey(Node.runStateOf(currentState))) {
				evenMap.get(Node.runStateOf(currentState)).execute(appId);
				evenMap.remove(Node.runStateOf(currentState));
			}
			
			if (Node.isGoon(currentState)) {
				int nextState = Node.incrementState(currentState);

				logger.info("service impl:1_" + Node.debugState(nextState));
				ICommand command = new DeployCommand(appId, nextState, srcPath,
						ipList);
				node.executeCommand(command);
			}
		}
	}

	private Map<Integer, EventListener> evenMap = new HashMap<Integer, EventListener>(
			2);

	@Override
	public void registerEventListener(int event, EventListener eventListener) {
		evenMap.put(event, eventListener);
	}

}
