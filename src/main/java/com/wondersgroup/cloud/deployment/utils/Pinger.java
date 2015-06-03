package com.wondersgroup.cloud.deployment.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Pinger {

	private Log logger = LogFactory.getLog(Pinger.class);

	/** * 要ping的主机 */
	private String remoteIpAddress;
	/** * 设置ping的次数 */
	private final int pingTimes;
	/** * 设置超时 */
	private int timeOut;

	/** * 构造函数 * * @param remoteIpAddress * @param pingTimes * @param timeOut */
	public Pinger(String remoteIpAddress, int pingTimes, int timeOut) {
		super();
		this.remoteIpAddress = remoteIpAddress;
		this.pingTimes = pingTimes;
		this.timeOut = timeOut;
	}

	/** * 测试是否能ping通 * @param server * @param timeout * @return */
	public boolean isReachable() {
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime();
		// 将要执行的ping命令,此命令是windows格式的命令
		String pingCommand = "ping " + remoteIpAddress + " -c " + pingTimes;// +
																			// " -W "
																			// +
																			// timeOut
		// String pingCommand = "ping " + remoteIpAddress + " -n " + pingTimes +
		// " -w " + timeOut;
		try {
			// 执行命令并获取输出
			logger.info("Pinger:::" + pingCommand);
			Process p = r.exec(pingCommand);
			if (p == null) {
				return false;
			}
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
			int connectedCount = 0;
			String line = null;
			while ((line = in.readLine()) != null) {
				connectedCount += getCheckResult(line);
			}
			// 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
			return connectedCount == pingTimes;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage(), ex);
			// 出现异常则返回假
			return false;
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	/** * 若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0. * @param line * @return */
	private int getCheckResult(String line) {
		logger.info("Pinger detail:::" + line);
		// "(\\d+ms)(\\s+)(TTL=\\d+)"
		Pattern pattern = Pattern.compile("(TTL=\\d+)(\\s+)(TIME=)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			return 1;
		}
		return 0;
	}

	public static void main(String[] args) {
		Pinger p = new Pinger("10.1.65.103", 1, 5000);
		System.out.println(p.isReachable());
	}

}
