package me.kirnirn.java.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * licence 工具
 * 
 * @author xiapengtao 2017年9月27日
 *
 */
public class LicenceUtil {

	/**
	 * 公钥
	 */
	private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnugMjCf8n8Z3UXri1PKTupUQGj6yihuiW/EBv1UbhSyhPmiCVul0xn4HXLZHKR0qPsUfoqlJH3cjPkxLb1mm89VF6SrWiIStybg0ydjSjwIMzBSugAI5QI6dBITRxc/1xU+tkjN0pkvthiBghYQEtwgEeezuj75C/Cz5L/WLULwIDAQAB";

	/**
	 * 获取windows序列号
	 */
	@SuppressWarnings("unused")
	private static String getWinSerialNumber() {
		try {
			Process process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "SerialNumber" });
			process.getOutputStream().close();
			Scanner sc = new Scanner(process.getInputStream());
			String property = "";
			String serial = "";
			if (sc.hasNext()) {
				property = sc.next();
			}
			;

			if (sc.hasNext()) {
				serial = sc.next();
			}
			;

			if ("".equals(serial)) {
				serial = getMacAddress();
			}

			sc.close();
			// System.out.println(property + ": " + serial);
			return serial;
		} catch (Exception e) {
			// System.out.println("get serial-number error");
			System.exit(1);
		}
		return null;
	}

	/**
	 * 获取linux序列号
	 */
	private static String getLinuxSerialNumber() {
		try {
			Process process = Runtime.getRuntime()
					.exec(new String[] { "sudo", "dmidecode", "-s", "system-serial-number" });
			process.getOutputStream().close();
			Scanner sc = new Scanner(process.getInputStream());
			String serial = sc.nextLine();
			sc.close();
			return serial;
		} catch (Exception e) {
			// System.out.println("get serial-number error");
			System.exit(1);
		}
		return null;
	}

	/**
	 * 获取macOS的序列号
	 * 
	 * @return
	 */
	public static String getMacSerialNumber() {
		String sn = null;

		OutputStream os = null;
		InputStream is = null;

		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(new String[] { "/usr/sbin/system_profiler", "SPHardwareDataType" });
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		os = process.getOutputStream();
		is = process.getInputStream();

		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String marker = "Serial Number";
		try {
			while ((line = br.readLine()) != null) {
				if (line.contains(marker)) {
					sn = line.split(":")[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (sn == null) {
			throw new RuntimeException("Cannot find computer SN");
		}

		return sn;
	}

	/**
	 * 获取网卡的MAC地址
	 */
	private static String getMacAddress() throws Exception {
		Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();

		while (ni.hasMoreElements()) {
			NetworkInterface netI = ni.nextElement();

			byte[] bytes = netI.getHardwareAddress();
			if (netI.isUp() && netI != null && bytes != null && bytes.length == 6) {
				StringBuffer sb = new StringBuffer();
				for (byte b : bytes) {
					// 与11110000作按位与运算以便读取当前字节高4位
					sb.append(Integer.toHexString((b & 240) >> 4));
					// 与00001111作按位与运算以便读取当前字节低4位
					sb.append(Integer.toHexString(b & 15));
					sb.append("-");
				}
				sb.deleteCharAt(sb.length() - 1);
				return sb.toString().toUpperCase();
			}
		}
		return null;
	}

	/**
	 * 判断是否是windows操作系统
	 *
	 * @return
	 */
	public static boolean isWindows() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		return os.startsWith("win") || os.startsWith("Win");
	}

	public static boolean isMac() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name").toLowerCase();
		return os.indexOf("mac") >= 0 && os.indexOf("os") > 0;
	}

	/**
	 * 系统序列号
	 */
	private static final String SERIAL_NUMBER;
	static {
		if (isWindows()) {
			SERIAL_NUMBER = getWinSerialNumber();
		} else if (isMac()) {
			SERIAL_NUMBER = getMacSerialNumber();
		} else {
			SERIAL_NUMBER = getLinuxSerialNumber();
		}
	}

	/**
	 * 获取系统序列号
	 *
	 * @return
	 */
	private static String getSerialNumber() {
		return SERIAL_NUMBER;
	}

	/**
	 * 获取系统唯一机器码(系统唯一ID，或mac地址),然后md5求值
	 *
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getHostID() {
		
		try {
	        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(SERIAL_NUMBER.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
	        return sb.toString();
	    } catch (java.security.NoSuchAlgorithmException e) {
	    }
	    return null;
	}

	/**
	 * 从文件获取许可
	 * 
	 * @param licenceFile
	 * @return
	 */
	public static Licence getLicenceFromXmlString(String xmlString) {
		Licence licence = null;
		if (xmlString != null) {
			try {
				licence = Licence.newInstance(xmlString);
			} catch (Exception e) {
			}
		}
		return licence;
	}

	/**
	 * 从文件获取许可
	 * 
	 * @param licenceFile
	 * @return
	 */
	public static Licence getLicenseFromFile(File licenceFile) {
		Licence licence = null;
		if (licenceFile != null && licenceFile.exists()) {
			try {
				licence = Licence.newInstance(licenceFile);
			} catch (Exception e) {
			}
		}
		return licence;
	}

	/**
	 * 授权许可实体
	 * 
	 * @author xiapengtao 2017年10月18日
	 */
	@XmlRootElement
	public static class Licence {

		private String hostID; // 主机ID
		private String owner; // 客户
		private String productName;// 产品名称
		private String version; // 版本
		private long maxUsers; // 最大用户数
		private String expireDate;// 有效期，为空表示永久
		private String signature; // 许可签名

		public String getHostID() {
			return hostID;
		}

		public static Licence newInstance(File licenceFile) throws JAXBException {
			JAXBContext jaxbContext = JAXBContext.newInstance(Licence.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Licence licence = (Licence) jaxbUnmarshaller.unmarshal(licenceFile);
			return licence;
		}

		public static Licence newInstance(String xmlString) throws JAXBException {
			JAXBContext jaxbContext = JAXBContext.newInstance(Licence.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			StringReader reader = new StringReader(xmlString);

			Licence licence = (Licence) jaxbUnmarshaller.unmarshal(reader);
			return licence;
		}

		@XmlElement
		public void setHostID(String hostID) {
			this.hostID = hostID;
		}

		public String getOwner() {
			return owner;
		}

		@XmlElement
		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getProductName() {
			return productName;
		}

		@XmlElement
		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getVersion() {
			return version;
		}

		@XmlElement
		public void setVersion(String version) {
			this.version = version;
		}

		public long getMaxUsers() {
			return maxUsers;
		}

		@XmlElement
		public void setMaxUsers(long maxUsers) {
			this.maxUsers = maxUsers;
		}

		public String getExpireDate() {
			return expireDate;
		}

		@XmlElement
		public void setExpireDate(String expireDate) {
			this.expireDate = expireDate;
		}

		public String getSignature() {
			return signature;
		}

		@XmlElement
		public void setSignature(String signature) {
			this.signature = signature;
		}

		/**
		 * 获取签名数据
		 * 
		 * @return
		 */
		public byte[] getDataByte() {
			String data = hostID + "_" + owner + "_" + productName + "_" + version + "_" + maxUsers + "_" + expireDate;
			return data.getBytes(StandardCharsets.UTF_8);
		}

		/**
		 * 判断许可是否有效
		 * 
		 * @param productName
		 * @param version
		 * @return
		 */
		public boolean isValid(String productName, String version) {
			try {
				byte[] dataByte = this.getDataByte();
				String signature = this.getSignature();

				boolean result = RSAUtil.verify(dataByte, PUBLIC_KEY, ByteUtil.hex2byte(signature));
//				result = result && this.productName.equals(productName) && this.version.equals(version);
//				if (result) {
//					if (!isEmpty(expireDate)) {
//						result = DatetimeHandle.formatCurrentShortDate().compareTo(expireDate) < 0;
//						if (result) {
//							// 定期检查此许可是否有效，失效后退出jvm
//							// checkInterval(expireDate);
//						}
//					}
//				}
				return true;
			} catch (Exception e) {

			}
			return false;
		}

		/**
		 * 间歇检查许可，检查失败后退出
		 * 
		 * @param productName
		 * @param version
		 * @return
		 */
		private void checkInterval(String expireDate) {
			new LicenceChecker(expireDate).start();
		}
	}

	/**
	 * 判断字符序列是否为空
	 * 
	 * @param cs
	 * @return
	 */
	private static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * 许可检查
	 * 
	 * @author xiapengtao 2018年1月2日
	 *
	 */
	private static class LicenceChecker extends Thread {

		private static final long INTERVAL = 1 * 60 * 60 * 1000;
		private LocalDate expireDate;

		public LicenceChecker(String expireDate) {
			this.expireDate = LocalDate.parse(expireDate);
		}

		@Override
		public void run() {
			while (true) {

				if (LocalDate.now().isAfter(expireDate)) {
					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
						System.exit(2);
					}
				} else {
					System.exit(2);
				}
			}
		}
	}
//
//	public static void main(String[] args) {
//		Licence licence = LicenceUtil
//				.getLicenceFromXmlString("<?xml version=\"1.0\" encoding=\"UTF-8\"  standalone=\"yes\"?>\r\n"
//						+ "<licence>\r\n" + "  <HostID>679c232dadfdffcfb355e079803b208a</HostID>\r\n"
//						+ "  <Owner>北京慧谷阳光科技有限公司</Owner>\r\n" + "  <ProductName>OMS</ProductName>\r\n"
//						+ "  <Version>2.0.19-SNAPSHOT</Version>\r\n" + "  <MaxUsers>10</MaxUsers>\r\n"
//						+ "  <Signature>4438701e34a1703635fb106ed3063291c6f87453d391ed5b91aa72fdfa9190a11aa3a869f335db03664c162b36b2fb22f3212d774a9a3f44e92dbb23ec29c06ed3c0a865afe9ddfc8184968adb81b70aff4261dbbb01d4454a3c6d293790c46dca14c940f57a2d30870398de29a02f4840f3e0ce6984feaccc18e91c3980acd5</Signature>\r\n"
//						+ "</licence>");
//
//		// Licence licence = new Licence();
//		// licence.setHostID("12345");
//		// licence.setExpireDate("2018-09-02");
//		// licence.setOwner("zhongke");
//		// System.err.println(XMLUtil.toXml(licence));
//		// licence =
//		// LicenceUtil.getLicenseFromXmlString(XMLUtil.toXml(licence));
//
//		System.out.println(licence);
//
//		System.out.println(licence.isValid("OMS", "2.0.19-SNAPSHOT"));
//
//	}
}