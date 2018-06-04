package netty.custom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 握手和安全认证请求
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		ctx.writeAndFlush(buildLoginReq());
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		NettyMessage message = (NettyMessage) msg;
		
		// 如果是握手应答消息, 判断是否认证成功
		if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP)
		{
			// 握手应答消息
			byte loginResult = (byte) message.getBody();
			if(loginResult != (byte) 0)
			{
				// 握手失败，关闭连接
				ctx.close();
			}
			else
			{
				System.out.println("login is ok: " + message);
				ctx.fireChannelRead(msg);
			}
		}
		else
		{
			// 传给后面handler处理
			ctx.fireChannelRead(msg);
		}
	}
	
	/**
	 * 构造握手请求消息
	 * @return
	 */
	private NettyMessage buildLoginReq()
	{
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		
		header.setType(MessageType.LOGIN_REQ);
		message.setHeader(header);
		return message;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		ctx.fireExceptionCaught(cause);
	}
}
