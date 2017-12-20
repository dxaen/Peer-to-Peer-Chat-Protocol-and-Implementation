package s2wmp.crypto;
/**
 * Class: CryptoHolder
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * Maintains one globally accessible instance of cryptographic functions
 */
public class CryptoHolder
{
	private static Crypto crypt = null;
	
	private CryptoHolder()
	{}
	
	//thread-safe singleton instance
	public static Crypto getInstance()
	{
		if(crypt == null)
		{
			synchronized(CryptoHolder.class)
			{
				if(crypt == null)
				{
					crypt = CryptoFactory.getInstance("AES");
				}
			}
		}
		
		return crypt;
	}

}
