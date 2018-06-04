package netty.time;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * netty实现TimeServer
 */
public class TimeServer
{
	public void bind(int port) throws Exception
	{
		// 配置服务端的NIO线程
		NioEventLoopGroup parentGroup = new NioEventLoopGroup(); //Reactor线程组, 负责接受客户端连接
		NioEventLoopGroup childGroup = new NioEventLoopGroup(); //Reactor线程组, 负责网络读写
		
		try
		{
			// 辅助启动类
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class) //设置创建的channel为NioServerSocketChannel
			.option(ChannelOption.SO_BACKLOG, 1024) //配置TCP参数
			.childHandler(new ChildChannelHandler()); //绑定IO事件处理类
			
			// 绑定端口, 同步等待成功
			ChannelFuture f = serverBootstrap.bind(port).sync();
			
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		}
		finally
		{
			// 退出, 释放线程池资源
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>
	{
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception
		{
			// 新增两个解码器,解决粘包问题
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
			arg0.pipeline().addLast(new StringDecoder());
			
			arg0.pipeline().addLast(new TimeServerHandler());
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
				// 采用默认值
			}
		}
		
		new TimeServer().bind(port);
	}
}