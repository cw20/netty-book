package netty.protobuf;

import java.util.ArrayList;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.protobuf.SubscribeReqProto.SubscribeReq.Builder;
import netty.protobuf.SubscribeRespProto.SubscribeResp;

public class SubReqClientHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		for(int i = 0; i < 10; i++)
		{
			ctx.write(subReq(i));
		}
		
		ctx.flush();
	}
	
	private SubscribeReqProto.SubscribeReq subReq(int i)
	{
		Builder buider = SubscribeReqProto.SubscribeReq.newBuilder();
		buider.setSubReqID(i);
		buider.setUserName("ZhanSan");
		buider.setProductName("netty for protobuf");
		
		ArrayList<String> address = new ArrayList<>();
		address.add("BeiJing");
		address.add("HangZhou");
		address.add("SuZhou");
		
		buider.addAllAddress(address);
		
		return buider.build();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		SubscribeRespProto.SubscribeResp resp = (SubscribeResp) msg;
		
		System.out.println("receive server response: [" + resp.toString() + "]");
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
}
