package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.ReplyDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.dto.Reply;

public class ReplyUpdateService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loginUser") == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            Emp loginUser = (Emp) session.getAttribute("loginUser");
            int no = Integer.parseInt(request.getParameter("no"));
            int idx_fk = Integer.parseInt(request.getParameter("idx_fk"));
            String content = request.getParameter("content");

            Reply reply = Reply.builder()
                    .no(no)
                    .empno(loginUser.getEmpno())
                    .content(content)
                    .build();

            ReplyDao dao = new ReplyDao();
            dao.update(reply);

            forward.setRedirect(true);
            forward.setPath("BoardDetail.do?idx=" + idx_fk);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forward;
    }
}
