package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.or.bit.dto.Reply;
import kr.or.bit.utils.ConnectionHelper;
import kr.or.bit.utils.DBType;

public class ReplyDao {
    private static ReplyDao instance;

    private ReplyDao() {
    }

    public static synchronized ReplyDao getInstance() {
        if (instance == null) {
            instance = new ReplyDao();
        }
        return instance;
    }

    public List<Reply> list(int idx_fk) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Reply> list = new ArrayList<>();
        List<Reply> visibleList = new ArrayList<>();

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "select r.no, r.empno, e.ename, d.deptname, r.content, r.writedate, "
                       + "       r.idx_fk, r.refer, r.depth, r.step, r.deleted "
                       + "from reply r "
                       + "left join emp e on r.empno = e.empno "
                       + "left join dept d on e.deptno = d.deptno "
                       + "where r.idx_fk = ? "
                       + "order by case when r.refer = 0 then r.no else r.refer end desc, r.step asc, r.no desc";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx_fk);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Reply reply = Reply.builder()
                        .no(rs.getInt("no"))
                        .empno(rs.getInt("empno"))
                        .ename(rs.getString("ename"))
                        .deptname(rs.getString("deptname"))
                        .content(rs.getString("content"))
                        .writedate(rs.getDate("writedate"))
                        .idx_fk(rs.getInt("idx_fk"))
                        .refer(rs.getInt("refer"))
                        .depth(rs.getInt("depth"))
                        .step(rs.getInt("step"))
                        .deleted(rs.getInt("deleted") == 1)
                        .build();

                list.add(reply);
            }

            for (int i = 0; i < list.size(); i++) {
                Reply reply = list.get(i);
                if (!reply.isDeleted() || hasVisibleDescendant(list, i)) {
                    visibleList.add(reply);
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

    private boolean hasVisibleDescendant(List<Reply> list, int index) {
        Reply reply = list.get(index);
        int threadRefer = threadRefer(reply);

        for (int i = index + 1; i < list.size(); i++) {
            Reply candidate = list.get(i);
            if (threadRefer(candidate) != threadRefer || candidate.getDepth() <= reply.getDepth()) {
                break;
            }
            if (!candidate.isDeleted()) {
                return true;
            }
        }
        return false;
    }

    private int threadRefer(Reply reply) {
        return reply.getRefer() == 0 ? reply.getNo() : reply.getRefer();
    }

    public int write(Reply reply) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            int no = nextReplyNo(conn);

            String sql = "insert into reply(no, empno, content, writedate, idx_fk, "
                       + "                  refer, depth, step, deleted) "
                       + "values(?, ?, ?, sysdate, ?, ?, 0, 0, 0)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, no);
            pstmt.setInt(2, reply.getEmpno());
            pstmt.setString(3, reply.getContent());
            pstmt.setInt(4, reply.getIdx_fk());
            pstmt.setInt(5, no);

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
    }

    public int writeReply(int parentNo, Reply reply) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);
            conn.setAutoCommit(false);

            String parentSql = "select no, idx_fk, refer, depth, step from reply where no = ? and idx_fk = ? and deleted = 0";
            pstmt = conn.prepareStatement(parentSql);
            pstmt.setInt(1, parentNo);
            pstmt.setInt(2, reply.getIdx_fk());
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return 0;
            }

            int parentRefer = rs.getInt("refer");
            int parentDepth = rs.getInt("depth");
            int parentStep = rs.getInt("step");
            int refer = parentRefer == 0 ? parentNo : parentRefer;
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);

            String shiftSql = "update reply set step = step + 1 where idx_fk = ? and (case when refer = 0 then no else refer end) = ? and step > ?";
            pstmt = conn.prepareStatement(shiftSql);
            pstmt.setInt(1, reply.getIdx_fk());
            pstmt.setInt(2, refer);
            pstmt.setInt(3, parentStep);
            pstmt.executeUpdate();
            ConnectionHelper.close(pstmt);

            int no = nextReplyNo(conn);
            String insertSql = "insert into reply(no, empno, content, writedate, idx_fk, "
                             + "                  refer, depth, step, deleted) "
                             + "values(?, ?, ?, sysdate, ?, ?, ?, ?, 0)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, no);
            pstmt.setInt(2, reply.getEmpno());
            pstmt.setString(3, reply.getContent());
            pstmt.setInt(4, reply.getIdx_fk());
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

    private int nextReplyNo(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("select reply_seq.nextval from dual");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("reply_seq.nextval failed");
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
        }
    }

    public int update(Reply reply) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "update reply "
                       + "set content = ? "
                       + "where no = ? and empno = ? and deleted = 0";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reply.getContent());
            pstmt.setInt(2, reply.getNo());
            pstmt.setInt(3, reply.getEmpno());

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
    }

    public int delete(int no, int empno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "update reply set deleted = 1 where no = ? and empno = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, no);
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
}
