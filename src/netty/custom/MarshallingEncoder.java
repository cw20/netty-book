package netty.custom;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * Netty消息编码工具类
 */
@Sharable
public class MarshallingEncoder
{
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	Marshaller marshaller;
	
	public MarshallingEncoder() throws IOException
	{
		this.marshaller = MarshallingCodecFactory.buildMarshaller();
	}
	
	protected void encode(Object msg, ByteBuf out) throws Exception
	{
		try
		{
			// 获取写入位置
			int lengthPos = out.writerIndex();
			// 先写入4个byte, 用于记录编码后长度
			out.writeBytes(LENGTH_PLACEHOLDER);
			// 使用代理对象，防止marshaller写完之后关闭byte buf
			ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
			//4. 开始使用marshaller往bytebuf中编码
			marshaller.start(output);
			marshaller.writeObject(msg);
			//5. 结束编码
			marshaller.finish();
			//6. 设置对象长度
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
		}
		finally
		{
			marshaller.close();
		}
	}
}
