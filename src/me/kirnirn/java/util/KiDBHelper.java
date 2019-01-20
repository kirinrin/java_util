package me.kirnirn.java.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KiDBHelper {
	public int bigIntToInt(Object obj) {
		
		return obj == null ? 0:((BigInteger) obj).intValue();
	}
	public long bigDecToLong(Object obj) {
		return obj == null ? 0:((BigDecimal)obj).longValue();
	}
	public int bigDecToInt(Object obj) {
		return obj == null ? 0:((BigDecimal)obj).intValue();
	}
	public float bigDecToFloat(Object obj) {
		return obj == null ? 0: ((BigDecimal)obj).floatValue();
	}
}
