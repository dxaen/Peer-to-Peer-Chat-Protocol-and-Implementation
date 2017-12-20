/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s2wmp.packets;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
//import java.security.Key;
//import java.security.KeyFactory;
//import java.security.spec.EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;

import s2wmp.UserID;
import s2wmp.Util;
//import s2wmp.crypto.KeyGenerator;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;
/**
 *
 * @author zad23, edited by smm562
 */
public class UserBroadCastPacket extends Packet {
    
	private Date timestamp; // 8 bytes - Total: 10 bytes
	private UserID userID; // 16 bytes - Total: 26 bytes
	private String userName;//16 bytes - Total: 42
	boolean isReply; 		// 1 byte  - Total: 43 bytes
	//private UserID clientID; // 16 bytes - Total: 42 bytes
	// private Key pubKey;// takes up rest of packet, either 226 or 227 bytes
	
	public UserBroadCastPacket(UserID id) {
    	super(PacketVersion.V1,PacketType.USERBROADCAST);
    	this.timestamp = new Date(System.currentTimeMillis());
    	this.userID = id;
    	isReply = false;
    	userName = "Unknown";
    	//this.clientID = new UserID(Util.genID());
    	// KeyGenerator KG = new KeyGenerator();
    	// KG.GenerateKey();
    	// this.pubKey = KG.pubKey;
    }
    
    /*public UserBroadCastPacket(UserID userID, UserID clientID)
    {
		super(PacketVersion.V1,PacketType.USERBROADCAST);
		this.timestamp = new Date(System.currentTimeMillis());
    	this.userID = userID;
    	//this.clientID = clientID;
    	// KeyGenerator KG = new KeyGenerator();
    	// KG.GenerateKey();
    	// this.pubKey = KG.pubKey;
    }*/
    
    public UserBroadCastPacket(byte[] data) throws Exception {
    	super(data,PacketVersion.V1,PacketType.USERBROADCAST);
    	//ByteBuffer timebuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 2, 10));
    	this.setTimestamp(new Date(Util.bytesToLong(Arrays.copyOfRange(data, 2, 10))));
    	this.setUserID(new UserID(Arrays.copyOfRange(data, 10, 26)));
    	this.userName = new String(Arrays.copyOfRange(data, 26, 42)).trim();
    	this.isReply = (data[42] == 1);
    	//this.setClientID(new UserID(Arrays.copyOfRange(data, 26, 42)));
    	// KeyFactory kf = KeyFactory.getInstance("DH");
    	// EncodedKeySpec keyspec = new X509EncodedKeySpec(Arrays.copyOfRange(data, 42, data.length));
    	// this.pubKey = kf.generatePublic(keyspec);
    }
    
    @Override
	public byte[] toByteArray() throws IOException {
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeLong(timestamp.getTime());
		out.write(userID.toBytes());
		String tempName = new String(userName);
		
		if(tempName.length() < 16)
		{
			int i = tempName.length();
			while(i++ < 16)
				tempName += " ";
		}
		out.writeChars(tempName);
		out.writeBoolean(isReply);
		//out.write(clientID.toBytes());
		// out.write(pubKey.getEncoded());
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

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
	}
	
	public void setReply(boolean r)
	{
		isReply = r;
	}
	
	public boolean isReply()
	{
		return isReply;
	}
	
	public void setUserName(String name)
	{
		this.userName = name;
	}
	
	public String getUserName()
	{
		return userName;
	}

	/*public UserID getClientID() {
		return clientID;
	}

	public void setClientID(UserID clientID) {
		this.clientID = clientID;
	}*/
    
}
