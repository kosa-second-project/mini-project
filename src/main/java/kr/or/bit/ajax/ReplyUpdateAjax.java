package kr.or.bit.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.dao.ReplyDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.dto.Reply;

@WebServlet("/ReplyUpdateAjax")
public class ReplyUpdateAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            out.print("{\"status\":\"fail\",\"message\":\"로그인 후 댓글을 수정할 수 있습니다.\"}");
            return;
        }

        Emp loginUser = (Emp) session.getAttribute("loginUser");
        int no = Integer.parseInt(request.getParameter("no"));
        int idx_fk = Integer.parseInt(request.getParameter("idx_fk"));
        String content = request.getParameter("content");

        if (content == null || content.trim().isEmpty()) {
            out.print("{\"status\":\"fail\",\"message\":\"댓글 내용을 입력해주세요.\"}");
            return;
        }

        Reply reply = Reply.builder()
                .no(no)
                .empno(loginUser.getEmpno())
                .content(content.trim())
                .build();

        ReplyDao dao = new ReplyDao();
        int row = dao.update(reply);

        if (row <= 0) {
            out.print("{\"status\":\"fail\",\"message\":\"댓글 수정 권한이 없거나 댓글이 존재하지 않습니다.\"}");
            return;
        }

        List<Reply> list = dao.list(idx_fk);
        StringBuilder json = new StringBuilder();

        json.append("{\"status\":\"success\",");
        json.append("\"loginEmpno\":").append(loginUser.getEmpno()).append(",");
        json.append("\"replyCount\":").append(list == null ? 0 : list.size()).append(",");
        json.append("\"replies\":[");

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Reply r = list.get(i);
                json.append("{");
                json.append("\"no\":").append(r.getNo()).append(",");
                json.append("\"empno\":").append(r.getEmpno()).append(",");
                json.append("\"content\":\"").append(r.getContent() == null ? "" : r.getContent().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n")).append("\",");
                json.append("\"writedate\":\"").append(r.getWritedate()).append("\",");
                json.append("\"idx_fk\":").append(r.getIdx_fk()).append(",");
                json.append("\"canEdit\":").append(loginUser.getEmpno() == r.getEmpno());
                json.append("}");
                if (i < list.size() - 1) json.append(",");
            }
        }

        json.append("]}");
        out.print(json.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }
}
