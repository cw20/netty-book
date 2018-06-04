package aio;

public class TimeServer
{
	public static void main(String[] args)
	{
		int port = 8088;
		
		new Thread(new AsyncTimeServerHandler(port), "AIO-AsyncTimeServerHandler-001").start();
	}
}
