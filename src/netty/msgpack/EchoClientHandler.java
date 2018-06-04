package netty.msgpack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter
{
	private final int sendNumber;
	
	public EchoClientHandler(int sendNumber)
	{
		this.sendNumber = sendNumber;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		UserInfo[] userInfo = UserInfo();
		for(UserInfo u : userInfo)
		{
			ctx.write(u);
		}
		ctx.flush();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		System.out.println("client read msg: " + msg);
		ctx.write(msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		cause.printStackTrace();
		ctx.close();
	}
	
	private UserInfo[] UserInfo()
	{
		UserInfo[] array = new UserInfo[sendNumber];
		
		for(int i = 0; i < sendNumber; i++)
		{
			UserInfo user = new UserInfo();
			user.setId(i);
			user.setName("name:" + i);
			user.setAge(i + 1);
			array[i] = user;
		}
		
		return array;
	}
}
