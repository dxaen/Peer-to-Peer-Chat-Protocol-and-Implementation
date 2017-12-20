package s2wmp.enums;

public enum Status
{
	ONLINE((byte)0x1),
	AWAY((byte)0x2),
	BUSY((byte)0x3),
	OFFLINE((byte)0x4);
	
	private final byte status;

	Status(byte value) {
        this.status = value;
    }
	
	public static Status fromValue(byte b)
	{
		for(Status s : values())
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
