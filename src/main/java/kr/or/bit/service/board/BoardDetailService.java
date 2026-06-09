package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

public class BoardDetailService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            int idx = BoardFormUtil.parseInt(request, "idx");
            BoardDao dao = BoardDao.getInstance();
            Board board = dao.getBoard(idx);

            if (board != null && !board.isDeleted()) {
                dao.increaseReadNum(idx);
                board = dao.getBoard(idx);
            }

            BoardFormUtil.setKakaoMapKey(request);
            request.setAttribute("board", board);
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_detail.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardList.do");
        }
        return forward;
    }
}
