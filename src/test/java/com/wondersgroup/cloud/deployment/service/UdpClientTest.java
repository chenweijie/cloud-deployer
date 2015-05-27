package com.wondersgroup.cloud.deployment.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.wondersgroup.cloud.deployment.utils.URL;

public class UdpClientTest {

	public static void main(String[] args) throws IOException {
		InetAddress mutilcastAddress = null;
		MulticastSocket mutilcastSocket = null;
		URL url = URL.valueOf("multicast://10.1.255.255:1238");
		mutilcastAddress = InetAddress.getByName(url.getHost());
		mutilcastSocket = new MulticastSocket(url.getPort());
		mutilcastSocket.setLoopbackMode(false);
		mutilcastSocket.joinGroup(mutilcastAddress);

		String msg = "11111";
		DatagramPacket hi = new DatagramPacket(msg .getBytes(), msg.length(),
				mutilcastAddress, mutilcastSocket.getLocalPort());
		mutilcastSocket.send(hi);
	}

}
