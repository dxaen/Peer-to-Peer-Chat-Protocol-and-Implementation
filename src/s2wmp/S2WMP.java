
package s2wmp;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLEngineResult.Status;

import s2wmp.enums.ChatRequestStatus;
import s2wmp.services.RequestCleanupService;
import s2wmp.services.TCPListener;
import s2wmp.services.UDPListener;

/**
 * Class: S2WMP
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * The main class
 */
public class S2WMP
{
	//unregistered active peers
	private static Map<UserID, User> activePeers = new ConcurrentHashMap<UserID, User>();
	//list of current requests
	private static Map<UserID, Long> outgoingRequests = new ConcurrentHashMap<UserID, Long>();
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        //load the profile if it exists, otherwise create a new one
        Profile profile = null;
    

        File pfile = new File("./myprofile.json");
        if(pfile.exists())
        {
            try
			{
				profile = Util.loadProfile(pfile.getAbsolutePath());
			} 
            catch (IOException e)
			{
				System.err.println("Cannot load profile");
				System.exit(2);
			}
        }
        else
        {
            System.out.print("No profiles found. Enter desired user name: ");
            String name = in.nextLine();
            try
            {
                //creating new profile
                profile = new Profile(name, Util.getHash("sampleclient"));
                try
				{
					Util.saveProfile(profile, "./myprofile.json");
				} 
                catch (IOException e)
				{
					System.err.println("Cannot save profile. Data will be lost");
				}
            }
            catch (InvalidKeySpecException | NoSuchAlgorithmException e)
            {
                System.err.println("Could not create profile, exiting");
                System.exit(1);
            }
        }
        Networking net = new Networking(profile, outgoingRequests);
        
        Thread packetListen = new Thread(new TCPListener(profile, outgoingRequests, net));
        packetListen.start();
        Thread cleanup = new Thread(new RequestCleanupService(outgoingRequests));
        cleanup.start();
        Thread udp = new Thread(new UDPListener(activePeers, profile));
        udp.start();
        System.out.println("Chat client has started. Enter command at any time. Type \\help to get the list of commands");
       

        System.out.println("Current active connections:");
        profile.getActivePeers();
        
        //try to connect to the server
        net.connectToBootstrap();
        if(!net.haveServer())
        {
        	try
			{
        		System.out.println("Bootstrap server address is invalid. Starting lookup");
				net.findServer();
			} 
        	catch (Exception e)
			{
				System.err.println("Bootstrap lookup failed");
				System.err.println(e.getMessage());
			}
        }
        
        //STATEFUL
        while (true)
        {
            String input = in.nextLine().trim();
            //command must start with a slash. If it's not, then the input is invalid
            if(input.length() == 0 )
            {
                continue;
            }
            else if(input.charAt(0) != '\\' || (input.charAt(0) != '\\' && input.length() == 1) )
            {
                System.err.println("Unknown command. Commands must start with \\");
                continue;
            }
            //get the command
            int idx = input.indexOf(' ');
            if(idx <= 0)
            {
                idx = input.length();
            }

            String command = input.substring(0, idx);
            //process the command
            switch(command)
            {
                case("\\help"):
                    usage();
                    break;
                case("\\quit"):
					try
					{
						Util.saveProfile(profile, "./myprofile.json");
					} 
	                catch (IOException e1)
					{
	                	System.err.println("Cannot save profile. Data will be lost");
					}
                    System.exit(0);
                    break;
                case("\\discover"):
					try
					{
						System.out.println("Starting lan discovery");
						net.startLanDiscovery();
					} 
                	catch (Exception e1)
					{
						System.err.println("Lan discovery could not be completed");
					}
                	break;
                case("\\listPeers"):
                	System.out.println("Following peers are currently active:");
                	if(activePeers.size() == 0)
                	{
                		System.out.println("No peers found.");
                		continue;
                	}
                	for(User u : activePeers.values())
                	{
                		System.out.println(u.name + " : " + u.getUID().id);
                	}
                	break;
                case("\\listFriends"):
                	System.out.println("Following peers are registered:");
                	profile.getActivePeers();
                	break;
                case("\\add"):
                	String uid = input.substring(idx+1);
                	if(uid == null || uid.isEmpty())
                	{
                		System.err.println("User ID is not provided");
                		continue;
                	}
                	UserID id = new UserID(uid);
                	if(profile.isFriend(id))
                	{
                		System.err.println("User is already in contacts");
                		continue;
                	}
                	
                	if(activePeers.containsKey(id))
                	{
                		net.sendHelloPacket(activePeers.get(id), ChatRequestStatus.VALID);
                		continue;
                	}
                	//ask server for user ID
                	if(net.haveServer())
                	{
                		try
						{
                			System.out.println("Looking up user's address...");
							net.lookupUser(uid);
							
						} catch (Exception e)
						{
							System.err.println("User is not found");
							continue;
						}
                	}
                	break;
                case("\\send"):
                	//strip the command away, get username and the message
                	String data = input.substring(idx, input.length()).trim();
                	//check if command is valid
                	if(data.length() == 0)
                	{
                		System.err.println("Destination is not specified. Message cannot be empty");
                		continue;
                	}
                	//check if there is a space. If not, then either message is empty or
                	//username is not specified
                	idx = input.indexOf(' ');
                	
                	if(idx <= 0)
                	{
                		System.err.println("Destination or message is not specified");
                		continue;
                	}
                	//grab username from the input
                	String uname = data.substring(0, idx);
                	//if user is not added or the name is wrong - don't send the message
                	if(!profile.isFriend(uname))
                	{
                		System.err.println( uname + " is not in the contact list.");
                		continue;
                	}
                	//so far, so good. Get the message from the data
                	String msg = data.substring(uname.length()).trim();
                	//check if message is not an empty string.
                	if(msg == null || msg.isEmpty())
                	{
                		System.err.println("Message is not specified");
                		continue;
                	}
                	//should be good. try sending message
					try
					{
						net.sendMessage(profile.getFriend(uname), msg);
					} 
					catch (Exception e)
					{
						System.err.println(e.getMessage());
						continue;
					}
                	
                	break;
                case("\\rem"):
                	String name = input.substring(idx+1).trim();
                	if(name == null || name.isEmpty())
                	{
                		System.err.println("User name is not specified");
                	}
                	if(profile.isFriend(name))
                	{
                		profile.removeFriend(name);
                		System.out.println(name + " was removed.");
                	}
                	else
                	{
                		System.out.println("Peer is not in the friend list");
                	}
                	break;
                case("\\status"):
                	String st = input.substring(idx).trim();
                	if(st == null || st.isEmpty())
                	{
                		System.out.println("No status specified");
                	}
                	
                	try
                	{
                		Status s = Status.valueOf(st.toUpperCase());
                		for(User user : profile.getFriendsIterator())
                		{
                			net.sendStatus(user);
                		}
                	}
                	catch(Exception e)
                	{
                		System.out.println("Invalid value enetered");
                	}
                	
                	break;
                default:
                    System.err.println("Uknown command.");
                    break;
            }
            //do_something(input);
            
        }
    }

    private static void usage()
    {
        System.out.println("Send message: \\send user message");
        System.out.println("Add friend: \\add userid (128 bit id)");
        System.out.println("Remove friend: \\rem username or user ID");
        System.out.println("List discovered peers: \\listPeers");
        System.out.println("List registered friends: \\listFriends");
        System.out.println("Perform LAN discovery: \\discover");
        System.out.println("Status change: \\statu status");
        System.out.println("Exit: \\quit");
        System.out.println("Print this menu: \\help");

    }

}

