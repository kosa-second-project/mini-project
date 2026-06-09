package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.or.bit.dto.Emp;
import kr.or.bit.utils.ConnectionHelper;
import kr.or.bit.utils.DBType;

public class EmpDao {
    private static EmpDao instance = null;

    private EmpDao() {
    }

    public static synchronized EmpDao getInstance() {
        if (instance == null) {
            instance = new EmpDao();
        }
        return instance;
    }

    // 사번으로 특정 사원 조회 (로그인 검증 및 상세조회용)
    public Emp getEmpByEmpno(int empno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Emp emp = null;
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "SELECT e.SEQ, e.EMPNO, e.ENAME, e.JOB, e.POSITION, e.MGR, " +
                         "TO_CHAR(e.HIREDATE, 'YYYY-MM-DD') AS HIREDATE, e.SAL, e.DEPTNO, d.DEPTNAME, e.PWD, e.ROLE " +
                         "FROM EMP e LEFT JOIN DEPT d ON e.DEPTNO = d.DEPTNO WHERE e.EMPNO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                emp = Emp.builder()
                        .seq(rs.getInt("SEQ"))
                        .empno(rs.getInt("EMPNO"))
                        .ename(rs.getString("ENAME"))
                        .job(rs.getString("JOB"))
                        .position(rs.getString("POSITION"))
                        .mgr(rs.getInt("MGR"))
                        .hiredate(rs.getString("HIREDATE"))
                        .sal(rs.getInt("SAL"))
                        .deptno(rs.getInt("DEPTNO"))
                        .deptname(rs.getString("DEPTNAME"))
                        .pwd(rs.getString("PWD"))
                        .role(rs.getString("ROLE"))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return emp;
    }

