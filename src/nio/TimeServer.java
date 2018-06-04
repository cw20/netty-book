package nio;

public class TimeServer
{
	public static void main(String[] args)
	{
		int port = 8088;
		
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		
		new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
	}
}
