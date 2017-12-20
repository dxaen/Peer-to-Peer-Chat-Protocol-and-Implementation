package s2wmp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import s2wmp.enums.LookupStatus;
import s2wmp.enums.RegistrationStatus;
import s2wmp.packets.LookupPacket;
import s2wmp.packets.LookupResponsePacket;
import s2wmp.packets.RegistrationPacket;
import s2wmp.packets.RegistrationResponsePacket;
import s2wmp.packets.UserBroadCastPacket;

/**
 * Class: BootStrapServer2
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *This class is responsible for registration and lookup of peers
 */
public class BootStrapServer 
{
	
	public static void main(String args[])
	{
		Map<UserID, InetAddress> Table = new HashMap<UserID, InetAddress>();
		InetAddress serverAddress = null;
		
			try
			{
				serverAddress = InetAddress.getByName(Constants.B_ADDR);
			} catch (UnknownHostException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		try(DatagramSocket socket = new DatagramSocket(Constants.B_PORT, serverAddress)){
			
			while(true){
				try{
					byte[] arr = new byte[100];
					DatagramPacket request = new DatagramPacket(arr, 100);
					socket.receive(request);
					System.out.println("Received");
					//registration packet
					if(arr[1] == 2)
					{
						byte[] RegPackBytes = Arrays.copyOfRange(arr, 0, 50);
						RegistrationPacket RP = new RegistrationPacket(RegPackBytes);
						UserID userID = RP.getUserID();
						InetAddress userAddress = request.getAddress();
						if(!Table.containsKey(userID))
						{
							Table.put(userID, userAddress);
							RegistrationResponsePacket responsePack = new RegistrationResponsePacket();
							responsePack.setStatus(RegistrationStatus.SUCCESS);
							responsePack.setRequestID(RP.getUserID());
							byte[] responseArray = responsePack.toByteArray();
							DatagramPacket response = new DatagramPacket(responseArray, responseArray.length, request.getAddress(),request.getPort());
							socket.send(response);
						}
						else{
							RegistrationResponsePacket responsePack = new RegistrationResponsePacket();
							responsePack.setStatus(RegistrationStatus.ID_COLLISION);
							responsePack.setRequestID(RP.getUserID());
							byte[] responseArray = responsePack.toByteArray();
							DatagramPacket response = new DatagramPacket(responseArray, responseArray.length, request.getAddress(),request.getPort());
							socket.send(response);
						}
						
						
						
					}//lookup packet
					else if(arr[1] == 4){
						byte[] LookupPackBytes = Arrays.copyOfRange(arr, 0, 59);
						LookupPacket LP = new LookupPacket(LookupPackBytes);
						UserID lookupID = LP.getLookupID();
						
						if(Table.containsKey(lookupID))
						{
							InetAddress ipAddress = Table.get(lookupID);
							LookupResponsePacket LRP = new LookupResponsePacket(LP.getLookupID(), LP.getUserID());
							LRP.setStatus(LookupStatus.FOUND);							
							//LRP.setUserID(LP.getUserID());
							//LRP.setLookupID(lookupID);
							LRP.setRequestID(LP.getRequestID());
							LRP.setIPv4Address(ipAddress);
							byte[] lookupResponse = LRP.toByteArray();
							DatagramPacket response = new DatagramPacket(lookupResponse, lookupResponse.length, request.getAddress(),request.getPort());
							socket.send(response);
							
							
							
						}
						else{
							LookupResponsePacket LRP = new LookupResponsePacket(LP.getLookupID(), LP.getUserID());
							LRP.setStatus(LookupStatus.NOT_FOUND);
							LRP.setRequestID(LP.getRequestID());
							//LRP.setUserID(LP.getUserID());
							//LRP.setLookupID(lookupID);
							byte[] lookupResponse = LRP.toByteArray();
							DatagramPacket response = new DatagramPacket(lookupResponse, lookupResponse.length, request.getAddress(),request.getPort());
							socket.send(response);
							
						}
						
						
					}//server discovery packet
					else if(arr[1] == 7)
					{
						DatagramPacket data = new DatagramPacket(new byte[43], 43);
						socket.receive(data);
						UserBroadCastPacket p = new UserBroadCastPacket(data.getData());
						p.setReply(true);
						p.setTimestamp(new Date());
						byte [] retVal = p.toByteArray();
						socket.send(new DatagramPacket(retVal, retVal.length,data.getAddress(), Constants.REPLY_B_PORT));
					}
					
					
							
				}catch(IOException | RuntimeException ex){
					ex.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static InetAddress getIPv4InterfaceAddress() throws SocketException{
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        
    	InetAddress address = null;
    	while (interfaces.hasMoreElements()) 
    	{
    		NetworkInterface networkInterface = interfaces.nextElement();
    		
    		if (networkInterface.isLoopback() || !networkInterface.isUp())
				continue;
    		
        	for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) 
        	{

        		InetAddress addr = interfaceAddress.getAddress();
        		byte[] arr = addr.getAddress();
        		if(arr.length == 16)
        			continue;
        		else
        			address = addr;
        		
        		
        	}
        	//System.out.println(address);
    	}
    	return address;
	}

}
