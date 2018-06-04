package netty.custom;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer
{
	public void bind() throws Exception
	{
		NioEventLoopGroup parentGroup = new NioEventLoopGroup();
		NioEventLoopGroup childGroup = new NioEventLoopGroup();
		
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
				ch.pipeline().addLast("messageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
				ch.pipeline().addLast("messageEncoder", new NettyMessageEncoder());
				ch.pipeline().addLast("loginAuthRespHandler", new LoginAuthRespHandler());
				ch.pipeline().addLast("heartBeatRespHandler", new HeartBeatRespHandler());
			}
		});
		
		serverBootstrap.bind(new InetSocketAddress(NettyConstant.REMOTEIP, NettyConstant.PORT)).sync();
		
		System.out.println("Netty server start ok : " + (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));
	}
	
	public static void main(String[] args) throws Exception
	{
		new NettyServer().bind();
	}
}
