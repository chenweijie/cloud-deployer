package com.wondersgroup.cloud.deployment.file;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.Node;

@ChannelPipelineCoverage("one")
public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	private Log logger = LogFactory.getLog(HttpResponseHandler.class);
	private volatile boolean readingChunks;
	private File downloadFile;
	private FileOutputStream fOutputStream = null;
	private Node node;
	private String appId;
	private String ipList;
	private String srcPath;

	public HttpResponseHandler(Node node, String appId, String srcPath,
			String ipList) {
		this.node = node;
		this.appId = appId;
		this.srcPath = srcPath;
		this.ipList = ipList;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		// logger.info("receive msg...");
		if (e.getMessage() instanceof HttpResponse) {
			DefaultHttpResponse httpResponse = (DefaultHttpResponse) e
					.getMessage();
			String fileName = httpResponse.getHeader("Content-Disposition")
					.substring(20);
			// downloadFile = new File(System.getProperty("user.dir")+
			// File.separator + "download" + fileName);
			downloadFile = new File("/root/apache-tomcat-6.0.39/webapps/" // 
					+ fileName);
			httpResponse.getContent().readableBytes();
			readingChunks = httpResponse.isChunked();

			if (!readingChunks && httpResponse.getContent().readableBytes() > 0) {
				// 直接传输文件了
				ChannelBuffer buffer = httpResponse.getContent();
				if (fOutputStream == null) {
					fOutputStream = new FileOutputStream(downloadFile);
				}
				while (buffer.readable()) {
					byte[] dst = new byte[buffer.readableBytes()];
					buffer.readBytes(dst);
					fOutputStream.write(dst);
				}
				fOutputStream.flush();
				if (node != null) {
					node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
							| Node.SUCCESS, this.srcPath, this.ipList));
				}
			}
		} else {
			HttpChunk httpChunk = (HttpChunk) e.getMessage();
			if (!httpChunk.isLast()) {
				ChannelBuffer buffer = httpChunk.getContent();
				if (fOutputStream == null) {
					fOutputStream = new FileOutputStream(downloadFile);
				}
				while (buffer.readable()) {
					byte[] dst = new byte[buffer.readableBytes()];
					buffer.readBytes(dst);
					fOutputStream.write(dst);
				}
			} else {
				readingChunks = false;
				if (node != null) {
					node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
							| Node.SUCCESS, this.srcPath, this.ipList));
				}
			}
			fOutputStream.flush();
		}
		if (!readingChunks) {
			if (fOutputStream != null) {
				fOutputStream.close();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		System.out.println(e.getCause());
		if (node != null) {
			node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
					| Node.FAILURE, this.srcPath, this.ipList));
		}
	}
}
