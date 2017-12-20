package s2wmp.packets;

import java.io.*;
import java.util.Arrays;

import s2wmp.UserID;
import s2wmp.Util;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;
import s2wmp.enums.RegistrationStatus;

public class RegistrationResponsePacket extends Packet {
	
	private RegistrationStatus status; // 1 byte - Total: 3 bytes
	private UserID registrationID; // 16 bytes - Total: 19 bytes
	
	public RegistrationResponsePacket() {
		super(PacketVersion.V1,PacketType.REG_RESPONSE);
		status = RegistrationStatus.fromValue((byte)rand.nextInt(RegistrationStatus.values().length));
		registrationID = new UserID(Util.genID());
	}
	
	public RegistrationResponsePacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.REG_RESPONSE);
		status = RegistrationStatus.fromValue(data[2]);
		registrationID = new UserID(Arrays.copyOfRange(data, 2, 18));
	}

	@Override 
	public boolean equals(Object o)
	{
		if(!(o instanceof RegistrationResponsePacket))
		{
			return false;
		}
		RegistrationResponsePacket p = (RegistrationResponsePacket)o;
		return this.status == p.status && this.registrationID.equals(p.registrationID);
	}
	
	@Override
	public byte[] toByteArray() throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeByte(status.getByte());
		out.write(registrationID.toBytes());
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}

	public UserID getRequestID() {
		return registrationID;
	}

	public void setRequestID(UserID requestID) {
		this.registrationID = requestID;
	}

	public RegistrationStatus getStatus() {
		return status;
	}

	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

}
