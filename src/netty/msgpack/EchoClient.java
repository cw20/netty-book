package netty.msgpack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class EchoClient
{
	public void connect() throws Exception
	{
		NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
		
		try
		{
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(nioEventLoopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.handler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					ch.pipeline().addLast("Frame Decoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
					ch.pipeline().addLast("Msgpack Decoder", new MsgpackDecoder());
					
					ch.pipeline().addLast("Frame Encoder", new LengthFieldPrepender(2));
					ch.pipeline().addLast("Msgpack Encoder", new MsgpackEncoder());
					
					ch.pipeline().addLast(new EchoClientHandler(100));
				}
			});
			
			ChannelFuture f = bootstrap.connect("127.0.0.1", 8088).sync();
			f.channel().closeFuture().sync();
		}
		finally
		{
			nioEventLoopGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new EchoClient().connect();;
	}
}
