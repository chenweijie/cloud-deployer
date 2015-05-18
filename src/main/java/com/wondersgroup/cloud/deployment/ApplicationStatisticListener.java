package com.wondersgroup.cloud.deployment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.wondersgroup.cloud.deployment.service.IAppStatisticService;

/**
 * ��¼״̬���� app1 {serverA: 1close, serverB: 1close, serverC: 0close} app2
 * {serverD: 1start, serverE: 1start, serverF: 0start}
 */
public class ApplicationStatisticListener implements INodeListener,
		IAppStatisticService {

	private Node node;

	private Map<String, Map<String, Integer>> appStatus = new HashMap<String, Map<String, Integer>>(
			2);

	public ApplicationStatisticListener(Node node) {
		this.node = node;
	}

	@Override
	public int getStatus(String appId) {
		// ��������� �����һ��server��0״̬ ��ô�ʹ����app���״̬Ϊ0 ,���� �ر�״̬ʧ�ܣ������͹رճɹ�
		if (this.appStatus.containsKey(appId)) {
			Iterator<Integer> iter = this.appStatus.get(appId).values()
					.iterator();
			int result = 0;
			while (iter.hasNext()) {
				int status = iter.next();
				if (status == Node.DEPLOY) {
					return status;
				} else {
					int realOne = Node.stateDetailOf(status);
					result = Node.runStateOf(status);
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
		// ��ʼ����һ��
		if (Node.runStateOf(node.selectKey(msg)) == Node.DEPLOY) {
			ICommand command = (ICommand) params[0];
			String appId = command.getAppId();
			String[] ips = command.getDoingIPs();
			this.updateStatus(appId, ips, Node.DEPLOY);
		} else if (node.getIp() != srcIp) {// ȷ������״̬����
			// ������� ���ǵ�������ֱ�ӷ��������Ϣ
			int key = node.selectKey(msg);
			int status = key;// Node.runStateOf(key);
			String content = msg.substring(msg.indexOf(","),
					msg.lastIndexOf(",") + 1);
			String[] args = content.split(",");
			String appId = args[0];
			this.updateStatus(appId, new String[] { srcIp }, status);
		}
	}

	private void updateStatus(String appId, String[] ips, int status) {
		if (!this.appStatus.containsKey(appId)) {
			this.appStatus.put(appId, new HashMap<String, Integer>(2));
		}
		for (String ip : ips) {
			Map serverStatus = this.appStatus.get(appId);
			serverStatus.put(ip, status);
		}

		if (ips.length == 1 && status != Node.DEPLOY) {
			// ���ÿ��������״̬���Ƿ���Դ����µ�״̬��
			Iterator<Integer> iter = this.appStatus.get(appId).values()
					.iterator();
			int finalStatus = Node.SUCCESS;
			int state = Node.CLOSE;
			while (iter.hasNext()) {
				int _status = iter.next();
				if (status != Node.DEPLOY) {
					int realOne = Node.stateDetailOf(_status);
					finalStatus = finalStatus & realOne;
					state = Node.runStateOf(_status);
				} else {
					finalStatus = 0;
					break;
				}
			}
			if (finalStatus == Node.SUCCESS) {
				// ������һ���׶�
				// ��������׶�������Ϣ
				ICommand close_command = new DeployCommand(appId, Node.NEXT);
				node.fireNodeEvent(close_command.toString(), node.getIp(),
						state);
				// fireEvent OK APP
			} else {
				// fireEvent Failure APP
				System.out.println(appId + "---" + state
						+ "--��Ŀ���ڻ��޷���ȫ�ж�Ϊ�׶���ʧ��");
			}
		}
	}

}
