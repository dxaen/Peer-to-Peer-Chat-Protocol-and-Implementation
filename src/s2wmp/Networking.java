package s2wmp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.crypto.SecretKey;

import s2wmp.crypto.CryptoHolder;
import s2wmp.crypto.KeyManager;
import s2wmp.enums.ChatRequestStatus;
import s2wmp.enums.LookupStatus;
import s2wmp.packets.ACKPacket;
import s2wmp.packets.ChatRequestPacket;
import s2wmp.packets.LookupPacket;
import s2wmp.packets.LookupResponsePacket;
import s2wmp.packets.MessagingPacket;
import s2wmp.packets.Packet;
import s2wmp.packets.StatusPacket;
import s2wmp.packets.UserBroadCastPacket;
/**
 * Class: Networking
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *The class is responsible for set netwroking operations
 */
public class Networking
{
	InetAddress serverAddr;
	DatagramSocket serverConn;
	boolean isDynamicServer;
	Profile profile;
	Map<UserID, Long> requests;
	
	public Networking(Profile p, Map<UserID, Long> requests)
	{
		profile = p;
		isDynamicServer = false;
		this.requests = requests;
	}

	/**
	 * Connects to the bootstrap server
	 */
	public void connectToBootstrap()
	{
		try
		{
			if(!isDynamicServer)
				serverAddr =InetAddress.getByName(Constants.B_ADDR);
			serverConn = new DatagramSocket(Constants.B_PORT, serverAddr);			
		} 
		catch (Exception e)
		{} 
		
	}
	
	public boolean isDynamicServer()
	{
		return isDynamicServer;
	}
	public void setDynamic(boolean val)
	{
		isDynamicServer = val;
	}
	public boolean haveServer()
	{
		if(serverConn == null)
			return false;
		
		try
		{
			if((!serverConn.isBound() || serverConn.isClosed()) && serverAddr.isReachable(10000))
			{
				return false;
			}
		} catch (IOException e)
		{
			return false;
		}
		return true;
	}
	
	public void sendMessage(User user, String msg) throws Exception
	{
		//create message from string
		MessagingPacket message = new MessagingPacket(user, profile);		
		
		if(user.getSharedKey() == null)
		{
			SecretKey k = KeyManager.agreeSecretKey(user.getPublicKey(), 
					profile.getKeyManager().getPrivateKey());
			user.setSharedKey(k);
		}
		Socket output = new Socket(user.getAddress(), Constants.PORT);
		
		byte [] encrypted = CryptoHolder.getInstance().Encrypt(msg, user.getSharedKey());
		message.setEncryptedMessage(encrypted);
	
		output.getOutputStream().write(message.toByteArray());
		output.close();
	}
	
