package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import kr.or.bit.dto.Dept;

public class DeptDao {
    private DataSource ds = null;
    private static DeptDao instance = null;

    private DeptDao() throws NamingException {
        Context context = new InitialContext();
        ds = (DataSource) context.lookup("java:comp/env/jdbc/oracle");
    }

    public static synchronized DeptDao getInstance() {
        if (instance == null) {
            try {
                instance = new DeptDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public List<Dept> getDeptList() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Dept> list = new ArrayList<>();
        try {
            conn = ds.getConnection();
            String sql = "SELECT DEPTNO, DEPTNAME, LOC FROM DEPT ORDER BY DEPTNO ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Dept dept = Dept.builder()
                        .deptno(rs.getInt("DEPTNO"))
                        .deptname(rs.getString("DEPTNAME"))
                        .loc(rs.getString("LOC"))
                        .build();
                list.add(dept);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return list;
    }
}
