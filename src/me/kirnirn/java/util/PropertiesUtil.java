package me.kirnirn.java.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesUtil {

	/**
	 * 修改或添加键值对 如果key存在，修改, 反之，添加。
	 * 
	 * @param filePath
	 *            文件路径，即文件所在包的路径，例如： /app.properties
	 * @param key
	 *            键
	 * @param value
	 *            键对应的值
	 */
	public static void writeData(String filePath, String key, String value) {
		try {
			String sysFilePath = System.getProperty("user.dir");
			// 获取绝对路径
			filePath = sysFilePath + File.separator + filePath;
			log.info("filePath:" + filePath);
			// 截掉路径的”file:/“前缀
			// filePath = filePath.substring(6);
			Properties prop = new Properties();
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
			InputStream fis = new FileInputStream(file);
			prop.load(fis);
			// 一定要在修改值之前关闭fis
			fis.close();
			OutputStream fos = new FileOutputStream(filePath);
			prop.setProperty(key, value);
			// 保存，并加入注释
			prop.store(fos, "Update '" + key + "' value" + value);
			fos.close();
		} catch (IOException e) {
			log.error("Visit " + filePath + " for updating " + key + " value:" + value + " value error", e);
		}
	}

	/**
	 * 对set集合的数字转换成逗号与和连续的信息
	 * 
	 * @param sourceStation
	 *            数据源集合.7101,7104,7105,7106的集合
	 * @return String[2] String[0] 7101 String[1] 7104-7106
	 */
	public static String[] allStationToString(Set<String> sourceStation) {
		Set<String> sortSet = new TreeSet<String>(Comparator.naturalOrder());
		sortSet.addAll(sourceStation);
		String station = "", stations = "";
		int lastSta = 0, current = 0, statIndx = 0, i = 0;

		for (String sta : sortSet) {
			i++;
			log.info("第{} 分机号：{} lastSta-{} -statIndx：{}", i, sta, lastSta, statIndx);
			if (1 == sortSet.size()) {
				station = sta;
				break;
			}
			current = Integer.parseInt(sta);
			if (current - lastSta == 1) {
				// 连续的
				if (statIndx == 0 && lastSta > 0) {
					// 前面跳了
					statIndx = lastSta;
				}
				if (i == sortSet.size() && statIndx > 0) {
					// 最后一轮
					stations = stations + "," + statIndx + "-" + current;
				}
			} else if (current - lastSta > 1) {
				// 没有起始
				if (statIndx == 0 && lastSta > 0) {
					// 前面是单个的
					station = station + "," + lastSta;
				} else {
					if (lastSta > 0)
						stations = stations + "," + statIndx + "-" + lastSta;
				}

				if (i == sortSet.size() && lastSta > 0) {
					// 最后一轮
					station = station + "," + current;
				}

				statIndx = 0;
			}
			lastSta = current;
		}
		if (station.startsWith(","))
			station = station.substring(1);

		if (stations.startsWith(","))
			stations = stations.substring(1);
		if (stations.startsWith("0,"))
			stations = stations.substring(2);
		log.info("目前所有分机情况如下：{}", sortSet.toString());
		log.info("单个分机信息:{}", station);
		log.info("批量分机如下:{}", stations);

		return new String[] { station, stations };
	}
}
