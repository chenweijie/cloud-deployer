package com.wondersgroup.cloud.deployment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;

import com.wondersgroup.cloud.deployment.utils.StringUtils;
import com.wondersgroup.cloud.deployment.utils.URL;

public class MutilGroupNetworkCommander implements NetworkCommander {

	private Log logger = LogFactory.getLog(MutilGroupNetworkCommander.class);

	private URL url;

	private InetAddress mutilcastAddress;

	private MulticastSocket mutilcastSocket;

	private Node node;

	public MutilGroupNetworkCommander(URL url, Node node) {
		if (!isMulticastAddress(url.getHost())) {
			throw new IllegalArgumentException("Invalid multicast address "
					+ url.getHost() + ", scope: 224.0.0.0 - 239.255.255.255");
		}
		this.url = url;
		this.node = node;
		try {
			mutilcastAddress = InetAddress.getByName(url.getHost());
			mutilcastSocket = new MulticastSocket(url.getPort());
			mutilcastSocket.setLoopbackMode(false);
			mutilcastSocket.joinGroup(mutilcastAddress);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void sendMsg(String ip, ICommand command) {
		logger.info("send msg:::" + ip);
		String msg = command.toString() + "," + ip;
		logger.info("send msg detail:::" + command.getClass().getSimpleName());
		logger.info("send msg detail:::" + msg);
		DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
				mutilcastAddress, mutilcastSocket.getLocalPort());
		try {
			mutilcastSocket.send(hi);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private static boolean isMulticastAddress(String ip) {
		int i = ip.indexOf('.');
		if (i > 0) {
			String prefix = ip.substring(0, i);
			if (StringUtils.isInteger(prefix)) {
				int p = Integer.parseInt(prefix);
				return p >= 224 && p <= 239;
			}
		}
		return false;
	}

	@Override
	public void acceptMsg() {
		try {
			byte[] buf = new byte[1024];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			mutilcastSocket.receive(recv);
			logger.info("receive data start==================================");
			logger.info("src host:::"
					+ ((InetSocketAddress) recv.getSocketAddress())
							.getHostString());
			String rawContent = new String(recv.getData()).trim();
			// 268435456,10.1.65.105
			logger.info("raw data:::" + rawContent);
			String srcIp = rawContent
					.substring(rawContent.lastIndexOf(",") + 1);

			String msg = rawContent.substring(0, rawContent.lastIndexOf(","));
			logger.info("msg:::" + msg);
			if (msg.indexOf(",") > 0) {
				String[] _datas = DeployCommand.toData(msg);
				logger.info("state:1__"
						+ Node.debugState(Integer.valueOf(msg.substring(0,
								msg.indexOf(",")))));
				String ipList = _datas[2];

				logger.info("state:1.1__" + ipList);
				logger.info("state:1.2__" + node.getIp());
				JSONArray jsonArray = JSONArray.fromObject(ipList);
				// 客户端
				logger.info("state:1.3__" + (node.isClient()));
				logger.info("state:1.4__" + (jsonArray.contains(node.getIp())));
				if (node.isClient() && jsonArray.contains(node.getIp())) {
					// 交给node中注册的指令处理类实现
					node.handleReceive(msg,
							(InetSocketAddress) recv.getSocketAddress(), srcIp);
				} else if (!node.isClient()) {
					// 服务端
					logger.info("state:ssssssssssssssssss");
					node.handleReceive(msg,
							(InetSocketAddress) recv.getSocketAddress(), srcIp);
				}
			} else {
				logger.info("state:2__" + Node.debugState(Integer.valueOf(msg)));
				// join command
				node.handleReceive(msg,
						(InetSocketAddress) recv.getSocketAddress(), srcIp);
			}
			logger.info("receive data end==================================");
		} catch (Exception e) {
			// 线程内部打日志吧
			e.printStackTrace();
		}
	}

	public URL getUrl() {
		return url;
	}

}
