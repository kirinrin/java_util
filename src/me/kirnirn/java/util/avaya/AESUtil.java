package me.kirnirn.java.util.avaya;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AESUtil {
	/*
	 * 不能跨平台 加密 1.构造密钥生成器 2.根据ecnodeRules规则初始化密钥生成器 3.产生密钥 4.创建和初始化密码器 5.内容加密
	 * 6.返回字符串
	 */
	public static String AESEncodeJava(String encodeRules, String content) {
		try {
			// 1.构造密钥生成器，指定为AES算法,不区分大小写
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			// 2.根据ecnodeRules规则初始化密钥生成器

			// 解决linux不能解密
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(encodeRules.getBytes());
			// 解决linux无法解密的问题

			// 生成一个128位的随机源,根据传入的字节数组

			keygen.init(128, secureRandom);
			// 3.产生原始对称密钥
			SecretKey original_key = keygen.generateKey();
			// 4.获得原始对称密钥的字节数组
			byte[] raw = original_key.getEncoded();
			// 5.根据字节数组生成AES密钥
			SecretKey key = new SecretKeySpec(raw, "AES");
			// 6.根据指定算法AES自成密码器
			Cipher cipher = Cipher.getInstance("AES");
			// 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// 8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
			byte[] byte_encode = content.getBytes("utf-8");
			// 9.根据密码器的初始化方式--加密：将数据加密
			byte[] byte_AES = cipher.doFinal(byte_encode);
			// 10.将加密后的数据转换为字符串
			// 这里用Base64Encoder中会找不到包
			// 解决办法：
			// 在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
			Base64 base = new Base64();
			String AES_encode = base.encodeAsString(byte_AES);

			// 11.将字符串返回
			return AES_encode;
		} catch (NoSuchAlgorithmException e) {
			log.error("AESEncode key:{} content:{}  NoSuchAlgorithmException:", encodeRules, content, e);
		} catch (NoSuchPaddingException e) {
			log.error("AESEncode key:{} content:{}  NoSuchPaddingException:", encodeRules, content, e);
		} catch (InvalidKeyException e) {
			log.error("AESEncode key:{} content:{}  InvalidKeyException:", encodeRules, content, e);
		} catch (IllegalBlockSizeException e) {
			log.error("AESEncode key:{} content:{}  IllegalBlockSizeException:", encodeRules, content, e);
		} catch (BadPaddingException e) {
			log.error("AESEncode key:{} content:{}  BadPaddingException:", encodeRules, content, e);
		} catch (UnsupportedEncodingException e) {
			log.error("AESEncode key:{} content:{} UnsupportedEncodingException:", encodeRules, content, e);
		} catch (Exception e) {
			log.error("AESEncode key:{} content:{} Exception:", encodeRules, content, e);
		}

		// 如果有错就返加nulll
		return null;
	}

	/*
	 * 解密 解密过程： 1.同加密1-4步 2.将加密后的字符串反纺成byte[]数组 3.将加密内容解密
	 */
	public static String AESDecodeJava(String encodeRules, String content) {
		try {
			// 1.构造密钥生成器，指定为AES算法,不区分大小写
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			// 2.根据ecnodeRules规则初始化密钥生成器
			// 解决linux不能解密
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(encodeRules.getBytes());
			// 解决linux无法解密的问题

			// 生成一个128位的随机源,根据传入的字节数组
			keygen.init(128, secureRandom);
			// 3.产生原始对称密钥
			SecretKey original_key = keygen.generateKey();
			// 4.获得原始对称密钥的字节数组
			byte[] raw = original_key.getEncoded();
			// 5.根据字节数组生成AES密钥
			SecretKey key = new SecretKeySpec(raw, "AES");
			// 6.根据指定算法AES自成密码器
			Cipher cipher = Cipher.getInstance("AES");
			// 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cipher.init(Cipher.DECRYPT_MODE, key);
			// 8.将加密并编码后的内容解码成字节数组
			Base64 base = new Base64();
			byte[] byte_content = base.decode(content.getBytes("utf-8"));

			/*
			 * 解密
			 */
			byte[] byte_decode = cipher.doFinal(byte_content);
			String AES_decode = new String(byte_decode, "utf-8");
			return AES_decode;
		} catch (NoSuchAlgorithmException e) {
			log.error("AESEncode key:{} content:{}  NoSuchAlgorithmException:", encodeRules, content, e);
		} catch (NoSuchPaddingException e) {
			log.error("AESEncode key:{} content:{}  NoSuchPaddingException:", encodeRules, content, e);
		} catch (InvalidKeyException e) {
			log.error("AESEncode key:{} content:{}  InvalidKeyException:", encodeRules, content, e);
		} catch (IOException e) {
			log.error("AESEncode key:{} content:{}  IOException:", encodeRules, content, e);
		} catch (IllegalBlockSizeException e) {
			log.error("AESEncode key:{} content:{}  IllegalBlockSizeException:", encodeRules, content, e);
		} catch (BadPaddingException e) {
			log.error("AESEncode key:{} content:{}  BadPaddingException:", encodeRules, content, e);
		} catch (Exception e) {
			log.error("AESEncode key:{} content:{}  Exception:", encodeRules, content, e);
		}

		// 如果有错就返加nulll
		return null;
	}

	/**
	 * 兼容全平台，就是全部加密都是 AES/CBC/ZeroPadding 128位模式；
	 * 
	 * 
	 * JS <script src="aes.js"></script> <script src="pad-zeropadding.js"></script>
	 * <script> var data = "Test String"; var key =
	 * CryptoJS.enc.Latin1.parse('1234567812345678'); var iv =
	 * CryptoJS.enc.Latin1.parse('1234567812345678');
	 * 
	 * //加密 var encrypted =
	 * CryptoJS.AES.encrypt(data,key,{iv:iv,mode:CryptoJS.mode.CBC,padding:CryptoJS.pad.ZeroPadding});
	 * 
	 * document.write(encrypted.ciphertext); document.write('<br/>
	 * ); document.write(encrypted.key); document.write('<br/>
	 * '); document.write(encrypted.iv); document.write('<br/>
	 * ); document.write(encrypted.salt); document.write('<br/>
	 * '); document.write(encrypted); document.write('<br/>
	 * ');
	 * 
	 * 
	 * //解密 var decrypted =
	 * CryptoJS.AES.decrypt(encrypted,key,{iv:iv,padding:CryptoJS.pad.ZeroPadding});
	 * console.log(decrypted.toString(CryptoJS.enc.Utf8)); </script>
	 * 
	 * //https://my.oschina.net/Jacker/blog/86383
	 * 
	 * @return
	 */
	public static String AESEncodeCommon() {
		return null;
	}

	/**
	 * 
	 * var text = "The quick brown fox jumps over the lazy dog. 👻 👻"; var secret =
	 * "René Über"; var encrypted = CryptoJS.AES.encrypt(text, secret); encrypted =
	 * encrypted.toString(); console.log("Cipher text: " + encrypted);
	 * 
	 * out =
	 * U2FsdGVkX1+tsmZvCEFa/iGeSA0K7gvgs9KXeZKwbCDNCs2zPo+BXjvKYLrJutMK+hxTwl/hyaQLOaD7LLIRo2I5fyeRMPnroo6k8N9uwKk=
	 * 
	 * @param secret
	 * @param cipherText
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static String AESDecodeCryptoJS(String secret, String cipherText) throws Exception {

		Base64 base = new Base64();
		byte[] cipherData = base.decode(cipherText);
		byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("md5方法不存在", e);
			throw e;
		}
		final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
		SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
		IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

		byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
		Cipher aesCBC;
		try {
			aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			log.error("AES/CBC/PKCS5Padding方法出错", e);
			throw e;
		}
		try {
			aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			log.error("Cipher init 出错", e);
			throw e;
		}
		byte[] decryptedData = null;
		try {
			decryptedData = aesCBC.doFinal(encrypted);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			log.error("Cipher 解码出错", e);
			throw e;
		}
		String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
		log.info("解密文本 cipherText={}, decode={}", decryptedText);
		return decryptedText;
	}

	public static String AESDecodeCryptoJS2(String secret, String cipherText) throws Exception {

		Base64 base = new Base64();
		byte[] cipherData = base.decode(cipherText);
		byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

		MessageDigest md5 = MessageDigest.getInstance("MD5");
		final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
		SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
		IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

		byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
		Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] decryptedData = aesCBC.doFinal(encrypted);
		String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

		System.out.println(decryptedText);
		return decryptedText;
	}

	/**
	 * Generates a key and an initialization vector (IV) with the given salt and
	 * password.
	 * <p>
	 * This method is equivalent to OpenSSL's EVP_BytesToKey function (see
	 * https://github.com/openssl/openssl/blob/master/crypto/evp/evp_key.c). By
	 * default, OpenSSL uses a single iteration, MD5 as the algorithm and UTF-8
	 * encoded password data.
	 * </p>
	 * 
	 * @param keyLength
	 *            the length of the generated key (in bytes)
	 * @param ivLength
	 *            the length of the generated IV (in bytes)
	 * @param iterations
	 *            the number of digestion rounds
	 * @param salt
	 *            the salt data (8 bytes of data or <code>null</code>)
	 * @param password
	 *            the password data (optional)
	 * @param md
	 *            the message digest algorithm to use
	 * @return an two-element array with the generated key and IV
	 */
	public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password,
			MessageDigest md) {

		int digestLength = md.getDigestLength();
		int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
		byte[] generatedData = new byte[requiredLength];
		int generatedLength = 0;

		try {
			md.reset();

			// Repeat process until sufficient data has been generated
			while (generatedLength < keyLength + ivLength) {

				// Digest data (last digest if available, password data, salt if available)
				if (generatedLength > 0)
					md.update(generatedData, generatedLength - digestLength, digestLength);
				md.update(password);
				if (salt != null)
					md.update(salt, 0, 8);
				md.digest(generatedData, generatedLength, digestLength);

				// additional rounds
				for (int i = 1; i < iterations; i++) {
					md.update(generatedData, generatedLength, digestLength);
					md.digest(generatedData, generatedLength, digestLength);
				}

				generatedLength += digestLength;
			}

			// Copy key and IV into separate byte arrays
			byte[][] result = new byte[2][];
			result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
			if (ivLength > 0)
				result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

			return result;

		} catch (DigestException e) {
			throw new RuntimeException(e);

		} finally {
			// Clean out temporary data
			Arrays.fill(generatedData, (byte) 0);
		}
	}
}
