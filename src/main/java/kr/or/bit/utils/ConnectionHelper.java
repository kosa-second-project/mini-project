package kr.or.bit.utils;
//DB연결 작업 편하게

//함수 > 편하게 > 많이 사용 > new (x) > static method (오버로딩)
//확장성 : Oracle, Mysql .... 사용 (코드성) > enum

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//하지만 실무(개발환경) : 성능
//DB연결 ..생성.. (DB매번하지 말고 미리 만들어 넣자)
//실 프로젝트에서는 Connection Pool (히카리 풀) : Hikari Cp

public class ConnectionHelper {

	public static Connection getConnection(DBType dbType) {

		Connection conn = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			switch (dbType) {
				case ORACLE:
					conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.43:1521/XEPDB1", "KOSA", "0000");
					break;
				case MARIADB:
					conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/sampledb", "KOSA", "1004");
					break;
			}
		} catch (Exception e) {
			System.out.println("connection Factory : " + e.getMessage());
		}

		return conn;
	}

	public static Connection getConnection(DBType dbType, String id, String pwd) {

		Connection conn = null;

		try {
			switch (dbType) {
				case ORACLE:
					conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", id, pwd);
					break;
				case MARIADB:
					conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/sampledb", id, pwd);
					break;
			}
		} catch (Exception e) {
			System.out.println("connection Factory : " + e.getMessage());
		}

		return conn;
	}

	// 자원해제
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void close(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
