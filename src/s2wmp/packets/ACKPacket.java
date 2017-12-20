package s2wmp.packets;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

import s2wmp.enums.MessageError;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;
import s2wmp.UserID;
import s2wmp.Util;

public class ACKPacket extends Packet {
	
	private boolean ack;
	private MessageError err;
	private Date timestamp;
	private UserID userID;
	
	public ACKPacket() {
		super(PacketVersion.V1,PacketType.ACK);
		this.ack = rand.nextBoolean();
		this.err = MessageError.NONE;
		this.timestamp = new Date(System.currentTimeMillis());
		this.userID = new UserID(Util.genID());
	}
	
	public ACKPacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.ACK);
		this.ack = (data[2] == 1);
		this.err = MessageError.values()[data[3]];
		this.timestamp = new Date(Util.bytesToLong(Arrays.copyOfRange(data, 4, 12)));
		userID = new UserID(Arrays.copyOfRange(data, 12, 28));
	}
	
	public ACKPacket(boolean ack, byte err, Date timestamp, UserID userID) {
		this();
		this.ack = ack;
		this.err = MessageError.fromValue(err);
		this.timestamp = timestamp;
		this.userID = userID;
	}
	
	@Override
	public byte[] toByteArray() throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream stream = new ByteArrayOutputStream();		
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeBoolean(ack);
		out.writeByte(err.getError());
		out.writeLong(timestamp.getTime());
		out.write(userID.toBytes());
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}
	
	public boolean getAck() {
		return ack;
	}
	
	public void setAck(boolean ack) {
		this.ack = ack;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public MessageError getErr() {
		return err;
	}
	
	public void setErr(MessageError err) {
		this.err = err;
	}

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		
		this.userID = userID;
	}

}
