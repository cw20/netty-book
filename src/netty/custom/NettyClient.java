package netty.custom;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyClient
{
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	NioEventLoopGroup group = new NioEventLoopGroup();
	
	public void connect() throws Exception
	{
		try
		{
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					// 解码器，消息最大长度限制，头部位移，头部长度
					ch.pipeline().addLast("messageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
					ch.pipeline().addLast("messageEncoder", new NettyMessageEncoder());
					ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(100));
					ch.pipeline().addLast("loginAuthReqHandler", new LoginAuthReqHandler());
					ch.pipeline().addLast("heartBeatReqHandler", new HeartBeatReqHandler());
				}
			});
			
			// 发起异步连接操作
//			ChannelFuture f = bootstrap.connect(new InetSocketAddress(NettyConstant.REMOTEIP, NettyConstant.PORT), new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)).sync();
			ChannelFuture f = bootstrap.connect("127.0.0.1", 8080).sync();
			f.channel().closeFuture().sync();
		}
		finally
		{
			// 所有资源释放完成之后，清空资源，再次发起重连操作
			executor.execute(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						TimeUnit.SECONDS.sleep(5);
						
						try
						{
							// 发起重连操作
							connect();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public static void main(String[] arg) throws Exception
	{
		new NettyClient().connect();
	}
}
