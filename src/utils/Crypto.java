package utils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public final class Crypto{

    private static final SecretKey secretKey = new SecretKeySpec("ESR-PR-MULTICAST".getBytes(), "AES");


    public static byte[] encrypt(byte[] plainText) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        return cipher.doFinal(plainText);
    }


    public static byte[] decrypt(byte[] encrypted) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        return cipher.doFinal(encrypted); 
    }
}