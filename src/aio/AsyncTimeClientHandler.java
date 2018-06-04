package aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements Runnable, CompletionHandler<Void, AsyncTimeClientHandler>
{
	private String host;
	private int port;
	CountDownLatch countDownLatch;
	AsynchronousSocketChannel asynchronousSocketChannel;
	
	public AsyncTimeClientHandler(String host, int port)
	{
		this.host = host;
		this.port = port;
		
		try
		{
			this.asynchronousSocketChannel = AsynchronousSocketChannel.open();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		countDownLatch = new CountDownLatch(1);
		asynchronousSocketChannel.connect(new InetSocketAddress(host, port), this, this);
		
		try
		{
			countDownLatch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void completed(Void result, AsyncTimeClientHandler attachment)
	{
		byte[] bytes = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		writeBuffer.put(bytes);
		writeBuffer.flip();
		
		asynchronousSocketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>()
		{
			@Override
			public void completed(Integer result, ByteBuffer buffer)
			{
				if(buffer.hasRemaining())
				{
					asynchronousSocketChannel.write(buffer, buffer, this);
				}
				else
				{
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					
					asynchronousSocketChannel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>()
					{
						@Override
						public void completed(Integer result, ByteBuffer buffer)
						{
							buffer.flip();
							byte[] bytes = new byte[buffer.remaining()];
							buffer.get(bytes);
							
							try
							{
								String resp = new String(bytes, "UTF-8");
								System.out.println(resp);
								
								countDownLatch.countDown();
							}
							catch (UnsupportedEncodingException e)
							{
								e.printStackTrace();
							}
						}

						@Override
						public void failed(Throwable exc, ByteBuffer buffer)
						{
							try
							{
								asynchronousSocketChannel.close();
								countDownLatch.countDown();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer buffer)
			{
				try
				{
					asynchronousSocketChannel.close();
					countDownLatch.countDown();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, AsyncTimeClientHandler attachment)
	{
		exc.printStackTrace();
		try
		{
			asynchronousSocketChannel.close();
			countDownLatch.countDown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
