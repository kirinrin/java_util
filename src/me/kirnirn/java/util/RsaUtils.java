package me.kirnirn.java.util;


import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaUtils{
	
	private static PrivateKey  privateKey = null;
	private static PublicKey  publicKey = null;
	
	private static final String DEFAULT_PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ0NjeNFH8SBVvPE4AZZ8xjcffHZc2ujUYI9PZRpQfrBNzi48sIctPwJ7mK7K33osJWqnHeopTRFqJQFm4Zku/WLxCiBRoPzw76KRLYF6q2JeZJHn+nHhs1ApzAHGUGV/DW0YeOVbCjU+ldNrlx4hf+zpB4uPHINZ6kUCx2gNmFQIDAQAB";
	private static final String DEFAULT_PRIVATE_KEY_STRING = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAInQ2N40UfxIFW88TgBlnzGNx98dlza6NRgj09lGlB+sE3OLjywhy0/AnuYrsrfeiwlaqcd6ilNEWolAWbhmS79YvEKIFGg/PDvopEtgXqrYl5kkef6ceGzUCnMAcZQZX8NbRh45VsKNT6V02uXHiF/7OkHi48cg1nqRQLHaA2YVAgMBAAECgYA1M97EyUXtMpazvoAOPyzZoHZMd8vsYK4mh449DrYYFAHeVT4MN2+Zdi7hhHUV7l+WWN26GGBWDMk2uxTnzvXmchIGwfqMtqEfDvBlKy9Lu/bTFUXWoVtYyJ7JVEIiWJtTQSwJP3owGFcZeP+jk6iJhv5mOnmWMKeMYstlUZ8DcQJBANgpPXgWRnKr+6G8RheOsy0Od4lX4pFlPqtNh/4erfUMtuCoO5nfLNXHHDglbi8zwpo4TpxY0S9Nxvf+xngwt3sCQQCjNyzZQhoEHbtwj1cgspgTxYdRIwqbYuSeHih1cC0QzIN36YHakLlErOd12Oq2CZqXSM72FO5Px/POxEk7VBuvAkBGOj/KQBJrNwztoulyxd3YiZa0fGUTr70IkovYN8d0kcjofD/A4g0C90lnxGFj3IEg8aI4kXKE19QIxjFc4xb5AkB9f3dktob6k0IFErKT2b9FtKSPproJMMlJKzdA1bhRKnAMS+gk/xL1011GKDyRLGFJ4hoA7acwgTbezd7hV5LRAkAESZmCfAfpqIlPyTnkN/ln9J5KpflouWbVVsAchAxw/tvf1ndXgVz4c4Cu35RRnoNvxQ6j6zPfr/yu/P6HoHof";

	
	private static final String  METHOD= "RSA";
	public static KeyPair genKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator;
			keyPairGenerator = KeyPairGenerator.getInstance(METHOD);
			keyPairGenerator.initialize(1024); // 密钥长度推荐为1024位.
			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
			
			
//			log.info("公钥 " + org.apache.commons.codec.binary.Base64.encodeBase64String(publicKey.getEncoded()));
//			log.info("私钥 " + org.apache.commons.codec.binary.Base64.encodeBase64String(privateKey.getEncoded()));
			return keyPair;
	}
	
	public static void initPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes;
		keyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
		publicKey = keyFactory.generatePublic(keySpec);
	}
	
	public static void initPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes;
        keyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(METHOD);
        privateKey = keyFactory.generatePrivate(keySpec);
	}
	
	public static String genEncryptData(String painTesxt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		if(privateKey == null) {
			initPrivateKey(DEFAULT_PRIVATE_KEY_STRING);
		}
		Cipher cipher = Cipher.getInstance(METHOD);
		cipher.init(Cipher.ENCRYPT_MODE, privateKey, new SecureRandom());
		byte[] cipherData = cipher.doFinal(painTesxt.getBytes());
		String enData = org.apache.commons.codec.binary.Base64.encodeBase64String(cipherData);

		return enData;
	}
	
	public static String descryptText(String encrptyData) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		if(publicKey == null) {
			initPublicKey(DEFAULT_PUBLIC_KEY_STRING);
		}
		Cipher cipher = Cipher.getInstance(METHOD);
		cipher.init(Cipher.DECRYPT_MODE, publicKey, new SecureRandom());
		byte[] data = org.apache.commons.codec.binary.Base64.decodeBase64(encrptyData);
		byte[] plainData = cipher.doFinal(data);
		
		return new String(plainData);
	}
}
