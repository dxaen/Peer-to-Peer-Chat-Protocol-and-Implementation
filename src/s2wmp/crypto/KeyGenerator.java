package s2wmp.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class: CryptoHolder
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *	Generates Keys for DH exchange
 */
public class KeyGenerator 
{
    
    final int RSAKeySize = 512;
    public Key pubKey = null;
    public Key prvKey = null;
    
    public void GenerateKey()
    {   
        try
        {
            KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("DH");
            RSAKeyGen.initialize(RSAKeySize);
            KeyPair pair = RSAKeyGen.generateKeyPair();
            pubKey = pair.getPublic();
            prvKey = pair.getPrivate();
            byte[] keybytes = pubKey.getEncoded();
            //System.out.println(keybytes.length);
        } 
        catch (GeneralSecurityException e) {
            //System.out.println(e.getLocalizedMessage() + newline);
            System.out.println("Error initialising encryption. Exiting.\n");
            System.exit(0);
        }
    }
    public Key getPrivateKey()
    {
        return prvKey;
    }
    public Key getPublicKey()
    {
        return pubKey;
    }
  
}
