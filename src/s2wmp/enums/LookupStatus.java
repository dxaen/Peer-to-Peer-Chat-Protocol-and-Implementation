package s2wmp.enums;

public enum LookupStatus
{
	FOUND((byte)0),
	NOT_FOUND((byte)0x1),
	INVALID_LOOKUP_ID((byte)0x2),
	INVALID_REQUESTOR_ID((byte)0x3);
	
	private final byte status;

	LookupStatus(byte value) {
        this.status = value;
    }
	
	public static LookupStatus fromValue(byte b)
	{
		for(LookupStatus s : values())
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
