package s2wmp.packets;

import s2wmp.enums.*;
import s2wmp.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.Arrays;

public class LookupPacket extends Packet {
	
	private Date timestamp; // 8 bytes - Total: 10 bytes
	//private Status status; // 1 byte - Total: 11 bytes
	private UserID requestID; // 16 bytes - Total: 27 bytes
	private UserID userID; // 16 bytes - Total: 43 bytes
	private UserID lookupID; // 16 bytes - Total: 59 bytes
	
	public LookupPacket(UserID lookup, UserID requestor) throws UnknownHostException {
		super(PacketVersion.V1,PacketType.LOOKUP);
		this.timestamp = new Date(System.currentTimeMillis());
		//this.status = Status.values()[rand.nextInt(Status.values().length)];
		this.requestID = new UserID(Util.genID());
		this.userID = requestor;
		this.lookupID = lookup;
	}
	
	public LookupPacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.LOOKUP);
		ByteBuffer timeBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 2, 10));
		timestamp = new Date(timeBuffer.getLong());
		//status = Status.values()[data[10] - 1];
		requestID = new UserID(Arrays.copyOfRange(data, 10, 26));
		userID = new UserID(Arrays.copyOfRange(data, 26, 42));
		lookupID = new UserID(Arrays.copyOfRange(data, 42, 58));
	}

	@Override
	public byte[] toByteArray() throws IOException 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeLong(timestamp.getTime());
		//out.writeByte(status.getByte());
		out.write(requestID.toBytes());
		out.write(userID.toBytes());
		out.write(lookupID.toBytes());
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public UserID getRequestID() {
		return requestID;
	}

	public void setRequestID(UserID requestID) {
		this.requestID = requestID;
	}

	/*public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}*/

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
	}

	public UserID getLookupID() {
		return lookupID;
	}

	public void setLookupID(UserID lookupID) {
		this.lookupID = lookupID;
	}

}
