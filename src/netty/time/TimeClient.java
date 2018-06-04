package netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * netty实现TimeClient
 */
public class TimeClient
{
	public void connet(int port, String host) throws Exception
	{
		NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
		
		try
		{
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(nioEventLoopGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>()
				{
					// 将ChannelHandel设置到ChannelPipeline(管道)中,用于处理网络I/O事件
					@Override
					protected void initChannel(SocketChannel ch) throws Exception
					{
						ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
						ch.pipeline().addLast(new StringDecoder());
						
						// 创建NioScoketChannel
						ch.pipeline().addLast(new TimeClientHandler());
					}
				});
			
			// 发起异步连接操作
			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
			
			// 等待客户端链路关闭
			channelFuture.channel().closeFuture().sync();
		}
		finally
		{
			// 退出, 释放NIO线程组
			nioEventLoopGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		int port = 8088;
		if(null != args && 0 < args.length)
		{
			try
			{
				port = Integer.parseInt(args[0]);
			}
			catch (Exception e)
			{
			}
		}
		
		new TimeClient().connet(port, "192.168.1.228");
	}
}
