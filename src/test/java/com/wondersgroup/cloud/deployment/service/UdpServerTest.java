package com.wondersgroup.cloud.deployment.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.wondersgroup.cloud.deployment.utils.URL;

public class UdpServerTest {

	public static void main(String[] args) throws IOException {
		InetAddress mutilcastAddress = null;
		MulticastSocket mutilcastSocket = null;
		URL url = URL.valueOf("multicast://224.5.255.255:1238");
		mutilcastAddress = InetAddress.getByName(url.getHost());
		mutilcastSocket = new MulticastSocket(url.getPort());
		mutilcastSocket.setLoopbackMode(false);
		mutilcastSocket.joinGroup(mutilcastAddress);

		while (true) {
			// recv
			byte[] buf = new byte[1024];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			mutilcastSocket.receive(recv);
			String rawContent = new String(recv.getData()).trim();
			System.out.println("received msg::::" + rawContent);
		}
	}

}
