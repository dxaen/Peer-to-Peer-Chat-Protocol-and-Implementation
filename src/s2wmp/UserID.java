package s2wmp;
/**
 * Class: UserID
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * A wrapper for handling userID 
 */
public class UserID
{
	//id of the client. Permanent.
	public final String id;
	
	public UserID()
	{
		this(Util.genID());
	}
	
	public UserID(String id)
	{
		this.id = new String(id);
	}
	
	public UserID(byte [] bytes)
	{
		id = Util.bytesToHex(bytes);
	}
	
	/*public void setId(String id)
	{
		this.id = id;
	}*/
	
	public String getId()
	{
		return id;
	}
	
	public byte[] toBytes() 
	{		
		return Util.hexToBytes(id);
	}
	
	@Override
	public boolean equals(Object id)
	{
		if(! (id instanceof UserID))
		{
			return false;
		}
		
		UserID other = (UserID)id;
		
		return this.id.equalsIgnoreCase(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
