package kr.or.bit.service.board;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dao.ReplyDao;
import kr.or.bit.dto.Board;
import kr.or.bit.dto.Reply;

public class BoardDetailService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            int idx = BoardFormUtil.parseInt(request, "idx");

            BoardDao boardDao = BoardDao.getInstance();
            ReplyDao replyDao = ReplyDao.getInstance();

            Board board = boardDao.getContent(idx);
            if (board != null && !board.isDeleted()) {
                boardDao.getReadNum(idx);
                board = boardDao.getContent(idx);
            }

            List<Reply> replyList = replyDao.list(idx);

            request.setAttribute("idx", idx);
            request.setAttribute("board", board);
            request.setAttribute("replyList", replyList);

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
