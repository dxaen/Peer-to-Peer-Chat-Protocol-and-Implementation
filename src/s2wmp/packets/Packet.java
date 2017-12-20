package s2wmp.packets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import s2wmp.enums.PacketType;
import s2wmp.enums.PacketVersion;

public abstract class Packet {
	
	protected PacketVersion version; // 1 byte - Total: 1 byte
	protected PacketType type; // 1 byte - Total: 2 bytes
	
	protected static final SecureRandom rand = new SecureRandom();
	protected static final Charset mychar = StandardCharsets.UTF_8;
	
	protected Packet(byte[] data, PacketVersion version, PacketType type) throws Exception {
		this(version,type);
		if (data[0] != version.getPacketVersion() || data[1] != type.getPacketType()) {
			throw new Exception("Invalid Packet Type");
		}
	}
	
	protected Packet(PacketVersion version, PacketType type) {
		this.version = version;
		this.type = type;
	}
	
	public PacketVersion getVersion() {
		return version;
	}
	
	public PacketType getType() {
		return type;
	}
	
	public abstract byte[] toByteArray() throws IOException;
}