package s2wmp.enums;

public enum PacketType {
	MESSAGING((byte)0x1),
	REGISTRATION((byte)0x2),
	STATUS((byte)0x3),
	LOOKUP((byte)0x4),
	LOOKUP_RESPONSE((byte)0x5),
	ACK((byte)0x6),
	USERBROADCAST((byte)0x7),
	CHAT_REQUEST((byte)0x8),
	REG_RESPONSE((byte)0x9);
	
	private final byte type;

	PacketType(byte value) {
		this.type = value;
    }
	public static PacketType fromValue(byte b)
	{
		for(PacketType s : values())
		{
			if(s.type == b)
			{
				return s;
			}
		}
		throw new IllegalArgumentException();
	}
	public byte getPacketType()
	{
		return type;
	}
}
