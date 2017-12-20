package s2wmp.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;

import s2wmp.Profile;
import s2wmp.User;
import s2wmp.UserID;
import s2wmp.enums.ChatRequestStatus;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;

public class ChatRequestPacket extends Packet {
	
	private ChatRequestStatus status; // 1 byte - Total: 3 bytes
	private Date timestamp; // 8 bytes - Total: 11 bytes
	private UserID requesterID; // 16 bytes - Total: 27 bytes
	private UserID friendID; // 16 bytes - Total: 43 bytes
	private int keyLength; // 4 bytes - Total: 47 bytes
	private Key publicKey; // variable
	private int nameLength;
	private String username;
	
	public ChatRequestPacket(Profile p, User friend) throws NoSuchAlgorithmException, InvalidKeySpecException {
		super(PacketVersion.V1,PacketType.CHAT_REQUEST);
		this.status = ChatRequestStatus.VALID;
		this.setTimestamp(new Date(System.currentTimeMillis()));
		this.setRequesterID(p.getUID());
		this.setFriendID(friend.getUID());
		this.setPublicKey(p.getKeyManager().getPublicKey());
		this.setUsername(p.getName());
	}
	
	public ChatRequestPacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.CHAT_REQUEST);
		this.status = ChatRequestStatus.fromValue(data[2]);
		ByteBuffer timeBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 3, 11));
		this.timestamp = new Date(timeBuffer.getLong());
		this.requesterID = new UserID(Arrays.copyOfRange(data, 11, 27));
		this.friendID = new UserID(Arrays.copyOfRange(data, 27, 43));
		ByteBuffer keyLengthBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 43, 47));
		this.keyLength = keyLengthBuffer.getInt();
		KeyFactory kf = KeyFactory.getInstance("DH");
		EncodedKeySpec keyspec = new X509EncodedKeySpec(Arrays.copyOfRange(data, 47, 47 + this.keyLength));
    	this.setPublicKey(kf.generatePublic(keyspec));
    	ByteBuffer nameLengthBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 47+keyLength, 51+keyLength));
    	this.nameLength = nameLengthBuffer.getInt();
    	this.username = new String(Arrays.copyOfRange(data, 51+keyLength, 51+keyLength+nameLength),mychar);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeByte(status.getByte());
		out.writeLong(timestamp.getTime());
		out.write(requesterID.toBytes());
		out.write(friendID.toBytes());
		out.writeInt(keyLength);
		out.write(publicKey.getEncoded());
		out.writeInt(nameLength);
		out.write(username.getBytes(mychar));
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

	public UserID getRequesterID() {
		return requesterID;
	}

	public void setRequesterID(UserID requesterID) {
		this.requesterID = requesterID;
	}

	public UserID getFriendID() {
		return friendID;
	}

	public void setFriendID(UserID friendID) {
		this.friendID = friendID;
	}

	public Key getPublicKey() {
		return publicKey;
	}
	
	public int getKeyLength() {
		return keyLength;
	}

	public void setPublicKey(Key publicKey) {
		this.publicKey = publicKey;
		this.keyLength = publicKey.getEncoded().length;
	}

	public ChatRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ChatRequestStatus status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		this.nameLength = username.getBytes(mychar).length;
	}

}