	public void findServer() throws Exception
	{
		try(DatagramSocket socket = new DatagramSocket(0))
		{
			//find broadcast address for a host
			InetAddress broadcastAddr = getBroadcastAddr();
			//construct broadcast message
			Packet packet = new UserBroadCastPacket(profile.getUID());
			byte [] data = packet.toByteArray();
			DatagramPacket request = new DatagramPacket(data, data.length, broadcastAddr, Constants.B_PORT);
			//send message
			socket.send(request);
			DatagramSocket receiver = new DatagramSocket(Constants.REPLY_B_PORT);
			DatagramPacket server = new DatagramPacket(new byte[data.length], data.length);
			socket.setSoTimeout(1000);
			try
			{
				socket.receive(server);
							
				this.setServerAddress(server.getAddress());
				this.setDynamic(true);
			}
			catch(SocketTimeoutException e)
			{
				System.out.println("Server lookup timed out");
			}
		}
		catch(SocketException e)
		{
			Exception ex = new Exception("Could not accuire broadcast address");
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
	}
	public void sendACK(User user, boolean isAck, Date timestamp)
	{
		ACKPacket p = new ACKPacket();
		p.setAck(isAck);
		p.setUserID(profile.getUID());
		p.setTimestamp(timestamp);
		try {
			Socket s = new Socket(user.getAddress(), Constants.PORT);
			s.getOutputStream().write(p.toByteArray());
			s.getOutputStream().flush();
			s.close();
		} catch (IOException e) {
			System.out.println("Could not send ack");
		}	
	}
	public void sendStatus(User user)
	{
		StatusPacket m = new StatusPacket(profile);
		try {
			Socket s = new Socket(user.getAddress(), Constants.PORT);
			s.getOutputStream().write(m.toByteArray());
			s.getOutputStream().flush();
			s.close();
		} catch (IOException e) {
			System.out.println("Could not update status");
		}		
	}
	
	public void startLanDiscovery() throws Exception
	{
		InetAddress broadcastAddr = null;
		
		try(DatagramSocket socket = new DatagramSocket(0))
		{
			//find broadcast address for a host
			broadcastAddr = getBroadcastAddr();
			//construct broadcast message
			UserBroadCastPacket packet = new UserBroadCastPacket(profile.getUID());
			packet.setUserName(profile.getName());
			byte [] data = packet.toByteArray();
			DatagramPacket request = new DatagramPacket(data, data.length, broadcastAddr, Constants.UDP_PORT);
			//send message
			socket.send(request);
		}
		catch(SocketException e)
		{
			Exception ex = new Exception("Could not accuire broadcast address");
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
	}
	
	public void setServerAddress(InetAddress addr)
	{
		serverAddr = addr;
	}
	
	public void setServerAddress(String addr) throws UnknownHostException
	{
		serverAddr = InetAddress.getByName(addr);
	}
	
	public InetAddress getServerAddress()
	{
		return serverAddr;
	}
	
	/**
	 * Looks up user in bootstrap server
	 * @throws Exception 
	 */
	public void lookupUser(String uid) throws Exception
	{
		if(serverConn == null)
		{
			throw new Exception("Server is not connected");
		}
		else if(!serverConn.isConnected())
		{
			throw new Exception("Server connection was closed");
		}
		LookupPacket packet = new LookupPacket(new UserID(uid), profile.getUID());
		byte [] data = packet.toByteArray();
		serverConn.send(new DatagramPacket(data, data.length, serverAddr, Constants.B_PORT));
		
	}
	
	/**
	 * Function send public key exchange packet.
	 * @param user
	 * @param status
	 */
	
	public void sendHelloPacket(User user, ChatRequestStatus status)
	{
		try 
		{
			ChatRequestPacket packet = new ChatRequestPacket(profile, user);
			packet.setStatus(status);
			Socket s = new Socket(user.getAddress(), Constants.PORT);
			s.getOutputStream().write(packet.toByteArray());
			s.getOutputStream().flush();
			s.close();
		} 
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private InetAddress getBroadcastAddr() throws SocketException
    {
    	Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        
    	InetAddress addr = null;
    	while (interfaces.hasMoreElements()) 
    	{
    		NetworkInterface networkInterface = interfaces.nextElement();
    		
    		if (networkInterface.isLoopback() || !networkInterface.isUp())
				continue;
    		
        	for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) 
        	{

        		addr = interfaceAddress.getBroadcast();
        		if (addr == null) 
        		{
                    continue;
                }
        		else
        		{
        			return addr;
        		}
    		}
    	}
    	return addr;
    }
	
	public  InetAddress getLocalAddr() throws SocketException
	{
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        
    	InetAddress addr = null;
    	while (interfaces.hasMoreElements()) 
    	{
    		NetworkInterface networkInterface = interfaces.nextElement();
    		
    		if (networkInterface.isLoopback() || !networkInterface.isUp())
				continue;
    		
        	for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) 
        	{

        		addr = interfaceAddress.getAddress();
        		if (addr == null || addr.isAnyLocalAddress()) 
        		{
                    continue;
                }
        		else
        		{
        			return addr;
        		}
    		}
    	}
    	return addr;
	}
	

}
