package test;

import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

public class ENC {
	
	 public static String ENCRYPT(String _value)
	 {
		 System.out.println("INPUT: " + _value);
		 try
		 {
			 String out = toHexString(getSHA(_value));
			 System.out.println("OUT: " +out + " L:"+out.length());
			 return out;
		 }
		 catch(Exception e) 
		 {
			 System.err.println("ENC PROBLEM");
		 }
		 return "";
	 }
	 
	 private  static byte[] getSHA(String input) throws NoSuchAlgorithmException 
	    {  
	        // Static getInstance method is called with hashing SHA  
	        MessageDigest md = MessageDigest.getInstance("SHA-256");  
	  
	        // digest() method called  
	        // to calculate message digest of an input  
	        // and return array of byte 
	        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
	    } 
	    
	   private static String toHexString(byte[] hash) 
	    { 
	        // Convert byte array into signum representation  
	        BigInteger number = new BigInteger(1, hash);  
	  
	        // Convert message digest into hex value  
	        StringBuilder hexString = new StringBuilder(number.toString(16));  
	  
	        // Pad with leading zeros 
	        while (hexString.length() < 32)  
	        {  
	            hexString.insert(0, '0');  
	        }  
	        return hexString.toString();  
	    } 
}
