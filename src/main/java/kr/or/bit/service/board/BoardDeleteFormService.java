package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;

public class BoardDeleteFormService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            request.setAttribute("board", BoardDao.getInstance().getBoard(BoardFormUtil.parseInt(request, "idx")));
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_delete.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardList.do");
        }
        return forward;
    }
}
