/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s2wmp.enums;


public enum PacketVersion {
	V1((byte)0x1),
	V2((byte)0x2),
	V3((byte)0x3),
	V4((byte)0x4),
	V5((byte)0x5),
	V6((byte)0x6);
	
	private final byte type;

	PacketVersion(byte value) {
		this.type = value;
    }
	
	public byte getPacketVersion()
	{
		return type;
	}
}
