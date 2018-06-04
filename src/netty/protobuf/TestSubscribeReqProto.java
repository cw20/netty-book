package netty.protobuf;

import java.util.ArrayList;

import com.google.protobuf.InvalidProtocolBufferException;

import netty.protobuf.SubscribeReqProto.SubscribeReq.Builder;

public class TestSubscribeReqProto
{
	private static byte[] encode(SubscribeReqProto.SubscribeReq req)
	{
		return req.toByteArray();
	}
	
	private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException
	{
		return SubscribeReqProto.SubscribeReq.parseFrom(body);
	}
	
	public static SubscribeReqProto.SubscribeReq createSubscribeReq()
	{
		Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
		
		builder.setSubReqID(1);
		builder.setUserName("ZhangSan");
		builder.setProductName("Netty");
		
		ArrayList<String> address = new ArrayList<>();
		address.add("BeiJing");
		address.add("ShenZhen");
		address.add("HangZhou");
		
		builder.addAllAddress(address);
		
		return builder.build();
	}
	
	public static void main(String[] args) throws InvalidProtocolBufferException
	{
		SubscribeReqProto.SubscribeReq req = createSubscribeReq();
		System.out.println("before encode: " + req.toString());
		
		byte[] encodeReq = encode(req);
		System.out.println("after encode: " + encodeReq);
		
		SubscribeReqProto.SubscribeReq req2 = decode(encodeReq);
		System.out.println("after decode: " + req2.toString());
		
		System.out.println("assert equal: " + req2.equals(req));
	}
}
