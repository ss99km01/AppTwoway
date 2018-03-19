package com.jefeko.apptwoway.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jefeko.apptwoway.ServiceCommon;

/**
 * db Util 함수
 */
public class DatabaseUtil {
	static final String TAG = "[DatabaseUtil]";
	
	private static final int DATABASE_VERSION = 1;	// DB Version
	
	private static DatabaseUtil mDatabaseUtil = null;
	private SQLiteDatabase mDb = null;		// db instance
	private Context mContext = null;
	private boolean mIsBeginTransaction = false; // 데이터 동기화를 위해 사용함
		
	public static DatabaseUtil getInstance(Context context) {
		if(mDatabaseUtil == null) {
			mDatabaseUtil = new DatabaseUtil(context);
		}
		
		return mDatabaseUtil;
	}
	
	public static DatabaseUtil getInstance() {
		if(mDatabaseUtil == null) { 
			Log.e(TAG, "error DatabaseUtil is not instance, call getInstance(Context context)");
		}
		
		return mDatabaseUtil;
	}
	
	public static void closeInstance() {
		if(mDatabaseUtil != null) {
			mDatabaseUtil.mDb.close();
			mDatabaseUtil = null;
		}
	}

	/**
	 * 생성자 
	 * @param context
	 */
	public DatabaseUtil(Context context) {
		mContext = context.getApplicationContext();
		OpenHelper dbHelper = new OpenHelper(mContext);
		
		try {
			mDb = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// db버전이 업그레이드 될 경우 asset에서 복사하고, db를 close하고 다시 open함 
		if(dbHelper.mIsUpgrade) {
			mDb.close();
			
			dbHelper = new OpenHelper(mContext);
			try {
				mDb = dbHelper.getWritableDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * openHelper
	 * @author ejlee
	 */
	private static class OpenHelper extends SQLiteOpenHelper {		
		private static final String DATABASE_NAME = ServiceCommon.DB_PATH;
//		private static final String DATABASE_NAME = "Apptwoway.db";
		public boolean mIsUpgrade = false;
		
		/**
		 * 생성자 sd카드에서 db open
		 * @param context
		 */
		public OpenHelper(Context context) {
//			super(new ContextWrapper(context) {
//		        @Override
//		        public SQLiteDatabase openOrCreateDatabase(String name,
//		                int mode, SQLiteDatabase.CursorFactory factory) {
//
//		            return SQLiteDatabase.openDatabase(DATABASE_NAME, null,
//		                SQLiteDatabase.CREATE_IF_NECESSARY);
//		        }
//		    }, DATABASE_NAME, null, DATABASE_VERSION);
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			Log.e("ss99km01", "ss99km01 OpenHelper onCreate");
			createTable(db);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			Log.e(TAG, "onUpgrade : " + oldVersion + " " + newVersion);
			
			mIsUpgrade = true;
		}
		
		/**
		 * 테이블 생성
		 * @param db
		 */
		public void createTable(SQLiteDatabase db) {
//			db.execSQL(DatabaseDefine.CREATE_USER);
//			db.execSQL(DatabaseDefine.CREATE_AUTO_LOGIN);
//			db.execSQL(DatabaseDefine.INSERT_AUTO_LOGIN);
		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {
			createTable(db);
			
			super.onOpen(db);
		}
	}
	
	/**
	 * transaction 시작 함수
	 */
	public void beginTransaction() {
		mDb.beginTransaction();
		mIsBeginTransaction = true;
	}
	
	/**
	 * transaction 종료 함수
	 * @param isCommit : commit할지 여부
	 */
	public void endTransaction(boolean isCommit) {
		if(isCommit) {
			mDb.setTransactionSuccessful();
		}
		
		mDb.endTransaction();
		mIsBeginTransaction = false;
	}
	
	/**
	 * execSQL을 실행할지 결정하는 함수
	 * @param query
	 */
	public void execSQL(String query) {
		mDb.execSQL(query);
	}

	/**
	 * auto_login 설정 변경
	 * @param user_id
	 * @param pass
	 * @param id_save
	 * @param auto_login
	 */
	public void  updateAutoLogin(String user_id, String pass, int id_save,int auto_login) {
		String query = "";
		
		try {
			query = String.format(DatabaseDefine.UPDATE_AUTO_LOGIN, singleQuote(user_id), singleQuote(pass), id_save, auto_login);
			execSQL(query);
		} catch (Exception e){
			e.printStackTrace();
			Log.e(TAG, "insertCustomer Error : " + e);
		}
	}

	/**
	 * 회원 update
	 * @param customerNo : customerNo
	 * @param id : 아이디
	 * @param password : 비밀번호
	 * @param customerName : 회원 이름
	 */
	public void  updateCustomer(int customerNo, String id, String password, String customerName) {
		String query = "";

		try {
//			query = String.format(DatabaseDefine.UPDATE_SBM_CUSTOMER, quote(id), quote(password), quote(customerName), customerNo);
//			query = String.format(DatabaseDefine.UPDATE_SBM_CUSTOMER, singleQuote(id), singleQuote(password), singleQuote(customerName), customerNo);
			execSQL(query);
		} catch (Exception e){
			e.printStackTrace();
			Log.e(TAG, "updateCustomer Error : " + e);
		}
	}

	/**
	 * string quote 함수
	 * @param str : quote할 string
	 * @return quote한 string
	 */
	public static String quote(String str) {
		return "\"" + str + "\"";
	}

	/**
	 * string single quote 함수
	 * @param str : quote할 string
	 * @return quote한 string
	 */
	public static String singleQuote(String str) {
		return "\'" + str + "\'";
	}
}
