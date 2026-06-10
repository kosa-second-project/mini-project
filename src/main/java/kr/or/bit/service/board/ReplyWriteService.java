package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.ReplyDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.dto.Reply;
import kr.or.bit.utils.SessionUtil;

public class ReplyWriteService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            Emp loginUser = SessionUtil.getLoginUser(request);
            if (loginUser == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            int idx_fk = Integer.parseInt(request.getParameter("idx_fk"));
            String content = request.getParameter("content");

            Reply reply = Reply.builder()
                    .idx_fk(idx_fk)
                    .empno(loginUser.getEmpno())
                    .content(content)
                    .refer(0)
                    .depth(0)
                    .step(0)
                    .build();

            ReplyDao dao = ReplyDao.getInstance();
            dao.write(reply);

            forward.setRedirect(true);
            forward.setPath("BoardDetail.do?idx=" + idx_fk);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forward;
    }
}
