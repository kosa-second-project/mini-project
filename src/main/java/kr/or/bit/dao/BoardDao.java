package kr.or.bit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import kr.or.bit.dto.Board;
import kr.or.bit.dto.Reply;

public class BoardDao {
    private DataSource ds = null;
    private static BoardDao instance = null;
    
    private BoardDao() throws NamingException {
        Context context = new InitialContext();
        ds = (DataSource) context.lookup("java:comp/env/jdbc/oracle");
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
    
    // Service 레이어에서 트랜잭션 단위로 제어할 Connection을 획득하기 위한 메소드
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    public int writeok(Board boarddata) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            conn = ds.getConnection();
            String sql = "insert into jspboard(idx, writer, pwd, subject, content, email, homepage, writedate, readnum, filename, filesize, refer)" + 
                         " values(jspboard_idx.nextval,?,?,?,?,?,?,sysdate,0,?,0,?)";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, boarddata.getWriter());
            pstmt.setString(2, boarddata.getPwd());
            pstmt.setString(3, boarddata.getSubject());
            pstmt.setString(4, boarddata.getContent());
            pstmt.setString(5, boarddata.getEmail());
            pstmt.setString(6, boarddata.getHomepage());
            pstmt.setString(7, boarddata.getFilename());
            pstmt.setInt(8, boarddata.getRefer());
            
