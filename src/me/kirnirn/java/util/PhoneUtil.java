package me.kirnirn.java.util;

import java.util.Locale;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

public class PhoneUtil {

	private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

	private static PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

	private static PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();

	/**
	 * 根据国家代码和手机号码判断归属地
	 * 
	 * @param phoneNumber
	 * @param countryCode
	 * @return
	 */
	public static String getGeoArea(Long phoneNumber, Integer countryCode) {
		PhoneNumber pn = new PhoneNumber();
		pn.setCountryCode(countryCode);
		pn.setNationalNumber(phoneNumber);

		return geocoder.getDescriptionForNumber(pn, Locale.CHINESE);
	}

	/**
	 * 根据国家代码和手机号码判断归属地
	 * 
	 * @param phoneNumberStr
	 * @param countryCode
	 * @return
	 */
	public static String getGeoArea(String phoneNumberStr, Integer countryCode) {
		try {
			Long phoneNumber = Long.parseLong(phoneNumberStr);
			return getGeoArea(phoneNumber, countryCode);
		} catch (Exception e) {
			return null;
		}
	}

}
