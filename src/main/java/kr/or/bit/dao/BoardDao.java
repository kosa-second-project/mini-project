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
        try {
            return visibleBoardList().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Board> list(int cpage, int pagesize) {
        try {
            List<Board> visibleList = visibleBoardList();
            int start = Math.max(0, cpage * pagesize - pagesize);
            int end = Math.min(visibleList.size(), start + pagesize);
            if (start >= visibleList.size()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(visibleList.subList(start, end));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Board> visibleBoardList() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Board> list = new ArrayList<>();
        List<Board> visibleList = new ArrayList<>();

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "select b.idx, b.empno, e.ename, d.deptname, b.subject, b.content, b.writedate, b.readnum, "
                       + "       b.refer, b.depth, b.step, b.deleted "
                       + "from board b "
                       + "left join emp e on b.empno = e.empno "
                       + "left join dept d on e.deptno = d.deptno "
                       + "order by case when b.refer = 0 then b.idx else b.refer end desc, b.step asc, b.idx desc";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapBoard(rs));
            }

            for (int i = 0; i < list.size(); i++) {
                Board board = list.get(i);
                if (!board.isDeleted() || hasVisibleDescendant(list, i)) {
                    visibleList.add(board);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return visibleList;
    }

    private boolean hasVisibleDescendant(List<Board> list, int index) {
        Board board = list.get(index);
        int threadRefer = threadRefer(board);

        for (int i = index + 1; i < list.size(); i++) {
            Board candidate = list.get(i);
            if (threadRefer(candidate) != threadRefer || candidate.getDepth() <= board.getDepth()) {
                break;
            }
            if (!candidate.isDeleted()) {
                return true;
            }
        }
        return false;
    }

    private int threadRefer(Board board) {
        return board.getRefer() == 0 ? board.getIdx() : board.getRefer();
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
            int idx = nextBoardIdx(conn);

            String sql = "insert into board(idx, empno, subject, content, writedate, readnum, "
                       + "                  refer, depth, step, deleted) "
                       + "values(?, ?, ?, ?, sysdate, 0, ?, 0, 0, 0)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            pstmt.setInt(2, board.getEmpno());
            pstmt.setString(3, board.getSubject());
            pstmt.setString(4, board.getContent());
            pstmt.setInt(5, idx);

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
    }

    public int writeReply(int parentIdx, Board board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            String parentSql = "select idx, refer, depth, step from board where idx = ? and deleted = 0";
            pstmt = conn.prepareStatement(parentSql);
            pstmt.setInt(1, parentIdx);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return 0;
            }

            int parentRefer = rs.getInt("refer");
            int parentDepth = rs.getInt("depth");
            int parentStep = rs.getInt("step");
            int refer = parentRefer == 0 ? parentIdx : parentRefer;
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);

            String shiftSql = "update board set step = step + 1 where (case when refer = 0 then idx else refer end) = ? and step > ?";
            pstmt = conn.prepareStatement(shiftSql);
            pstmt.setInt(1, refer);
            pstmt.setInt(2, parentStep);
            pstmt.executeUpdate();
            ConnectionHelper.close(pstmt);

            int idx = nextBoardIdx(conn);
            String insertSql = "insert into board(idx, empno, subject, content, writedate, readnum, "
                             + "                  refer, depth, step, deleted) "
                             + "values(?, ?, ?, ?, sysdate, 0, ?, ?, ?, 0)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, idx);
            pstmt.setInt(2, board.getEmpno());
            pstmt.setString(3, board.getSubject());
            pstmt.setString(4, board.getContent());
            pstmt.setInt(5, refer);
            pstmt.setInt(6, parentDepth + 1);
            pstmt.setInt(7, parentStep + 1);

            row = pstmt.executeUpdate();
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
            ConnectionHelper.close(conn);
        }

        return row;
    }

    private int nextBoardIdx(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("select board_seq.nextval from dual");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("board_seq.nextval failed");
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
        }
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
