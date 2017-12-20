package s2wmp.crypto;

import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import s2wmp.Util;
/**
 * Class: CryptoHolder
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * Responsible for managing and saving keys, including creating a shared key
 */
public class KeyManager 
{
	byte[] pub, priv;
	
	public KeyManager() throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		KeyGenerator gen = new KeyGenerator();
		gen.GenerateKey();
		pub = gen.getPublicKey().getEncoded();
		priv = gen.getPrivateKey().getEncoded();
	}
	
	public KeyManager(byte [] pub, byte [] priv) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		this.priv = priv;
		this.pub = pub;
	}
	
	public KeyManager(String pub, String priv) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		this(Util.hexToBytes(pub), Util.hexToBytes(priv));
	}
	
	public void setPublicKey(Key k)
	{
		pub = k.getEncoded();
	}
	public void setPrivateKey(Key k)
	{
		priv = k.getEncoded();
	}
	
	public Key getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		KeyFactory kf = KeyFactory.getInstance("DH");
		return kf.generatePublic(new X509EncodedKeySpec(pub));
	}
	
	public Key getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		KeyFactory kf = KeyFactory.getInstance("DH");
		return kf.generatePrivate(new PKCS8EncodedKeySpec(priv));
	}
	
	/**
     * 
     * @param pub public key of the other party
     * @param prv my private key
     * @return A common secret based on internal private key and public key provided
     * @throws Exception
     */
    public static SecretKey agreeSecretKey(Key pub, Key prv) throws Exception 
    {
    	KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(prv);
        //find common secret
        ka.doPhase(pub, true);
        
        byte[] secret = ka.generateSecret();

        //generate AES key based on the secret
        MessageDigest sha1 = MessageDigest.getInstance("SHA1"); 
        byte[] bkey = Arrays.copyOf(sha1.digest(secret), 128 / Byte.SIZE);

        return new SecretKeySpec(bkey, "AES");
    }
}
