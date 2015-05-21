package com.wondersgroup.cloud.deployment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.wondersgroup.cloud.deployment.utils.URL;

public class Node {

	private String ip;// 使用绑定的网卡对应IP 替代
	private NetworkCommander commander;
	private Map<Integer, IReceiveHandler> handlerMap = new HashMap<Integer, IReceiveHandler>(
			4);

	public Node() {
		try {
			Properties props = new Properties();
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("node.properties"));
			URL url = URL.valueOf(props.getProperty("register.url"));

			ip = props.getProperty("network.bind.ip.name");
			if (ip == null || ip.equals("")) {
				String networkInterface = props
						.getProperty("network.bind.interface.name");
				ip = this.getBindIP(networkInterface);
			}
			commander = new MutilGroupNetworkCommander(url, this);
		} catch (IOException ex) {
			throw new DeployException("初始化异常:" + ex.getMessage(), ex);
		}
	}

	private String getBindIP(String networkInterface) throws SocketException {
		Enumeration<NetworkInterface> netInterfaces = NetworkInterface
				.getNetworkInterfaces();
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = netInterfaces.nextElement();
			if (networkInterface.equals(ni.getName())) {
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					// 直接返回绑定的第一个IP
					String bindIp = ips.nextElement().getHostAddress();
					System.out.println("bindIp:::" + bindIp);
					return bindIp;
				}
			}
		}
		throw new DeployException("没有匹配的IP地址");
	}

	public void run() {
		Thread deamon = new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] buf = new byte[1024];
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				// 不停接受 外部传来的消息，交给注册的处理器处理
				while (true) {
					commander.acceptMsg(recv);
				}
			}

		});
		deamon.setName("Node BackEnd Thread");
		deamon.setDaemon(true);
		deamon.start();
	}

	public void executeCommand(ICommand command) {
		// 2 .如果没有注册join的处理器 就认为是 工作机 可以继续
		// this.workerIPs.size() == 0 &&
		if (handlerMap.containsKey(Node.JOIN)) {
			return;
		}
		this.fireNodeEvent(command.toString(), this.ip, command);
		commander.sendMsg(this.ip, command);
	}

	public void registerReceiveHandler(int key, IReceiveHandler receiveHandler) {
		handlerMap.put(key, receiveHandler);
	}

	// 换成2进制 剩下4位做状态更替使用 上面32-4全部做状态名称；
	public static final int STATUS_BITS = Integer.SIZE - 4;
	public static final int STATUS_CHANGE = (1 << STATUS_BITS) - 1;

	public static final int INIT = 0 << STATUS_BITS;
	public static final int JOIN = 1 << STATUS_BITS;
	public static final int NEXT = 2 << STATUS_BITS;
	// 这是一个整体
	public static final int DEPLOY = 3 << STATUS_BITS;
	public static final int CLOSE = 4 << STATUS_BITS;
	public static final int DELETE = 5 << STATUS_BITS;
	public static final int TRANSPORT = 6 << STATUS_BITS;
	public static final int START = 7 << STATUS_BITS;
	public static final int TEST = 8 << STATUS_BITS;
	// 定时任务
	public static final int DEPLOY_SCHEDULE = 9 << STATUS_BITS;
	
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	

	public static int runStateOf(int c) {
		return c & ~STATUS_CHANGE;
	}

	public static int stateDetailOf(int c) {
		return c & STATUS_CHANGE;
	}

	public void changeToSuccess(int c) {
		c &= SUCCESS;
	};

	public void changeToFAILURE(int c) {
		c &= FAILURE;
	};

	public int selectKey(String msg) {
		return Integer.valueOf(msg.substring(0, msg.indexOf(",")));
	}

	public void handleReceive(String msg, InetSocketAddress socketAddress,
			String srcIp) {
		// 如果远端IP与本身IP是一样的 那就不做处理
		if (srcIp.equals(this.ip)) {
			return;
		}

		int _key = this.selectKey(msg);
		IReceiveHandler handler = handlerMap.get(_key);
		handler.handle(msg, srcIp);
		this.fireNodeEvent(msg, srcIp, _key);
	}

	public void fireNodeEvent(String msg, String srcIp, Object... params) {
		for (INodeListener listener : nodeListeners) {
			listener.fireNodeEvent(msg, srcIp, params);
		}
	}

	private List<INodeListener> nodeListeners = new ArrayList<INodeListener>(2);

	public void registerNodeListener(INodeListener nodeListener) {
		nodeListeners.add(nodeListener);
	}

	public String getIp() {
		return ip;
	}

}
