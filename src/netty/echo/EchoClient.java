package netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient
{
	public void connect(int port, String host) throws Exception
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
					@Override
					protected void initChannel(SocketChannel ch) throws Exception
					{
						// 以指定字符拆包
//						ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//						ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
						
						// 以指定长度拆包
						ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
						
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new EchoClientHandler());
					}
				});
			
			ChannelFuture f = bootstrap.connect(host, port).sync();
			f.channel().closeFuture().sync();
		}
		finally
		{
			nioEventLoopGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		int port = 8088;
		
		new EchoClient().connect(port, "127.0.0.1");
	}
}
