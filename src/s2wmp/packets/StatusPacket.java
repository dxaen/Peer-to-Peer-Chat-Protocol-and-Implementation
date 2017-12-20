package s2wmp.packets;

import java.io.*;
import java.util.Arrays;

import s2wmp.Profile;
import s2wmp.UserID;
import s2wmp.Util;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;
import s2wmp.enums.Status;

public class StatusPacket extends Packet {
	
	private Status status; // 1 byte - Total: 3 bytes
	private UserID requestID; // 16 bytes - Total: 19 bytes
	
	public StatusPacket(Profile p) {
		super(PacketVersion.V1,PacketType.STATUS);
		status = p.getStatus();
		requestID = new UserID(Util.genID());
	}
	
	public StatusPacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.STATUS);
		status = Status.values()[data[2] - 1];
		requestID = new UserID(Arrays.copyOfRange(data, 3, 19));
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeByte(status.getByte());
		out.write(requestID.toBytes());
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}

	public UserID getRequestID() {
		return requestID;
	}

	public void setRequestID(UserID requestID) {
		this.requestID = requestID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
