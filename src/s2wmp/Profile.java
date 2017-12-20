package s2wmp;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import s2wmp.crypto.KeyManager;
import s2wmp.enums.Status;
/**
 * Class: Profile
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *This class contains all of the user settings, including list of friends
 */
public class Profile
{
	Status status;
	private String name;
	private KeyManager keys; //creates and stores public and private keys
	private UserID uid;
	private String clientID; 
	private Map<String, User> friends;
	
	public Profile() throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		this("User",Util.getHash("sampleclient"));
	}
	
	public Profile(String name, String clientID) throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		this.name = name;
		keys = new KeyManager();
		uid = new UserID();
		friends = new HashMap<String, User>();
		this.clientID = clientID;
		status = Status.ONLINE;
	}
	
	public void addFriend(User friend)
	{
		if(!friends.containsKey(friend.id.id))
		{
			friends.put(friend.id.id, friend);
		}
	}
	
	public User getFriend(UserID id)
	{
		if(friends.containsKey(id.id))
		{
			return friends.get(id.id);
		}
		else
		{
			return null;
		}
	}
	
	public void removeFriend(String name)
	{
		for(User friend : friends.values())
		{
			if(friend.name.equals(name))
			{
				friends.remove(friend.id);
				return;
			}
		}
	}
	
	public User getFriend(String name)
	{
		User u = null;
		for(User friend : friends.values())
		{
			if(friend.name.equals(name))
			{
				u = friend;
				break;
			}
		}
		return u;
	}
	
	public boolean isFriend(UserID id)
	{
		return friends.containsKey(id.id);
	}
	
	public boolean isFriend(String name)
	{
		for(User friend : friends.values())
		{
			if(friend.name.equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setClientID(String id)
	{
		clientID = id;
	}
	
	public String getClientID()
	{		
		return clientID;
	}
	
	public UserID getUID()
	{
		return uid;
	}
	
	public void toXML()
	{
		
	}
	
	public void setFriends(Map<String, User> m)
	{
		friends = m;
	}
	
	public Map<String, User> getFriends()
	{
		return friends;
	}
	
	public void setKeyManager(KeyManager m)
	{
		keys = m;
	}
	
	public KeyManager getKeyManager()
	{
		return keys;
	}
	
	public void setStatus(Status s)
	{
		status = s;
	}
	
	public Status getStatus()
	{
		return status;
	}
	
	
	public  Collection<User> getFriendsIterator()
	{
		return friends.values();
	}
	
	public void getActivePeers()
	{
		int ct = 0;
		for(User user : friends.values())
		{
			if(user.getStatus() != Status.OFFLINE)
			{
				System.out.println(user.getName());
				++ct;
			}
		}
		if(ct == 0)
		{
			System.out.println("No friends online");
		}
	}
	
}
