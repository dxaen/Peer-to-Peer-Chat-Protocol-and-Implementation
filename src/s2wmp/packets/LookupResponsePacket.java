package s2wmp.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.Arrays;

import s2wmp.UserID;
import s2wmp.Util;
import s2wmp.enums.LookupStatus;
import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;

public class LookupResponsePacket extends Packet 
{

	private Date timestamp; // 8 bytes - Total: 10 bytes
	private LookupStatus status; // 1 byte - Total: 11 bytes
	private UserID requestID; // 16 bytes - Total: 27 bytes
	private UserID userID; // 16 bytes - Total: 43 bytes
	private UserID lookupID; // 16 bytes - Total: 59 bytes
	private InetAddress ipv4Address; // 4 bytes - Total: 63 bytes
	//private InetAddress ipv6Address; // 16 bytes - 79 bytes
	
	public LookupResponsePacket(UserID lookup, UserID requestor) throws UnknownHostException 
	{
		super(PacketVersion.V1,PacketType.LOOKUP_RESPONSE);
		this.timestamp = new Date(System.currentTimeMillis());
		status = LookupStatus.FOUND;
		this.requestID = new UserID(Util.genID());
		this.userID = lookup;
		this.lookupID = requestor;
		this.ipv4Address = InetAddress.getByAddress(rand.generateSeed(4));
		//this.ipv6Address = InetAddress.getByAddress(rand.generateSeed(16));
	}
	
	public LookupResponsePacket(byte[] data) throws Exception 
	{
		super(data,PacketVersion.V1,PacketType.LOOKUP_RESPONSE);
		//ByteBuffer timeBuffer = ByteBuffer.wrap(Arrays.copyOfRange(data, 2, 10));
		timestamp = new Date(Util.bytesToLong(Arrays.copyOfRange(data, 2, 10)));
		status = LookupStatus.fromValue(data[10]);
		requestID = new UserID(Arrays.copyOfRange(data, 11, 27));
		userID = new UserID(Arrays.copyOfRange(data, 27, 43));
		lookupID = new UserID(Arrays.copyOfRange(data, 43, 59));
		ipv4Address = InetAddress.getByAddress(Arrays.copyOfRange(data, 59, 63));
		//ipv6Address = InetAddress.getByAddress(Arrays.copyOfRange(data, 63, 79));
	}

	
	@Override
	public byte[] toByteArray() throws IOException 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		out.writeByte(version.getPacketVersion());
		out.writeByte(type.getPacketType());
		out.writeLong(timestamp.getTime());
		out.writeByte(status.getByte());
		out.write(requestID.toBytes());
		out.write(userID.toBytes());
		out.write(lookupID.toBytes());
		out.write(ipv4Address.getAddress());
		//out.write(ipv6Address.getAddress());
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

	public LookupStatus getStatus() {
		return status;
	}

	public void setStatus(LookupStatus status) {
		this.status = status;
	}

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

	public InetAddress getIPv4Address() {
		return ipv4Address;
	}

	public void setIPv4Address(InetAddress ipv4Address) {
		this.ipv4Address = ipv4Address;
	}

	/*public InetAddress getIPv6Address() {
		return ipv6Address;
	}

	public void setIPv6Address(InetAddress ipv6Address) {
		this.ipv6Address = ipv6Address;
	}*/

}
