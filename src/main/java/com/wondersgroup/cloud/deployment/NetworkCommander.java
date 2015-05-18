package com.wondersgroup.cloud.deployment;

import java.net.DatagramPacket;

public interface NetworkCommander {

	void acceptMsg(DatagramPacket recv);

	void sendMsg(String ip, ICommand command);

}
