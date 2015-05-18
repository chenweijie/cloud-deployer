package com.wondersgroup.cloud.deployment.bootstrap;

import java.net.UnknownHostException;
import java.util.Scanner;

import com.wondersgroup.cloud.deployment.service.ApplicatioinServiceImpl;
import com.wondersgroup.cloud.deployment.service.ApplicationService;

/*
 * ����� Ҳ������ν�����Ķ�
 * 1.���ܵ�deployee�ļ�������
 * 2.��׼����
 * 3.�����ⲿָ�Ȼ����ָ�����̨deployee
 * 4.����ָ�������Ƚ��аѿأ��������������ƣ�
 */
public class Deployer {

	public static void main(String[] args) throws UnknownHostException {
		
		// // TODO: �����֤��һϵ�з���ָ���
		// ICommand command = new PlainCommand("close app1");
		// node.executeCommand(command);
		// command = new PlainCommand("delete app1");
		// node.executeCommand(command);
		// command = new PlainCommand("transport app1");
		// node.executeCommand(command);
		// command = new PlainCommand("start app1");
		// node.executeCommand(command);
		// command = new PlainCommand("test app1");
		// node.executeCommand(command);

		final ApplicationService applicationService = new ApplicatioinServiceImpl();
		Thread deamonAppStatus = new Thread(new Runnable(){
			@Override
			public void run() {
				// ��Ҳû�취
				int status = applicationService.getAppStatus("app1");
			}});
		deamonAppStatus.setDaemon(true);
		deamonAppStatus.start();

		while (true) {
			Scanner sc = new Scanner(System.in);
			System.out.print("Please enter a cmd : ");
			String cmmd = sc.nextLine();
			System.out.println("Your input is : " + cmmd);
			String[] _args = cmmd.split(" ");
			applicationService.deploy(_args[1]);
		}
	}

}
