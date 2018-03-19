package com.jefeko.apptwoway;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.jefeko.apptwoway.utils.CommonUtil;

public class ServiceCommon {

	/**
	 * Broadcast Action
	 */

	/** Extra Name
	 *
	 */
//	public static final String EXTRA_PUSH_MESSAGE = "message";
	
	/** 
	 * Package Name
	 */

	public static String COMPANY_ID = "";
	public static String EMPLOYEE_ID = "";
	public static String PUSH_TOKEN = "";

	public static final String STORAGE_PATH = CommonUtil.getInternalStoragePath();	// 기본 내장 스토리지 path
	public static final String CONTENTS_PATH = STORAGE_PATH + "/Apptwoway/";	// db 및 음성, 콘텐츠 파일)

	public static final String DB_FILE = "Apptwoway.db";								// DB파일 이름
	public static final String DB_PATH = CONTENTS_PATH + DB_FILE;						// DB파일 path

	public static final String COMPANY_TYPE_001 = "001";
	public static final String COMPANY_TYPE_002 = "002";
	public static final String COMPANY_TYPE_003 = "003";

	/**
	 * app version Name에 따라 개발, 상용 서버 URL을 설정하는 함수
	 * Version Name이 홀수면 개발, 짝수면 상용
	 * @param context
	 */
	public static void setServerUrl(Context context) {
		PackageInfo i;
		String version = "";
		try {
			i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = i.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if(Integer.valueOf(version.substring(version.length() - 1, version.length())) % 2 == 0) { // 짝수면 상용 서버
		} else { // 개발 서버
		}
	}
}
