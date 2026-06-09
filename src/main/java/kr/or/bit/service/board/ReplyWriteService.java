package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.ReplyDao;
import kr.or.bit.dto.Reply;

public class ReplyWriteService implements Action {
    private static final int LOGIN_EMPNO = 1060;

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            int idx_fk = Integer.parseInt(request.getParameter("idx_fk"));
            String content = request.getParameter("content");

            Reply reply = Reply.builder()
                    .idx_fk(idx_fk)
                    .empno(LOGIN_EMPNO)
                    .content(content)
                    .refer(0)
                    .depth(0)
                    .step(0)
                    .build();

            ReplyDao dao = new ReplyDao();
            dao.write(reply);

            forward.setRedirect(true);
            forward.setPath("BoardDetail.do?idx=" + idx_fk);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forward;
    }
}
