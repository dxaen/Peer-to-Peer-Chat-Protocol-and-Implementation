package s2wmp;

/**
 * Class: BootStrapServer2
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *The list of globals, used througth the project
 */
public class Constants
{
	//address of the bootstrap server
	public static final String B_ADDR = "127.0.0.1";
	//port of the bootstrap server
	public static final int B_PORT = 5005;
	//bootstrap replies to this port
	public static final int REPLY_B_PORT = 5002;
	//port for the chat and tcp messages
	public static final int PORT = 5000;
	//port for udp broadcast
	public static final int UDP_PORT = 5001;
}
