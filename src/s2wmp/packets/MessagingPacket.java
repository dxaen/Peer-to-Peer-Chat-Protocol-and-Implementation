package s2wmp.packets;

import s2wmp.enums.Status;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import s2wmp.*;

import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;

public class MessagingPacket extends Packet {

	private Status status; // 1 byte - Total: 3 bytes
	private short length; // 2 bytes - Total: 5 bytes
	private Date timestamp; // 8 bytes - Total: 13 bytes
	private UserID senderID; // 16 bytes - Total: 29 bytes
	private UserID receiverID; // 16 bytes - Total: 45 bytes
	private String clientID; // 16 bytes - Total: 61 bytes
	private String hash; // 16 bytes - Total: 77 bytes
	private byte [] encryptedMessage; // takes up rest of packet, variable size
	
	public MessagingPacket(User receiver, Profile sender) {
		super(PacketVersion.V1,PacketType.MESSAGING);
		this.status = sender.getStatus();
		this.timestamp = new Date(System.currentTimeMillis());
		this.senderID = sender.getUID();
		this.receiverID = receiver.getUID();
		this.clientID = sender.getClientID();
		this.hash = Util.getHash("Placeholder String");
		this.encryptedMessage = Util.hexToBytes("50a1e3f9d7bae63a");
		this.length = (short)encryptedMessage.length;
	}
	
	public MessagingPacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.MESSAGING);
		this.status = Status.values()[data[2] - 1];
		ByteBuffer lengthBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 3, 5));
		this.length = lengthBuffer.getShort();
		ByteBuffer timeBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 5, 13));
		timestamp = new Date(timeBuffer.getLong());
		senderID = new UserID(Arrays.copyOfRange(data, 13, 29));
		receiverID = new UserID(Arrays.copyOfRange(data, 29, 45));
		clientID = Util.bytesToHex(Arrays.copyOfRange(data, 45, 61));
		hash = Util.bytesToHex(Arrays.copyOfRange(data, 61, 77));
		encryptedMessage = Arrays.copyOfRange(data, 77, data.length);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream stream = new ByteArrayOutputStream();		
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeByte(status.getByte());
		out.writeShort(length);
		out.writeLong(timestamp.getTime());
		out.write(senderID.toBytes());
		out.write(receiverID.toBytes());
		out.write(Util.hexToBytes(clientID));
		out.write(Util.hexToBytes(hash));
		out.write(encryptedMessage);
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public short getLength() {
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public UserID getSenderID() {
		return senderID;
	}

	public void setSenderID(UserID senderID) {
		this.senderID = senderID;
	}

	public UserID getReceiverID() {
		return receiverID;
	}

	public void setReceiverID(UserID receiverID) {
		this.receiverID = receiverID;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	public void setHash(byte [] hash) {
		this.hash = Util.bytesToHex(hash);
	}

	public byte [] getEncryptedMessage() {
		return encryptedMessage;
	}

	public void setEncryptedMessage(byte [] encryptedMessage) 
	{
		setLength((short)encryptedMessage.length);
		hash = Util.bytesToHex(Util.getHash(encryptedMessage));
		this.encryptedMessage = encryptedMessage;
	}
	
	public void setEncryptedMessage(String msg)
	{
		byte [] bytes = Util.hexToBytes(msg);
		setLength((short)bytes.length);
		hash = Util.getHash(msg);
		this.encryptedMessage = bytes;
	}

}
