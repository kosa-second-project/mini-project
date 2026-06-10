package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.or.bit.dto.Board;
import kr.or.bit.utils.ConnectionHelper;
import kr.or.bit.utils.DBType;

public class BoardDao {
    private static BoardDao instance;

    private BoardDao() {
    }

    public static synchronized BoardDao getInstance() {
        if (instance == null) {
            instance = new BoardDao();
        }
        return instance;
    }

    public int totalBoardCount() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int totalCount = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "select count(*) cnt from board where deleted = 0";
            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                totalCount = rs.getInt("cnt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return totalCount;
    }

    public List<Board> list(int cpage, int pagesize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Board> list = new ArrayList<>();

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "select * from "
                       + "(select rownum rn, idx, empno, ename, deptname, subject, content, writedate, readnum, "
                       + "        refer, depth, step, deleted "
                       + " from (select b.idx, b.empno, e.ename, d.deptname, b.subject, b.content, b.writedate, b.readnum, "
                       + "              b.refer, b.depth, b.step, b.deleted "
                       + "       from board b "
                       + "       left join emp e on b.empno = e.empno "
                       + "       left join dept d on e.deptno = d.deptno "
                       + "       where b.deleted = 0 "
                       + "       order by b.idx desc) "
                       + " where rownum <= ?"
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
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return list;
    }

    public Board getContent(int idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Board board = null;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "select b.idx, b.empno, e.ename, d.deptname, b.subject, b.content, b.writedate, b.readnum, "
                       + "       b.refer, b.depth, b.step, b.deleted "
                       + "from board b "
                       + "left join emp e on b.empno = e.empno "
                       + "left join dept d on e.deptno = d.deptno "
                       + "where b.idx = ? and b.deleted = 0";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                board = mapBoard(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return board;
    }

    public boolean getReadNum(int idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "update board set readnum = readnum + 1 where idx = ? and deleted = 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);

            int row = pstmt.executeUpdate();
            if (row > 0) {
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return result;
    }

    public int write(Board board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "insert into board(idx, empno, subject, content, writedate, readnum, "
                       + "                  refer, depth, step, deleted) "
                       + "values(board_seq.nextval, ?, ?, ?, sysdate, 0, ?, ?, ?, 0)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, board.getEmpno());
            pstmt.setString(2, board.getSubject());
            pstmt.setString(3, board.getContent());
            pstmt.setInt(4, board.getRefer());
            pstmt.setInt(5, board.getDepth());
            pstmt.setInt(6, board.getStep());

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
    }

    public int update(Board board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "update board "
                       + "set subject = ?, content = ? "
                       + "where idx = ? and empno = ? and deleted = 0";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, board.getSubject());
            pstmt.setString(2, board.getContent());
            pstmt.setInt(3, board.getIdx());
            pstmt.setInt(4, board.getEmpno());

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
    }

    public int delete(int idx, int empno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "update board set deleted = 1 where idx = ? and empno = ? and deleted = 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            pstmt.setInt(2, empno);

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
    }

    private Board mapBoard(ResultSet rs) throws SQLException {
        return Board.builder()
                .idx(rs.getInt("idx"))
                .empno(rs.getInt("empno"))
                .ename(rs.getString("ename"))
                .deptname(rs.getString("deptname"))
                .subject(rs.getString("subject"))
                .content(rs.getString("content"))
                .writedate(rs.getDate("writedate"))
                .readnum(rs.getInt("readnum"))
                .refer(rs.getInt("refer"))
                .depth(rs.getInt("depth"))
                .step(rs.getInt("step"))
                .deleted(rs.getInt("deleted") == 1)
                .build();
    }
}
