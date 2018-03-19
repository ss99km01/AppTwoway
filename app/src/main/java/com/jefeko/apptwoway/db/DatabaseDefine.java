package com.jefeko.apptwoway.db;


/**
 * db Define 클래스
 * 각종 create 및 긴 Query를 가지고 있음.
  */
public class DatabaseDefine {
	// column과 value를 저장하기 위한 클래스
	public static class ColValue {
		String mColumn = "";	// 컬럼 이름
		String mValue = "";		// 컬럼 값
	};
	
	// 사용자 테이블
	public static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS user "
			+ " (seq INTEGER PRIMARY KEY, "			// seq
			+ " user_id TEXT, "						// 사용자 아이디
			+ " employee_id TEXT, "					// 직원 아이디
			+ " employee_name TEXT, "				// 이름
			+ " company_id TEXT, "					// 회사 아이디
			+ " company_name  TEXT, "				// 회사명
			+ " busi_no  TEXT, "						// 사업자번호
			+ " company_type_code  TEXT, "			// 회사 유형 코드
			+ " company_type_name  TEXT, "			// 회사 유형명
			+ " busi_type  TEXT, "					// 업태
			+ " busi_item  TEXT, "					// 종목
			+ " tel_no  TEXT, "						// 전화번호
			+ " fax_no  TEXT, "						// 팩스번호
			+ " zip  TEXT, "							// 우편번호
			+ " addr1  TEXT, "						// 주소
			+ " addr2  TEXT, "						// 상세주소
			+ " van_code  TEXT, "					// VAN사 코드
			+ " van_name  TEXT, "					// VAN사 명
			+ " pg_code  TEXT, "						// PG사 코드
			+ " pg_name  TEXT, "						// PG사 명
			+ " boss_employee_name  TEXT, "			// 대표자 이름
			+ " cell_no  TEXT, "						// 대표자 휴대폰번호
			+ " email  TEXT, "						// 대표자 이메일주소
			+ " bank_code  TEXT, "					// 대표자 은행코드
			+ " bank_name  TEXT, "					// 대표자 은행명
			+ " accounter  TEXT, "					// 대표자 예금주
			+ " account_no TEXT) ";					// 대표자 계좌번호
	
	// 회원 설정 테이블
	public static final String CREATE_AUTO_LOGIN = "CREATE TABLE IF NOT EXISTS auto_login "
			+ " (user_id TEXT, "				// 아이디
			+ " user_pass TEXT, "			// 패스워드
			+ " id_save INT "					// 아이디 저장
			+ " auto_login INT) ";			// 자동로그인

	/**
	 * 조건 - 고객 번호 조회용
	 */
	public static final String COMMON_STR_CUSTOMER_NO = "customer_no = %d";
	
	/*--------------------------------------------------*/
	/*                       sbm_customer               */
	/*--------------------------------------------------*/
	public static final String INSERT_AUTO_LOGIN = "INSERT OR REPLACE INTO auto_login(user_id, user_pass, id_save, auto_login) values('', '', 0, 0)";
	public static final String SELECT_AUTO_lOGIN = "SELECT user_id,user_pass,id_save,auto_login FROM auto_login";
	public static final String UPDATE_AUTO_LOGIN = "UPDATE auto_login SET user_id = %s, user_pass= %s , id_save = %s ,auto_login = %s ";

	public static final String SELECT_SBM_CUSTOMER_GET_CNT = "SELECT count(*) FROM sbm_customer";

	/*--------------------------------------------------*/
	/*                  sbm_customer_config             */
	/*--------------------------------------------------*/
	public static final String DELETE_SBM_CUSTOMER_CONFIG_WHERE_CUSTOMER_NO = "DELETE FROM sbm_customer_config WHERE "+COMMON_STR_CUSTOMER_NO;
	public static final String GET_SBM_CUSTOMER_CONFIG_GET_CONF = "SELECT conf_name, conf_value FROM sbm_customer_config WHERE "+COMMON_STR_CUSTOMER_NO;
	
}