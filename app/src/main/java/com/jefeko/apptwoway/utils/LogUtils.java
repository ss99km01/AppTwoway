package com.jefeko.apptwoway.utils;

import android.util.Log;


public class LogUtils {



	public static void v(String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.v(CommonUtil.LOG_TAG, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.v(CommonUtil.LOG_TAG, "[" + tag + "] " + msg);
		}
	}

	public static void nv(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.v(CommonUtil.LOG_TAG, "[" + tag + "] " + msg);
		}
	}

	public static void vv(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.v(tag, msg);
		}
	}

	public static void d(String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.d(CommonUtil.LOG_TAG, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.d(CommonUtil.LOG_TAG, "[" + tag + "] " + msg);
		}
	}
	
	public static void dd(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.d(tag, msg);
		}
	}
	
	public static void i(String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.i(CommonUtil.LOG_TAG, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.i(CommonUtil.LOG_TAG, "[" + tag + "] " + msg);
		}
	}
	
	public static void ii(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.i(tag, msg);
		}
	}
	
	public static void w(String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.w(CommonUtil.LOG_TAG, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.w(CommonUtil.LOG_TAG, "[" + tag + "] " + msg);
		}
	}
	
	public static void ww(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.w(tag, msg);
		}
	}
	
	public static void e(String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.e(CommonUtil.LOG_TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.e(tag, msg);
		}
	}
	
	public static void e(String msg, Throwable tr) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.e(CommonUtil.LOG_TAG, msg, tr);
		}
	}
	
	public static void e(String tag, String msg, Throwable tr) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.e(CommonUtil.LOG_TAG, "[" + tag + "] " + msg, tr);
		}
	}
	
	public static void ee(String tag, String msg, Throwable tr) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.e(tag, msg, tr);
		}
	}
	
	public static void log(int priority, String tag, String msg) {
		if (CommonUtil.IS_DEBUG_LOG) {
			Log.println(priority, CommonUtil.LOG_TAG, "[" + tag + "]" + msg);
		}
	}
}
