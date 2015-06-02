package com.wondersgroup.cloud.deployment.file;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.junit.Before;
import org.junit.Test;

public class FileTransportTest {

	@Before
	public void openServer() {
		Thread backend = new Thread(new Runnable() {

			@Override
			public void run() {
				ServerBootstrap bootstrap = new ServerBootstrap(
						new NioServerSocketChannelFactory(
								Executors.newCachedThreadPool(),
								Executors.newCachedThreadPool()));
				bootstrap.setOption("tcpNoDelay", true);

				bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

					@Override
					public ChannelPipeline getPipeline() throws Exception {
						ChannelPipeline pipeline = pipeline();
						pipeline.addLast("decoder", new HttpRequestDecoder());
						pipeline.addLast("aggregator", new HttpChunkAggregator(
								65536));// 
						pipeline.addLast("encoder", new HttpResponseEncoder());
						pipeline.addLast("chunkedWriter",
								new ChunkedWriteHandler());
						pipeline.addLast("handler", new FileServerHandler());
						return pipeline;
					}

				});

				bootstrap.bind(new InetSocketAddress(8085));
			}
		});
		backend.setDaemon(true);
		backend.start();
	}

	@Test
	public void download() {
		HttpClient httpClient = new HttpClient(null, "app1", "127.0.0.1", 8085,
				"xxxx", "bbbb");
		ChannelFuture future = httpClient.connect();
		if (httpClient.checkFutureState(future)) {
			System.out.println("connect ok");
			HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
					HttpMethod.GET, "thunder.zip");
			ChannelFuture writeFuture = httpClient.write(request);
			if (httpClient.checkFutureState(writeFuture)) {
				System.out.println("write ok");
				// writeFuture.getChannel().close();
				// httpClient.close();
			}
		}

		while (true) {

		}
	}

}
