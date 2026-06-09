package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

public class BoardEditFormService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            BoardDao dao = new BoardDao();
            Board board = dao.getContent(BoardFormUtil.parseInt(request, "idx"));
            BoardFormUtil.setKakaoMapKey(request);
            request.setAttribute("board", board);
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_edit.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardList.do");
        }
        return forward;
    }
}
