package s2wmp;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKey;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

import org.junit.Test;

import s2wmp.crypto.AESCrypto;
import s2wmp.crypto.KeyGenerator;
import s2wmp.crypto.KeyManager;
import s2wmp.enums.*;
import s2wmp.packets.*;

public class S2WMP_UnitTest {

	@Test
	public void GenerateIDTest() {
		byte[] test = Util.genID();
		assert(test.length == 16);
		SecureRandom rand = new SecureRandom();
		BigInteger x = new BigInteger(5,rand);
		byte[] test2 = x.toByteArray();
		assertEquals(test2.length,1);
	}
	
	@Test
	public void PacketTypesTest() {
		assertEquals(PacketType.MESSAGING.getPacketType(),0x1);
		assertEquals(PacketType.REGISTRATION.getPacketType(),0x2);
		assertEquals(PacketType.STATUS.getPacketType(),0x3);
		assertEquals(PacketType.LOOKUP.getPacketType(),0x4);
		assertEquals(PacketType.LOOKUP_RESPONSE.getPacketType(),0x5);
		assertEquals(PacketType.ACK.getPacketType(),0x6);
		assertEquals(PacketType.USERBROADCAST.getPacketType(),0x7);
	}
	
	@Test
	public void IPAddressTest() {
		try {
			InetAddress ip = InetAddress.getByName("192.168.10.5");
			byte[] bytes = ip.getAddress();
			assertEquals(bytes.length,4);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void MessagingPacketTest() throws Exception {
		Profile p = new Profile();
		User u = new User();
		MessagingPacket packet = new MessagingPacket(u, p);
		byte[] data = packet.toByteArray();
		MessagingPacket packet2 = new MessagingPacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void RegistrationPacketTest() throws Exception {
		
		RegistrationPacket packet = new RegistrationPacket(new Profile("uname", Util.getHash("test client")));
		byte[] data = packet.toByteArray();
		RegistrationPacket packet2 = new RegistrationPacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void ResponsePacketTest() throws Exception {
		RegistrationResponsePacket packet = new RegistrationResponsePacket();
		byte[] data = packet.toByteArray();
		RegistrationResponsePacket packet2 = new RegistrationResponsePacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void LookupPacketTest() throws Exception 
	{
		LookupPacket packet = new LookupPacket(new UserID(), new UserID());
		byte[] data = packet.toByteArray();
		LookupPacket packet2 = new LookupPacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void LookupResponsePacketTest() throws Exception 
	{

		LookupResponsePacket packet = new LookupResponsePacket(new UserID(), new UserID());
		packet.setStatus(LookupStatus.FOUND);
		byte[] data = packet.toByteArray();
		LookupResponsePacket packet2 = new LookupResponsePacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void ACKPacketTest() throws Exception {
		ACKPacket packet = new ACKPacket();
		byte[] data = packet.toByteArray();
		ACKPacket packet2 = new ACKPacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void UserBroadCastPacketTest() throws Exception {
		UserBroadCastPacket packet = new UserBroadCastPacket(new UserID());
		packet.setReply(false);
		packet.setUserName("TestName");
		byte[] data = packet.toByteArray();
		UserBroadCastPacket packet2 = new UserBroadCastPacket(data);
		assert(packet.equals(packet2));
	}
	
	@Test
	public void StatusTest() {
		Status s = Status.values()[Status.AWAY.getByte() - 1];
		assertEquals(Status.AWAY,s);
	}
	
	@Test
	public void testJSON() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		Profile p = new Profile("George Washington","Mount Vernon");
		try
		{
			p.addFriend(new User("Alexander Hamilton",new UserID(Util.genID())));
			p.addFriend(new User("Thomas Jefferson",new UserID(Util.genID())));
			p.addFriend(new User("John Adams",new UserID(Util.genID())));
			Util.saveProfile(p,"myprofile.json");
			
		} catch (Exception e)
		{
			assert(false);
			return;
		}
		Profile p2 = Util.loadProfile("myprofile.json");
		assert(p.equals(p2));
		
	}
	
	@Test
	public void testCrypto() throws Exception
	{
		s2wmp.crypto.Crypto crypt = s2wmp.crypto.CryptoFactory.getInstance("AES");
        String original = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc gravida justo augue, sed blandit ex volutpat eu. "
        		+ "Vivamus aliquam nulla at pulvinar congue. Mauris tempus justo a augue tempor, nec vehicula odio viverra. "
        		+ "Nam placerat enim et enim vestibulum, at scelerisque risus gravida. Proin convallis blandit massa sit amet placerat. "
        		+ "Praesent sollicitudin enim eu nibh imperdiet vehicula facilisis et nunc. Aliquam aliquet tempor justo consequat vestibulum. "
        		+ "Vestibulum vitae augue sit amet enim tempor facilisis. Sed non orci at felis lobortis consequat eu non ante. Pellentesque "
        		+ "pellentesque sit amet metus id finibus. In tincidunt tincidunt semper. In mattis aliquam imperdiet. "
        		+ "Donec elementum ut odio vitae tincidunt. ";
        KeyGenerator KG = new KeyGenerator();
		KeyGenerator KG1 = new KeyGenerator();
        KG.GenerateKey();
        KG1.GenerateKey();
        SecretKey secretKey = KeyManager.agreeSecretKey(KG1.getPublicKey(), KG.getPrivateKey());
        SecretKey secretKey1 = KeyManager.agreeSecretKey(KG.getPublicKey(), KG1.getPrivateKey());
        
        String encrypted = Util.bytesToHex(crypt.Encrypt(original, secretKey));
        String decrypted = new String(crypt.Decrypt(Util.hexToBytes(encrypted), secretKey1));
        assert(original.equals(decrypted));
	}
	
	@Test
	public void testKeyAgreement() throws Exception
	{
		KeyGenerator KG = new KeyGenerator();
		KeyGenerator KG1 = new KeyGenerator();
        KG.GenerateKey();
        KG1.GenerateKey();
        SecretKey secretKey = KeyManager.agreeSecretKey(KG1.getPublicKey(), KG.getPrivateKey());
        SecretKey secretKey1 = KeyManager.agreeSecretKey(KG.getPublicKey(), KG1.getPrivateKey());
        assert(secretKey.equals(secretKey1));
	}
	
	@Test
	public void testInvalidAlgorithm()
	{
		s2wmp.crypto.Crypto crypt = s2wmp.crypto.CryptoFactory.getInstance("NotAnAlgorithm");
		assert(crypt == null);
	}
	
	@Test
	public void testLoadKeys() throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		KeyManager set1 = new KeyManager();
		KeyManager set2 = null;
		try
		{
			set2 = new KeyManager(set1.getPublicKey().getEncoded(), set1.getPrivateKey().getEncoded());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		assert(set1.getPublicKey().equals(set2.getPublicKey()) 
				&& set1.getPrivateKey().equals(set2.getPrivateKey()));
	}
	
	@Test
	public void testChatRequest()
	{
		try 
		{
			Profile p = new Profile();
			KeyManager k2 = new KeyManager();
			
			User user = new User();
			user.setName("TestUser");
			
			user.setPublicKey(k2.getPublicKey());

			ChatRequestPacket packet = new ChatRequestPacket(p, user);
			ChatRequestPacket p2 = new ChatRequestPacket(packet.toByteArray());
			
			assert(p2.equals(packet));
			assert(p2.getPublicKey().equals(p.getKeyManager().getPublicKey()));
			
		} 		
		catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
