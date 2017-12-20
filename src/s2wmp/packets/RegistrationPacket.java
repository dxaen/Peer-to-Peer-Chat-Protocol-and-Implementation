package s2wmp.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
// import java.security.Key;
// import java.security.KeyFactory;
// import java.security.spec.EncodedKeySpec;
// import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import s2wmp.Profile;
import s2wmp.UserID;
import s2wmp.Util;
// import s2wmp.crypto.KeyGenerator;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;

public class RegistrationPacket extends Packet {
	
	private UserID userID; // 16 bytes - Total: 18 bytes
	private UserID requestID; // 16 bytes - Total: 34 bytes
	//private String clientID; // 16 bytes - Total: 50 bytes
	// private Key publicKey; // varies, could be either 226 or 227 bytes
	
	public RegistrationPacket(Profile p) throws UnknownHostException {
		super(PacketVersion.V1,PacketType.REGISTRATION);
		this.userID = p.getUID();
		this.requestID = new UserID(Util.genID());
		//this.clientID = p.getClientID();
		// KeyGenerator kg = new KeyGenerator();
		// kg.GenerateKey();
		// this.publicKey = kg.getPublicKey();
	}
	
	public RegistrationPacket(byte[] data) throws Exception {
		super(data,PacketVersion.V1,PacketType.REGISTRATION);
		userID = new UserID(Arrays.copyOfRange(data, 2, 18));
		requestID = new UserID(Arrays.copyOfRange(data, 18, 34));
		//clientID = Arrays.copyOfRange(data, 18, 34));
		// KeyFactory kf = KeyFactory.getInstance("DH");
    	// EncodedKeySpec keyspec = new X509EncodedKeySpec(Arrays.copyOfRange(data, 70, data.length));
    	// this.publicKey = kf.generatePublic(keyspec);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.write(userID.toBytes());
		out.write(requestID.toBytes());
		//out.write(clientID.toBytes());
		// out.write(publicKey.getEncoded());
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
	}

	public UserID getRequestID() {
		return requestID;
	}

	public void setRequestID(UserID requestID) {
		this.requestID = requestID;
	}
/*
	public UserID getClientID() {
		return clientID;
	}

	public void setClientID(UserID clientID) {
		this.clientID = clientID;
	}*/

}
