/**
 * Class: TCPListener
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *	Listens to tcp packets
 */

package s2wmp.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import s2wmp.Constants;
import s2wmp.Networking;
import s2wmp.Profile;
import s2wmp.User;
import s2wmp.UserID;
import s2wmp.crypto.CryptoHolder;
import s2wmp.enums.ChatRequestStatus;
import s2wmp.enums.PacketType;
import s2wmp.enums.RegistrationStatus;
import s2wmp.packets.ChatRequestPacket;
import s2wmp.packets.MessagingPacket;
import s2wmp.packets.RegistrationResponsePacket;
import s2wmp.packets.StatusPacket;

public class TCPListener implements Runnable
{
	Socket connection;
	ServerSocket listener;
	Profile profile;
	Map<UserID, Long> outgoingRequests;
	Networking net;
	public TCPListener(Profile p, Map<UserID, Long> outgoingRequests, Networking net)
	{
		profile = p;
		this.outgoingRequests = outgoingRequests;
		this.net = net;
	}
	@Override
	public void run()
	{
		try 
		{
			listener = new ServerSocket(Constants.PORT);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		//STATEFUL
		while(true)
		{
			try 
			{
				connection = listener.accept();
				//read first 2 bytes so we can check the type
				byte [] head = new byte[2];
				connection.getInputStream().read(head, 0, 2);
				
				PacketType type = PacketType.fromValue(head[1]);
				
				switch(type)
				{
					case MESSAGING:
						//read length of the message
						byte [] temp = new byte[3];
						connection.getInputStream().read(temp, 0, 3);
		                ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(temp, 1, 3));
		                short len = buffer.getShort();	
		                
		                //copy read values into data buffer
		                byte [] data = new byte [77 + len];
		                data[2] = temp[0];
		                data[3] = temp[1];
		                data[4] = temp[3];
		                //read the rest of the message
		                connection.getInputStream().read(data, 5, 77 + len - 5);
					try 
					{
						MessagingPacket p = new MessagingPacket(data);
						User u = profile.getFriend(p.getSenderID());
						String message = CryptoHolder.getInstance().Decrypt(p.getEncryptedMessage(), u.getSharedKey());
						System.out.println(u.getName() + ": " + message);
						net.sendACK(u, true, p.getTimestamp());
					} 
					catch (Exception e) 
					{						
						e.printStackTrace();
					}
		                
						break;
					case CHAT_REQUEST:


						byte [] request = new byte[1000];
                        request[0] = head[0];
                        request[1] = head[1];
                        //byte buf = 0;
                        //connection.getInputStream().read(request, 2, 47);
                        int size = connection.getInputStream().available();
                        connection.getInputStream().read(request, 2, size); 
                        try
                        {
                            //check if the person who sent re
                            ChatRequestPacket hello = new ChatRequestPacket(request);
                            if(!profile.isFriend(hello.getRequesterID()) && hello.getStatus() == ChatRequestStatus.VALID
                            		&&hello.getFriendID().equals(profile.getUID()))
                            {
                                User u = new User();
                                
                                u.setUID(hello.getRequesterID());
                               	u.setName(hello.getUsername());
                                u.setAddress(connection.getInetAddress());
                                System.out.println("Friend name: " + u.getName());
                                System.out.println("Friend ID: " + u.getUID().id);
                                profile.addFriend(u);
                               
                                net.sendHelloPacket(u, ChatRequestStatus.VALID);
                            }

                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

						break;
					case STATUS:
						//updating status of the user
						byte [] statusData = new byte[19];
						connection.getInputStream().read(statusData,0, 19);
						StatusPacket p;
						try {
							p = new StatusPacket(statusData);
							if(profile.isFriend(p.getRequestID()))
							{
								profile.getFriend(p.getRequestID()).setStatus(p.getStatus());
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					case REG_RESPONSE:
						byte [] regData = new byte[19];
						connection.getInputStream().read(regData,0, 19);
						RegistrationResponsePacket regPacket;
						try {
							regPacket = new RegistrationResponsePacket(regData);
							if(regPacket.getStatus() != RegistrationStatus.SUCCESS)
							{
								System.err.println("Could nor register");
							}
							else
							{
								System.out.println("Registration succeded");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						break;
				}
				
			} 
			catch (IOException e) 
			{}
		}
	}

}
