package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import kr.or.bit.dto.Board;

public class BoardDao {
    private static final String TABLE_NAME = "BOARD";
    private static final String SEQUENCE_NAME = "BOARD_SEQ";
    private DataSource ds = null;
    private static BoardDao instance = null;
    private String lastErrorMessage = null;

    private BoardDao() {
        try {
            Context context = new InitialContext();
            ds = (DataSource) context.lookup("java:comp/env/jdbc/oracle");
        } catch (NamingException e) {
            System.out.println("[BoardDao] JNDI jdbc/oracle not found. fallback to direct JDBC connection.");
        }
    }

    public static synchronized BoardDao getInstance() {
        if (instance == null) {
            try {
                instance = new BoardDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public int insertBoard(Board board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int idx = 0;
        lastErrorMessage = null;

        try {
            conn = getConnection();
            idx = getNextIdx(conn);

            String sql = "insert into " + TABLE_NAME + " "
                    + "(idx, empno, subject, content, writedate, readnum, refer, depth, step, deleted, lat, lng) "
                    + "values (?, ?, ?, ?, sysdate, 0, ?, 0, 0, 0, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            pstmt.setInt(2, board.getEmpno());
            pstmt.setString(3, board.getSubject());
            pstmt.setString(4, board.getContent());
            pstmt.setInt(5, idx);
            setNullableFloat(pstmt, 6, board.getLat());
            setNullableFloat(pstmt, 7, board.getLng());

            int row = pstmt.executeUpdate();
            System.out.println("[BoardDao.insertBoard] row=" + row + ", idx=" + idx);
            return row > 0 ? idx : 0;
        } catch (Exception e) {
            lastErrorMessage = e.getMessage();
            System.out.println("[BoardDao.insertBoard] insert failed. check BOARD columns: idx, empno, subject, content, writedate, readnum, refer, depth, step, deleted, lat, lng");
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }
        return 0;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public Board getBoard(int idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select idx, empno, subject, content, writedate, readnum, refer, depth, step, deleted, lat, lng "
                    + "from " + TABLE_NAME + " where idx = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapBoard(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(pstmt);
            close(conn);
        }
        return null;
    }

    public int updateBoard(Board board) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "update " + TABLE_NAME + " set subject = ?, content = ?, lat = ?, lng = ? where idx = ? and deleted = 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, board.getSubject());
            pstmt.setString(2, board.getContent());
            setNullableFloat(pstmt, 3, board.getLat());
            setNullableFloat(pstmt, 4, board.getLng());
            pstmt.setInt(5, board.getIdx());
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }
        return 0;
    }

    public int softDeleteBoard(int idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "update " + TABLE_NAME + " set deleted = 1 where idx = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }
        return 0;
    }

    public int increaseReadNum(int idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "update " + TABLE_NAME + " set readnum = readnum + 1 where idx = ? and deleted = 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }
        return 0;
    }

    public List<Board> list(int cpage, int pagesize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Board> list = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "select * from ("
                    + "select rownum rn, b.* from ("
                    + "select idx, empno, subject, content, writedate, readnum, refer, depth, step, deleted, lat, lng "
                    + "from " + TABLE_NAME + " order by refer desc, step asc"
                    + ") b where rownum <= ?"
                    + ") where rn >= ?";
            pstmt = conn.prepareStatement(sql);
            int start = cpage * pagesize - (pagesize - 1);
            int end = cpage * pagesize;
            pstmt.setInt(1, end);
            pstmt.setInt(2, start);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapBoard(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(pstmt);
            close(conn);
        }
        return list;
    }

    public int totalBoardCount() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "select count(*) cnt from " + TABLE_NAME;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(pstmt);
            close(conn);
        }
        return 0;
    }

    private int getNextIdx(Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select " + SEQUENCE_NAME + ".nextval from dual");
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException(SEQUENCE_NAME + ".nextval not found");
        } finally {
            close(rs);
            close(stmt);
        }
    }

    private Connection getConnection() throws SQLException {
        if (ds != null) {
            return ds.getConnection();
        }
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC driver not found.", e);
        }
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@//192.168.2.43:1521/XEPDB1",
                "KOSA",
                "0000");
    }

    private Board mapBoard(ResultSet rs) throws SQLException {
        Float lat = rs.getObject("lat") == null ? null : rs.getFloat("lat");
        Float lng = rs.getObject("lng") == null ? null : rs.getFloat("lng");

        return Board.builder()
                .idx(rs.getInt("idx"))
                .empno(rs.getInt("empno"))
                .subject(rs.getString("subject"))
                .content(rs.getString("content"))
                .writedate(rs.getDate("writedate"))
                .readnum(rs.getInt("readnum"))
                .refer(rs.getInt("refer"))
                .depth(rs.getInt("depth"))
                .step(rs.getInt("step"))
                .deleted(rs.getInt("deleted") == 1)
                .lat(lat)
                .lng(lng)
                .build();
    }

    private void setNullableFloat(PreparedStatement pstmt, int index, Float value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, java.sql.Types.FLOAT);
        } else {
            pstmt.setFloat(index, value);
        }
    }

    private void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
