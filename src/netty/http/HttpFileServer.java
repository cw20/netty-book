package netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer
{
	private static final String DEFAULT_URL = "/netty_file/";
	
	public void bind() throws Exception
	{
		NioEventLoopGroup parentGroup = new NioEventLoopGroup();
		NioEventLoopGroup childGroup = new NioEventLoopGroup();
		
		try
		{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					// http请求消息解码器
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
					// 将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
					
					// http响应编码器
					ch.pipeline().addLast("http-encoder", new HttpResponseDecoder());
					// 支持异步发送大的码流,防止内存溢出
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
					
					ch.pipeline().addLast("flieServerHandler", new HttpFileServerHandler());
				}
			});
			
			ChannelFuture f = serverBootstrap.bind(8088).sync();
			System.out.println("HTTP 文件服务器启动, 网址: http://192.168.1.228:8088" + HttpFileServer.DEFAULT_URL);
			
			f.channel().closeFuture().sync();
		}
		finally
		{
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new HttpFileServer().bind();
	}
}
