package bio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 时间服务器处理器线程池
 */
public class TimeServerHandlerExecutePool
{
	private ExecutorService executor;
	
	public TimeServerHandlerExecutePool(int maximumPoolSize, int queueSize)
	{
		this.executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
				, maximumPoolSize, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
	}
	
	public void execute(Runnable task)
	{
		executor.execute(task);
	}
}
