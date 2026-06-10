package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.or.bit.dto.Reply;
import kr.or.bit.utils.ConnectionHelper;
import kr.or.bit.utils.DBType;

public class ReplyDao {

    public List<Reply> list(int idx_fk) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Reply> list = null;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "select r.no, r.empno, e.ename, d.deptname, r.content, r.writedate, "
                       + "       r.idx_fk, r.refer, r.depth, r.step, r.deleted "
                       + "from reply r "
                       + "left join emp e on r.empno = e.empno "
                       + "left join dept d on e.deptno = d.deptno "
                       + "where r.idx_fk = ? and r.deleted = 0 "
                       + "order by r.no desc";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx_fk);

            rs = pstmt.executeQuery();

            list = new ArrayList<>();

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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(rs);
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return list;
    }

    public int write(Reply reply) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;

        try {
            conn = ConnectionHelper.getConnection(DBType.ORACLE);

            String sql = "insert into reply(no, empno, content, writedate, idx_fk, "
                       + "                  refer, depth, step, deleted) "
                       + "values(reply_seq.nextval, ?, ?, sysdate, ?, ?, ?, ?, 0)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reply.getEmpno());
            pstmt.setString(2, reply.getContent());
            pstmt.setInt(3, reply.getIdx_fk());
            pstmt.setInt(4, reply.getRefer());
            pstmt.setInt(5, reply.getDepth());
            pstmt.setInt(6, reply.getStep());

            row = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(pstmt);
            ConnectionHelper.close(conn);
        }

        return row;
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
