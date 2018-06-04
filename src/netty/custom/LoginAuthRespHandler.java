package netty.custom;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 握手和安全认证响应
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter
{
	/**
	 * 缓存
	 */
	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
	
	private String[] whitekList = {"127.0.0.1", "192.168.1.228"};
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		NettyMessage message = (NettyMessage) msg;
		
		// 如果是握手请求消息进行处理
		if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ)
		{
			String nodeIndex = ctx.channel().remoteAddress().toString();
			NettyMessage loginResp = null;
			
			if(nodeCheck.containsKey(nodeIndex))
			{
				// 重复登录，拒绝
				loginResp = buildResponse((byte) -1);
			}
			else
			{
				InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
				String ip = address.getAddress().getHostAddress();
				boolean isOK = false;
				for(String WIP : whitekList)
				{
					if(WIP.equals(ip))
					{
						isOK = true;
						break;
					}
				}
				
				if(isOK)
				{
					loginResp = buildResponse((byte) 0);
					nodeCheck.put(ip, true);
				}
				else
				{
					loginResp = buildResponse((byte) -1);
				}
			}
			
			System.out.println("The login response is :" + loginResp + "body [" + loginResp.getBody() + "]");
			ctx.writeAndFlush(loginResp);
		}
		else
		{
			ctx.fireChannelRead(msg);
		}
	}
	
	private NettyMessage buildResponse(byte result)
	{
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_RESP);
		message.setHeader(header);
		message.setBody(result);
		
		return message;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		nodeCheck.remove(ctx.channel().remoteAddress().toString()); //删除缓存
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
