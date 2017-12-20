package s2wmp.crypto;
/**
 * Class: CryptoFactory
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * Instantiate different algorithms. Currently only one supported
 */
public class CryptoFactory 
{
	public static Crypto getInstance(String algorithm)
	{
		switch(algorithm.toUpperCase())
		{
		case "AES":
			return new AESCrypto();
		default:
			return null;
		}
	}
}