            row = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return row;
    }

    public int getMaxRefer() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int referMax = 0;
        try {
            conn = ds.getConnection();
            String sql = "select nvl(max(refer), 0) from jspboard";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                referMax = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return referMax;
    }

    public List<Board> list(int cpage, int pagesize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Board> list = null;
        try {
            conn = ds.getConnection();
            String sql = "select * from " +
                         "(select rownum rn, idx, writer, email, homepage, pwd, subject, content, writedate, readnum " +
                         ", filename, filesize, refer, depth, step " +
                         " from ( select * from jspboard order by refer desc, step asc ) " +
                         " where rownum <= ?" + 
                         ") where rn >= ?";
            pstmt = conn.prepareStatement(sql);
            int start = cpage * pagesize - (pagesize - 1);
            int end = cpage * pagesize;
            pstmt.setInt(1, end);
            pstmt.setInt(2, start);
            
            rs = pstmt.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                 Board board = Board.builder()
                                    .idx(rs.getInt("idx"))
                                    .subject(rs.getString("subject"))
                                    .writer(rs.getString("writer"))
                                    .writedate(rs.getDate("writedate"))
                                    .readnum(rs.getInt("readnum"))
                                    .refer(rs.getInt("refer"))
                                    .step(rs.getInt("step"))
                                    .depth(rs.getInt("depth"))
                                    .filename(rs.getString("filename"))
                                    .build();
                 list.add(board);
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
    
    public int totalBoardCount() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int totalCount = 0;
        try {
            conn = ds.getConnection();
            String sql = "select count(*) cnt from jspboard";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalCount = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return totalCount;
    }

    public Board getContent(int idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Board board = null;
        try {
            conn = ds.getConnection();
            String sql = "select * from jspboard where idx=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String writer = rs.getString("writer");
                String email = rs.getString("email");
                String homepage = rs.getString("homepage");
                String pwd = rs.getString("pwd");
                String subject = rs.getString("subject");
                String content = rs.getString("content");
                String filename = rs.getString("filename");
                java.sql.Date writedate = rs.getDate("writedate");
                int readnum = rs.getInt("readnum");
                int filesize = rs.getInt("filesize");
                int refer = rs.getInt("refer");
                int step = rs.getInt("step");
                int depth = rs.getInt("depth");
                
                board = Board.builder()
                             .idx(idx)
                             .writer(writer)
                             .pwd(pwd)
                             .subject(subject)
                             .content(content)
                             .writedate(writedate)
                             .readnum(readnum)
                             .filename(filename)
                             .filesize(filesize)
                             .homepage(homepage)
                             .email(email)
                             .refer(refer)
                             .depth(depth)
                             .step(step)
                             .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return board;
    }

    public boolean getReadNum(String idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        try {
            conn = ds.getConnection();
            String sql = "update jspboard set readnum = readnum + 1 where idx=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idx);
            
            int row = pstmt.executeUpdate();
            if (row > 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return result;
    }

    // [트랜잭션/검증용] 게시글 비밀번호 조회 (단일 쿼리이므로 일반 커넥션 사용)
    public String getPwdById(String idx) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String pwd = null;
        try {
            conn = ds.getConnection();
            String sql = "select pwd from jspboard where idx=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                pwd = rs.getString("pwd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return pwd;
    }

    // [트랜잭션용] 특정 게시글의 모든 댓글 일괄 삭제
    public int deleteComments(Connection conn, String idx) throws SQLException {
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            String sql = "delete from reply where idx_fk=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idx);
            row = pstmt.executeUpdate();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
        }
        return row;
    }

    // [트랜잭션용] 특정 게시글 단건 삭제
    public int deletePost(Connection conn, String idx) throws SQLException {
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            String sql = "delete from jspboard where idx=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idx);
            row = pstmt.executeUpdate();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
        }
        return row;
    }

    public int replywrite(int idx_fk, String writer, String userid, String content, String pwd) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            conn = ds.getConnection();
            String sql = "insert into reply(no, writer, userid, content, pwd, idx_fk) values(reply_no.nextval,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, writer);
            pstmt.setString(2, userid);
            pstmt.setString(3, content);
            pstmt.setString(4, pwd);
            pstmt.setInt(5, idx_fk);
            
            row = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return row;
    }
        
    public List<Reply> replylist(String idx_fk) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Reply> list = null;
        try {
            conn = ds.getConnection();
            String replySql = "select * from reply where idx_fk=? order by no desc";
            pstmt = conn.prepareStatement(replySql);
            pstmt.setString(1, idx_fk);
            rs = pstmt.executeQuery();
            
            list = new ArrayList<>();
            while (rs.next()) {
                int no = rs.getInt("no");
                String writer = rs.getString("writer");
                String userid = rs.getString("userid");
                String pwd = rs.getString("pwd");
                String content = rs.getString("content");
                java.sql.Date writedate = rs.getDate("writedate");
                int idx = rs.getInt("idx_fk");
                
                Reply replydto = Reply.builder()
                                      .no(no)
                                      .writer(writer)
                                      .userid(userid)
                                      .pwd(pwd)
                                      .content(content)
                                      .writedate(writedate)
                                      .idx_fk(idx)
                                      .build();
                list.add(replydto);
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

    // [댓글 검증용] 댓글 비밀번호 가져오기 (단일 쿼리이므로 일반 커넥션 사용)
    public String getReplyPwdById(String no) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String pwd = null;
        try {
            conn = ds.getConnection();
            String sql = "select pwd from reply where no=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(no));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                pwd = rs.getString("pwd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return pwd;
    }

    // [댓글 삭제용] 댓글 삭제 (단건 삭제)
    public int deleteReplyById(String no) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            conn = ds.getConnection();
            String sql = "delete from reply where no=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(no));
            row = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return row;
    }

    // [트랜잭션용] 답글 입력을 위한 기존 정렬 순서(step) 업데이트
    public int updateStepForReply(Connection conn, int refer, int step) throws SQLException {
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            String sql = "update jspboard set step = step + 1 where step > ? and refer = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, step);
            pstmt.setInt(2, refer);
            row = pstmt.executeUpdate();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
        }
        return row;
    }

    // [트랜잭션용] 답글 생성
    public int insertReply(Connection conn, Board boardata) throws SQLException {
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            String sql = "insert into jspboard(idx, writer, pwd, subject, content, email, homepage, writedate, readnum, filename, filesize, refer, depth, step)" + 
                         " values(jspboard_idx.nextval,?,?,?,?,?,?,sysdate,0,?,0,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, boardata.getWriter());
            pstmt.setString(2, boardata.getPwd());
            pstmt.setString(3, boardata.getSubject());
            pstmt.setString(4, boardata.getContent());
            pstmt.setString(5, boardata.getEmail());
            pstmt.setString(6, boardata.getHomepage());
            pstmt.setString(7, boardata.getFilename());
            pstmt.setInt(8, boardata.getRefer());
            pstmt.setInt(9, boardata.getDepth());
            pstmt.setInt(10, boardata.getStep());
            
            row = pstmt.executeUpdate();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
        }
        return row;
    }

    public Board getEditContent(String idx) {
        return this.getContent(Integer.parseInt(idx));
    }
    
    public int boardEdit(Board boarddata) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int row = 0;
        try {
            conn = ds.getConnection();
            String sqlUpdate = "update jspboard set writer=?, email=?, homepage=?, subject=?, content=?, filename=? where idx=?";
            pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setString(1, boarddata.getWriter());
            pstmt.setString(2, boarddata.getEmail());
            pstmt.setString(3, boarddata.getHomepage());
            pstmt.setString(4, boarddata.getSubject());
            pstmt.setString(5, boarddata.getContent());
            pstmt.setString(6, boarddata.getFilename());
            pstmt.setInt(7, boarddata.getIdx());
            row = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
            if (conn != null) { try { conn.close(); } catch (Exception e) {} }
        }
        return row;
    }
}
