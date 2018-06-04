package netty.custom;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> //MessageToMessageEncoder<NettyMessage>
{
	MarshallingEncoder marshallingEncoder;
	
	public NettyMessageEncoder() throws IOException
	{
		this.marshallingEncoder = new MarshallingEncoder();
	}
	
	/*
	@Override
	public void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception
	{
		if(msg == null || msg.getHeader() == null)
			throw new Exception("The encode message is null");
		
		Header header = msg.getHeader();
		ByteBuf sendBuf = Unpooled.buffer();
		sendBuf.writeInt(header.getCrcCode());
		sendBuf.writeInt(header.getLength());
		sendBuf.writeLong(header.getSessionID());
		sendBuf.writeByte(header.getType());
		sendBuf.writeByte(header.getPriority());
		sendBuf.writeInt(header.getAttachment().size());
		
		Map<String, Object> attachment = header.getAttachment();
		String key = null;
		byte[] keyArray = null;
		Object value = null;
		for(Entry<String, Object> en : attachment.entrySet())
		{
			key = en.getKey();
			keyArray = key.getBytes("UTF-8");
			value = en.getValue();
			
			sendBuf.writeInt(keyArray.length);
			sendBuf.writeBytes(keyArray);
			marshallingEncoder.encode(value, sendBuf);
		}
		
		// for gc
		key = null;
		keyArray = null;
		value = null;
		if(msg.getBody() != null)
		{
			marshallingEncoder.encode(msg.getBody(), sendBuf);
		}
		
//		sendBuf.writeInt(0);
		// 在第4个字节出写入Buffer的长度
		sendBuf.setInt(4, sendBuf.readableBytes());
		// 把Message添加到List传递到下一个Handler   
        out.add(sendBuf);
	}
	*/

	@Override
	protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception
	{
		if(msg == null || msg.getHeader() == null)
			throw new Exception("The encode message is null");
		
		Header header = msg.getHeader();
		sendBuf.writeInt(header.getCrcCode());
		sendBuf.writeInt(header.getLength());
		sendBuf.writeLong(header.getSessionID());
		sendBuf.writeByte(header.getType());
		sendBuf.writeByte(header.getPriority());
		sendBuf.writeInt(header.getAttachment().size());
		
		Map<String, Object> attachment = header.getAttachment();
		String key = null;
		byte[] keyArray = null;
		Object value = null;
		for(Entry<String, Object> en : attachment.entrySet())
		{
			key = en.getKey();
			keyArray = key.getBytes("UTF-8");
			value = en.getValue();
			
			sendBuf.writeInt(keyArray.length);
			sendBuf.writeBytes(keyArray);
			marshallingEncoder.encode(value, sendBuf);
		}
		
		// for gc
		key = null;
		keyArray = null;
		value = null;
		if(msg.getBody() != null)
		{
			marshallingEncoder.encode(msg.getBody(), sendBuf);
		}
		else
		{
			sendBuf.writeInt(0);
		}
		
		// 之前写了crcCode 4bytes，除去crcCode和length 8bytes即为更新之后的字节
		sendBuf.setInt(4, sendBuf.readableBytes() - 8);
	}
}
