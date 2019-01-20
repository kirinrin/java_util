package me.kirnirn.java.util;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class KiDateTimeHelper {

	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Convert Instant to LocalDateTime by system default zone
	 * 
	 * @param instant
	 * @return
	 */
	public static LocalDateTime convert2LocaDateTime(Instant instant) {
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, zoneId);
		return ldt;
	}


	/**
	 * Convert Instant to LocalDateTime by time zone
	 * 
	 * @param instant
	 * @return
	 */
	public static LocalDateTime convert2LocaDateTime(Instant instant, ZoneId zone) {
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, zone);
		return ldt;
	}

	/**
	 * 计算时间差（秒），如果某个参数=0， 返回 0
	 * 针对精度问题，舍去秒后的精度进行计算
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long durationBySecond(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null) {
			return 0L;
		} else {
			return Duration.between(start.withNano(0), end.withNano(0)).getSeconds();
		}
	}

	/**
	 * 计算时间差（秒），如果某个参数=0， 返回 0, 三个比较，使用后两个中的前面的一个。
	 * * 针对精度问题，舍去秒后的精度进行计算 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long durationBySecond(LocalDateTime start, LocalDateTime mid, LocalDateTime end) {
		
		if (start == null || (mid == null && end == null)) {
			return 0L;
		} else if (mid == null) {
			return Duration.between(start.withNano(0), end.withNano(0)).getSeconds();
		} else if (end == null) {
			return Duration.between(start.withNano(0), mid.withNano(0)).getSeconds();
		} else {
			return 0L;
		}
	}
}
