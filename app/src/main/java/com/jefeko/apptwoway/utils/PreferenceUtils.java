package com.jefeko.apptwoway.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

	public static String getPreferenceValueOfString(Context context, String key){
		SharedPreferences pref = context.getSharedPreferences(CommonUtil.PREFERENCE_APP_NAME, Context.MODE_PRIVATE);
		String value = pref.getString(key, "");

		return value;
	}

	public static int getPreferenceValueOfInt(Context context, String key){
		SharedPreferences pref = context.getSharedPreferences(CommonUtil.PREFERENCE_APP_NAME, Context.MODE_PRIVATE);
		int value = pref.getInt(key, -1);

		return value;
	}

	public static boolean getPreferenceValueOfBoolean(Context context, String key){
		SharedPreferences pref = context.getSharedPreferences(CommonUtil.PREFERENCE_APP_NAME, Context.MODE_PRIVATE);
		boolean value = pref.getBoolean(key, false);

		return value;
	}

	public static void setPreferenceValue(Context context, String key, String value){
		SharedPreferences pref = context.getSharedPreferences(CommonUtil.PREFERENCE_APP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void setPreferenceValue(Context context, String key, int value){
		SharedPreferences pref = context.getSharedPreferences(CommonUtil.PREFERENCE_APP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void setPreferenceValue(Context context, String key, boolean value){
		SharedPreferences pref = context.getSharedPreferences(CommonUtil.PREFERENCE_APP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}