    // 전체 사원 건수 조회 (페이징용)
    public int getEmpCount() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "SELECT COUNT(*) FROM EMP";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return count;
    }

    // 부서별 사원 건수 조회 (페이징용)
    public int getEmpCountByDept(int deptno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "SELECT COUNT(*) FROM EMP WHERE DEPTNO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, deptno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return count;
    }

    // 전체 사원 목록 페이징 조회 (대표용)
    public List<Emp> getEmpList(int cpage, int pagesize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Emp> list = new ArrayList<>();
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "SELECT * FROM (" +
                         "  SELECT rownum rn, SEQ, EMPNO, ENAME, JOB, POSITION, MGR, HIREDATE, SAL, DEPTNO, DEPTNAME, ROLE FROM (" +
                         "    SELECT e.SEQ, e.EMPNO, e.ENAME, e.JOB, e.POSITION, e.MGR, " +
                         "    TO_CHAR(e.HIREDATE, 'YYYY-MM-DD') AS HIREDATE, e.SAL, e.DEPTNO, d.DEPTNAME, e.ROLE " +
                         "    FROM EMP e LEFT JOIN DEPT d ON e.DEPTNO = d.DEPTNO ORDER BY e.EMPNO ASC" +
                         "  ) WHERE rownum <= ?" +
                         ") WHERE rn >= ?";
            pstmt = conn.prepareStatement(sql);
            int start = cpage * pagesize - (pagesize - 1);
            int end = cpage * pagesize;
            pstmt.setInt(1, end);
            pstmt.setInt(2, start);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Emp emp = Emp.builder()
                        .seq(rs.getInt("SEQ"))
                        .empno(rs.getInt("EMPNO"))
                        .ename(rs.getString("ENAME"))
                        .job(rs.getString("JOB"))
                        .position(rs.getString("POSITION"))
                        .mgr(rs.getInt("MGR"))
                        .hiredate(rs.getString("HIREDATE"))
                        .sal(rs.getInt("SAL"))
                        .deptno(rs.getInt("DEPTNO"))
                        .deptname(rs.getString("DEPTNAME"))
                        .role(rs.getString("ROLE"))
                        .build();
                list.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return list;
    }

    // 특정 부서 사원 목록 페이징 조회 (일반 사원용)
    public List<Emp> getEmpListByDept(int deptno, int cpage, int pagesize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Emp> list = new ArrayList<>();
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "SELECT * FROM (" +
                         "  SELECT rownum rn, SEQ, EMPNO, ENAME, JOB, POSITION, MGR, HIREDATE, SAL, DEPTNO, DEPTNAME, ROLE FROM (" +
                         "    SELECT e.SEQ, e.EMPNO, e.ENAME, e.JOB, e.POSITION, e.MGR, " +
                         "    TO_CHAR(e.HIREDATE, 'YYYY-MM-DD') AS HIREDATE, e.SAL, e.DEPTNO, d.DEPTNAME, e.ROLE " +
                         "    FROM EMP e LEFT JOIN DEPT d ON e.DEPTNO = d.DEPTNO WHERE e.DEPTNO = ? ORDER BY e.EMPNO ASC" +
                         "  ) WHERE rownum <= ?" +
                         ") WHERE rn >= ?";
            pstmt = conn.prepareStatement(sql);
            int start = cpage * pagesize - (pagesize - 1);
            int end = cpage * pagesize;
            pstmt.setInt(1, deptno);
            pstmt.setInt(2, end);
            pstmt.setInt(3, start);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Emp emp = Emp.builder()
                        .seq(rs.getInt("SEQ"))
                        .empno(rs.getInt("EMPNO"))
                        .ename(rs.getString("ENAME"))
                        .job(rs.getString("JOB"))
                        .position(rs.getString("POSITION"))
                        .mgr(rs.getInt("MGR"))
                        .hiredate(rs.getString("HIREDATE"))
                        .sal(rs.getInt("SAL"))
                        .deptno(rs.getInt("DEPTNO"))
                        .deptname(rs.getString("DEPTNAME"))
                        .role(rs.getString("ROLE"))
                        .build();
                list.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return list;
    }

    // 신규 사원 등록
    public int insertEmp(Emp emp) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "INSERT INTO EMP (SEQ, EMPNO, ENAME, JOB, POSITION, MGR, HIREDATE, SAL, DEPTNO, PWD, ROLE) " +
                         "VALUES (SEQ_EMP_SEQ.NEXTVAL, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, emp.getEmpno());
            pstmt.setString(2, emp.getEname());
            pstmt.setString(3, emp.getJob());
            pstmt.setString(4, emp.getPosition());
            if (emp.getMgr() > 0) {
                pstmt.setInt(5, emp.getMgr());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            pstmt.setString(6, emp.getHiredate());
            pstmt.setInt(7, emp.getSal());
            pstmt.setInt(8, emp.getDeptno());
            pstmt.setString(9, emp.getPwd());
            pstmt.setString(10, emp.getRole());

            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return result;
    }

    // 사원 정보 수정
    public int updateEmp(Emp emp) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            boolean updatePwd = emp.getPwd() != null && !emp.getPwd().trim().isEmpty();
            String sql;
            if (updatePwd) {
                sql = "UPDATE EMP SET ENAME = ?, JOB = ?, POSITION = ?, MGR = ?, " +
                      "HIREDATE = TO_DATE(?, 'YYYY-MM-DD'), SAL = ?, DEPTNO = ?, PWD = ?, ROLE = ? WHERE EMPNO = ?";
            } else {
                sql = "UPDATE EMP SET ENAME = ?, JOB = ?, POSITION = ?, MGR = ?, " +
                      "HIREDATE = TO_DATE(?, 'YYYY-MM-DD'), SAL = ?, DEPTNO = ?, ROLE = ? WHERE EMPNO = ?";
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emp.getEname());
            pstmt.setString(2, emp.getJob());
            pstmt.setString(3, emp.getPosition());
            if (emp.getMgr() > 0) {
                pstmt.setInt(4, emp.getMgr());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            pstmt.setString(5, emp.getHiredate());
            pstmt.setInt(6, emp.getSal());
            pstmt.setInt(7, emp.getDeptno());
            if (updatePwd) {
                pstmt.setString(8, emp.getPwd());
                pstmt.setString(9, emp.getRole());
                pstmt.setInt(10, emp.getEmpno());
            } else {
                pstmt.setString(8, emp.getRole());
                pstmt.setInt(9, emp.getEmpno());
            }

            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return result;
    }

    // 사원 삭제
    public int deleteEmp(int empno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            String sql = "DELETE FROM EMP WHERE EMPNO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empno);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }
        return result;
    }
}
