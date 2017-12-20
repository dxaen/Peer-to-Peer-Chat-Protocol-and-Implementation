package s2wmp.enums;

public enum MessageError
{
	NONE((byte)0),
	INVALID_ID((byte)0x1),
	HASH_MISMATCH((byte)0x2),
	BAD_HEADER((byte)0x3),
	BAD_ENCRYPTION((byte)0x4);
	
	private final byte error;

	MessageError(byte value) {
        this.error = value;
    }
	
	public static MessageError fromValue(byte b)
	{
		for(MessageError e : values())
		{
			if(e.error == b)
			{
				return e;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public byte getError()
	{
		return error;
	}
}
