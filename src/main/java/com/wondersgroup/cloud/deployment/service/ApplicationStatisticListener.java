package com.wondersgroup.cloud.deployment.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.ICommand;
import com.wondersgroup.cloud.deployment.INodeListener;
import com.wondersgroup.cloud.deployment.Node;

/**
 * 记录状态如下 app1 {serverA: 1close, serverB: 1close, serverC: 0close} app2
 * {serverD: 1start, serverE: 1start, serverF: 0start}
 */
public class ApplicationStatisticListener implements INodeListener,
		IAppStatisticService {

	private Log logger = LogFactory.getLog(ApplicationStatisticListener.class);

	private Node node;

	private Map<String, Map<String, Integer>> appStatus = new HashMap<String, Map<String, Integer>>();
	
	public ApplicationStatisticListener(Node node) {
		this.node = node;
	}

	@Override
	public int getStatus(String appId) {
		// 这里规则是 如果有一个server是0状态 那么就代表该app这个状态为0 ,比如 关闭状态失败，其它就关闭成功
		// logger.info("shangmian:::" + this.appStatus);
		if (this.appStatus.containsKey(appId)) {
			Iterator<Integer> iter = this.appStatus.get(appId).values()
					.iterator();
			int result = 0;
			while (iter.hasNext()) {
				int status = iter.next();
				if (status == Node.DEPLOY) {
					return status;
				} else {
					logger.info("nimei11--" + Node.debugState(status));
					int realOne = Node.stateDetailOf(status);
					result = status;// Node.runStateOf(status);

					logger.info("nimei22--" + Node.debugState(result));
					logger.info("nimei33--" + realOne);
					if (realOne == Node.FAILURE) {
						return status;
					}
				}
			}
			return result;
		}
		return 0;
	}

	@Override
	public void fireNodeEvent(String msg, String srcIp, Object... params) {
		// 初始化那一下
		logger.info("fire node msg: "
				+ Node.debugState(Node.runStateOf(node.selectKey(msg))));

		if (Node.runStateOf(node.selectKey(msg)) == Node.DEPLOY) {
			ICommand command = (ICommand) params[0];
			String appId = command.getAppId();
			String[] ips = command.getDoingIPs();

			// 初始化下 这里直接把对应的值给晴空掉
			logger.info("init---------------------" + appId);
			this.resetApp(appId);
			this.updateStatus(appId.trim(), ips, Node.DEPLOY, null, null);
		} else if (node.getIp() != srcIp
				&& Node.runStateOf(node.selectKey(msg)) != Node.JOIN) {
			// 加盟节点不做业务信息记录
			// 确保不是状态更替 其它情况 都是单服务器直接反馈结果信息
			int key = node.selectKey(msg);
			int status = key;// Node.runStateOf(key);

			String[] datas = DeployCommand.toData(msg);
			String appId = datas[0];
			String srcPath = datas[1];
			String ipList = datas[2];

			this.updateStatus(appId.trim(), new String[] { srcIp }, status, srcPath,
					ipList);
		}
	}

	private void resetApp(String appId) {
		Map<String, Integer> serverStatus = this.appStatus.get(appId);
		if (serverStatus != null) {
			Iterator<String> servers = serverStatus.keySet().iterator();
			while (servers.hasNext()) {
				String server = servers.next();
				serverStatus.put(server, 0);
			}
		}
	}

	private void updateStatus(String appId, String[] ips, int status,
			String srcPath, String ipList) {
		if (!this.appStatus.containsKey(appId)) {
			this.appStatus.put(appId, new HashMap<String, Integer>());
		}
		for (String ip : ips) {
			Map<String, Integer> serverStatus = this.appStatus.get(appId);
			serverStatus.put(ip, status);
		}
		
		if (ips.length == 1 && status != Node.DEPLOY) {
			// 检查每个服务器状态看是否可以触发新的状态了
			logger.info("judge next state start=====================");
			Iterator<Integer> iter = this.appStatus.get(appId).values()
					.iterator();
			int finalStatus = Node.SUCCESS;
			int state = Node.CLOSE;
			while (iter.hasNext()) {
				int _status = iter.next();
				logger.info("judge state_" + Node.debugState(_status));
				if (status != Node.DEPLOY) {
					int realOne = Node.stateDetailOf(_status);
					finalStatus = finalStatus & realOne;
					state = Node.runStateOf(_status);
				} else {
					finalStatus = 0;
					break;
				}
			}
			logger.info("judge final state_" + finalStatus);
			if (finalStatus == Node.SUCCESS) {
				// 进入下一个阶段 发起这个阶段完结的消息
				logger.info("fire next state  success");
				ICommand close_command = new DeployCommand(appId, Node.NEXT,
						srcPath, ipList);
				node.fireNodeEvent(close_command.toString(), node.getIp(),
						state);
				// fireEvent OK APP
			} else {
				// fireEvent Failure APP
				logger.info("nothing happened fire next state failure");
			}
		}
	}

}
