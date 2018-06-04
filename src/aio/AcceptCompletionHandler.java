package aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * accept操作成功消息接收处理器
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>
{
	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment)
	{
		// 当前客户端连接成功后,因为一个AsynchronousSocketChannel可以接收成千上万个客户端,所以继续调用它的accept方法,接收其他的客户端连接,最终形成一个循环
		attachment.asynchronousServerSocketChannel.accept(attachment, this);
		// 接收缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// 接收回调handler
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment)
	{
		exc.printStackTrace();
		attachment.latch.countDown();
	}
}
