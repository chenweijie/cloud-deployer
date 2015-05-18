package com.wondersgroup.cloud.deployment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import com.wondersgroup.cloud.deployment.utils.StringUtils;
import com.wondersgroup.cloud.deployment.utils.URL;

public class MutilGroupNetworkCommander implements NetworkCommander {

	private URL url;

	private InetAddress mutilcastAddress;

	private MulticastSocket mutilcastSocket;

	private Node node;

	public MutilGroupNetworkCommander(URL url, Node node) {
		if (!isMulticastAddress(url.getHost())) {
			throw new IllegalArgumentException("Invalid multicast address " + url.getHost()
					+ ", scope: 224.0.0.0 - 239.255.255.255");
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
		String msg = command.toString()+ "," +ip;
		DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), mutilcastAddress,
				mutilcastSocket.getLocalPort());
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
	public void acceptMsg(DatagramPacket recv) {
		try {
			mutilcastSocket.receive(recv);
			System.out.println("收到数据:::" + new String(recv.getData()).trim() + "--" + ((InetSocketAddress)recv.getSocketAddress()).getHostString());
			String rawContent = new String(recv.getData()).trim();
			String srcIp = rawContent.substring(rawContent.lastIndexOf(",") + 1);
			// 交给node中注册的指令处理类实现
			node.handleReceive(rawContent.substring(0, rawContent.indexOf(",")), (InetSocketAddress)recv.getSocketAddress(), srcIp);
		} catch (Exception e) {
			// 线程内部打日志吧
			e.printStackTrace();
		}
	}

	public URL getUrl() {
		return url;
	}

}
