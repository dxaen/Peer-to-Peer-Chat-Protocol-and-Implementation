package s2wmp;
import java.security.SecureRandom;
import java.util.Arrays;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Class: Util
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 * Service class with various utility functions
 */
public class Util
{
	public static byte [] genID()
	{
		SecureRandom rand = new SecureRandom();
		byte[] array = new BigInteger(128,rand).toByteArray();
		/*if (array[0] == 0) 
		{
			byte[] tmp = new byte[array.length - 1];
			System.arraycopy(array, 1, tmp, 0, tmp.length);
			array = tmp;
		}*/
		return getHash(array);
	}
	
	public static byte [] longToBytes(long num)
	{
		byte bytes[] = new byte[8];
		
		for(int i = 7; i >= 0; --i)
		{
			bytes[i] = (byte)num;
			num >>>= 8;
		}
		
		return bytes;
	}
	
	public static byte [] hexToBytes(String hex)
	{
		byte [] bytes = new byte[hex.length() / 2];
		
		for(int i = 0; i < bytes.length; ++i )
		{
			int idx = i * 2;
			
			bytes[i] = (byte)((Character.digit(hex.charAt(idx), 16) << 4) |
					Character.digit(hex.charAt(idx+1), 16));
		}
		return bytes;
	}
	
	public static String bytesToHex(byte [] bytes)
	{
		StringBuilder str = new StringBuilder(bytes.length * 2);
		
		for(int i = 0; i < bytes.length; ++i)
		{
			str.append(String.format("%02x", bytes[i]));
		}
		return str.toString();
	}
	
	public static long bytesToLong(byte [] bytes)
	{
		long number = 0;
		for(int i = 0; i < 8; ++i)
		{
			number <<= 8;
			number |= (bytes[i] & 0xFF);				
		}
		return number;
	}
	
	public static String getHash(String val)
	{
		MessageDigest md = null;
		
		try 
		{
			md = MessageDigest.getInstance("SHA1");
		} 
		catch (NoSuchAlgorithmException e) 
		{}
		byte [] temp = md.digest(val.getBytes());
		return bytesToHex(Arrays.copyOfRange(temp, 0, 16));	
	}
	
	public static byte [] getHash(byte [] val)
	{
		MessageDigest md = null;
		
		try 
		{
			md = MessageDigest.getInstance("SHA1");
		} 
		catch (NoSuchAlgorithmException e) 
		{}
		byte [] temp = md.digest(val);
		return Arrays.copyOfRange(temp, 0, 16);	
	}
	
	public static void saveProfile(Profile profile, String filename) throws IOException {
		String jsonString = new Gson().toJson(profile);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
		out.write(jsonString);
		out.close();
	}
	
	public static Profile loadProfile(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
		String jsonString = "";
		String line;
		while ((line = in.readLine()) != null) {
			jsonString += line;
		}
		in.close();
		Profile result = new Gson().fromJson(jsonString, Profile.class);
		return result;
	}
}
