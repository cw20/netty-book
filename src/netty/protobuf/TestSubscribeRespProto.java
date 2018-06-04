package netty.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestSubscribeRespProto
{
	public static byte[] encode(SubscribeRespProto.SubscribeResp resp)
	{
		return resp.toByteArray();
	}
	
	public static SubscribeRespProto.SubscribeResp decode(byte[] body) throws InvalidProtocolBufferException
	{
		return SubscribeRespProto.SubscribeResp.parseFrom(body);
	}
	
	public static SubscribeRespProto.SubscribeResp createSubscribeReq()
	{
		SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
		builder.setSubReqID(1);
		builder.setRespCode(101);
		builder.setDesc("desc");
		
		return builder.build();
	}
	
	public static void main(String[] args) throws InvalidProtocolBufferException
	{
		SubscribeRespProto.SubscribeResp resp = createSubscribeReq();
		
		System.out.println("before encode: " + resp.toString());
		
		byte[] encodeResp = encode(resp);
		System.out.println("after encode: " + encodeResp);
		
		SubscribeRespProto.SubscribeResp resp2 = decode(encodeResp);
		System.out.println("after decode: " + resp2.toString());
		
		System.out.println("assert equal: " + resp2.equals(resp));
	}
}
