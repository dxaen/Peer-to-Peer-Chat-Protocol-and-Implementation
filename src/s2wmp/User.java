package s2wmp;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;

import javax.crypto.SecretKey;

import s2wmp.enums.*;

/**
 * Class: User
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * Represents a peer with all needed values
 */
public class User 
{
	String statusMessage;
	Status status;
	String name;
	UserID id;
	Key pubKey;
	SecretKey sharedKey;
	boolean ignore;
	InetAddress addr;

	
	public User()
	{
		statusMessage = "Status unknown";
		status = Status.OFFLINE;
		name = null;
		id = new UserID();
		pubKey = null;
		sharedKey = null;
		
		ignore = false;
		addr = null;
		
	}
	
	public User(String name, UserID id)
	{
		this();
		this.name = name;
		this.id = id;

	}
	
	@Override
	public String toString() {
		return "Profile [statusMessage="+statusMessage+", status="+status+", name="+name+", id="+id+
				", pubKey="+pubKey+", sharedKey="+sharedKey+", ignore="+ignore+", addr="+addr;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setUID(UserID id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public UserID getUID()
	{
		return id;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(! (o instanceof User))
		{
			return false;
		}
		User other = (User)o;
		
		return id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
	
	public void setSharedKey(SecretKey key)
	{
		this.sharedKey = key;
	}
	
	public SecretKey getSharedKey()
	{
		return sharedKey;
	}
	
	public void setPublicKey(Key pub)
	{
		this.pubKey = pub;
	}
	
	public Key getPublicKey()
	{
		return pubKey;
	}
	
	public void setAddress(String addr) throws UnknownHostException
	{
		this.addr = InetAddress.getByName(addr);
	}
	
	public void setAddress(InetAddress addr)
	{
		this.addr = addr;
	}
	
	public InetAddress getAddress()
	{
		return addr;
	}
	
	public Status getStatus()
	{
		return status;
	}
	
	public void setStatus(Status s)
	{
		status = s;
	}
}
