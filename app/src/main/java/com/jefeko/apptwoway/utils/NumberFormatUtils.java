package com.jefeko.apptwoway.utils;


import android.util.Log;

public class NumberFormatUtils {

	/**
	 * flot 타입 숫자를 3자리마다 콤마(,)를 추가한 문자열로 반환한다.
	 *
	 * <pre>
	 * numberToCommaString(10000.0) = 10,000.0
	 * </pre>
	 *
	 * @param f float 숫자
	 * @return 3자리마다 콤마(,)를 추가한 문자열
	 */
	public static String numberToCommaString(float f, int point){
		return String.format("%,."+point+"f", f);
	}


	/**
	 * int 타입 숫자를 3자리마다 콤마(,)를 추가한 문자열로 반환한다.
	 *
	 * <pre>
	 * numberToCommaString(10000) = 10,000
	 * </pre>
	 *
	 * @param i int 숫자
	 * @return 3자리마다 콤마(,)를 추가한 문자열
	 */
    public static String numberToCommaString(int i){
		return String.format("%,d", i);
	}


	/**
	 * long 타입 숫자를 3자리마다 콤마(,)를 추가한 문자열로 반환한다.
	 *
	 * <pre>
	 * numberToCommaString(10000) = 10,000
	 * </pre>
	 *
	 * @param l long 숫자
	 * @return 3자리마다 콤마(,)를 추가한 문자열
	 */
    public static String numberToCommaString(long l){
		return String.format("%,d", l);
	}


	/**
	 * double 타입 숫자를 3자리마다 콤마(,)를 추가한 문자열로 반환한다.
	 *
	 * <pre>
	 * numberToCommaString(10000.0) = 10,000.0
	 * </pre>
	 *
	 * @param d double 숫자
	 * @return 3자리마다 콤마(,)를 추가한 문자열
	 */
    public static String numberToCommaString(double d, int point){
		return String.format("%,."+point+"f", d);
	}


	/**
	 * String으로 표현된 숫자를 3자리마다 콤마(,)를 추가한 문자열로 반환한다.
	 *
	 * <pre>
	 * numberToCommaString("10000") = 10,000
	 * </pre>
	 *
	 * @param s String으로 표현된 숫자
	 * @return 3자리마다 콤마(,)를 추가한 문자열
	 */
    public static String numberToCommaString(String s){
		if ( s.indexOf(".") > 0 ) {
			String intPart=s.substring(0, s.indexOf("."));
			String realPart=s.substring(s.indexOf("."), s.length());
			Log.d("numberToCommaString", "intPart:"+intPart+", realPart:"+realPart);
			return numberToCommaString(Long.parseLong(intPart))+realPart;
		}else{
			return numberToCommaString(Long.parseLong(s));
		}
	}


	/**
	 * 문자열에 추가된 콤마(,)를 삭제하는 함수
	 *
	 * <pre>
	 * numberToCommaString("10,000") = 10000
	 * </pre>
	 *
	 * @param s String으로 표현된 숫자
	 * @return 콤마(,)가 삭제된 문자열
	 */
    public static String removeCommaInString(String s){
		return s.replace(",", "");
	}


	/**
	 * 입력된 문자열이 숫자인지 여부 확인하는 함수
	 *
	 * <pre>
	 * isNumeric("111") --> true
	 * isNumeric("abc") --> false
	 * </pre>
	 *
	 * @param s String으로 표현된 숫자
	 * @return 문자열이 숫자이면 true, 숫자가 아니면 false
	 */
    public static boolean isNumeric(String s){
		boolean isnumeric = false;

		if(s != null && !s.equals("")){
			isnumeric = true;
			char chars[] = s.toCharArray();

			for(int i=0; i<chars.length; i++){
				if(!Character.isDigit(chars[i]) && chars[i] != '.' )
					isnumeric = false;

				if(!isnumeric)
					break;
			}
		}
		return isnumeric;
	}
}
