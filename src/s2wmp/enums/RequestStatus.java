package s2wmp.enums;
/**
 * Class: ChatRequestStatus
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *	Values for status of teh key exchange
 */
public enum ChatRequestStatus {
	
	VALID((byte)0x1),
	INVALID_REQUESTER_ID((byte)0x2),
	INVALID_FRIEND_ID((byte)0x3),
	INVALID_KEY((byte)0x4);
	
	private final byte status;

	ChatRequestStatus(byte value) {
        this.status = value;
    }
	
	public static ChatRequestStatus fromValue(byte b)
	{
		for(ChatRequestStatus s : values())
		{
			if(s.status == b)
			{
				return s;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public byte getByte()
	{
		return status;
	}
}
