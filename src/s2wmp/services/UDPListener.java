package s2wmp.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import s2wmp.Constants;
import s2wmp.Profile;
import s2wmp.User;
import s2wmp.UserID;
import s2wmp.packets.UserBroadCastPacket;
/**
 * Class: UDPListener
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *	Listens to udp broadcasts
 */
public class UDPListener implements Runnable 
{
	Map<UserID, User> activePeers;
	Profile profile;
	Map<InetAddress, Long> recentRequests;
	public UDPListener(Map<UserID, User> activePeers, Profile p)
	{
		profile = p;
		this.activePeers = activePeers;
		recentRequests = new HashMap<InetAddress, Long>();
	}
	@Override
	public void run() 
	{
		try(DatagramSocket socket = new DatagramSocket(Constants.UDP_PORT))
		{
			System.out.println("UDPListener started");
			//receiving and processing broadcast packet
			socket.setBroadcast(true);
			while(true)
			{
				//read data
				DatagramPacket data = new DatagramPacket(new byte[43], 43);
				socket.receive(data);
				
				//don't reply to duplicates
				long cur = System.currentTimeMillis();
				InetAddress a = data.getAddress();
				
				if(recentRequests.containsKey(a) && (recentRequests.get(a) -cur) < 50000 )
					continue;
				else
					recentRequests.put(data.getAddress(), System.currentTimeMillis());
				
				byte [] bytes = data.getData();
				UserBroadCastPacket packet = new UserBroadCastPacket(bytes);
				if(packet.getUserID().equals(profile.getUID()))
					continue;
				//add user to the list of active peers
				if(!activePeers.containsKey(packet.getUserID()))
				{
					User u = new User(packet.getUserName(), packet.getUserID());
					u.setAddress(data.getAddress());
					activePeers.put(u.getUID(), u);
				}
				
				//if it's not a reply, send response back
				if(!packet.isReply())
				{					
					packet.setReply(true);
					packet.setUserID(profile.getUID());
					packet.setUserName(profile.getName());
					DatagramSocket ns = new DatagramSocket();
					ns.send(new DatagramPacket(packet.toByteArray(), 43, data.getAddress(), Constants.UDP_PORT));
				}
				
				cur = System.currentTimeMillis();
				for(InetAddress addr: recentRequests.keySet())
				{
					if((recentRequests.get(addr) - cur) > 50000)
					{
						recentRequests.remove(addr);
					}
				}
			}
			
		} 
		catch (SocketException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		catch (Exception e) 
		{			
			e.printStackTrace();
		}

	}

}
