package s2wmp.enums;

public enum RegistrationStatus
{
	SUCCESS((byte)0),
	ID_COLLISION((byte)0x1),
	ERROR((byte)0x2);
	
	private final byte status;

	RegistrationStatus(byte value) {
        this.status = value;
    }
	
	public static RegistrationStatus fromValue(byte b)
	{
		for(RegistrationStatus s : values())
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
