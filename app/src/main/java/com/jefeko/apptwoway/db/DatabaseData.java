package com.jefeko.apptwoway.db;

/**
 * db에서 조회시 리턴해주는 Data class
 */
public class DatabaseData {
	public static class AutoLogin{
		private String user_id = "";			//아이디
		private String user_pass = "";		//패스워드
		private int id_save = 0;				//아이디저장
		private int auto_login = 0;			//자동로그인
	}
}
