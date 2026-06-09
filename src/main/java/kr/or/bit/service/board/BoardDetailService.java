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
            int idx = Integer.parseInt(request.getParameter("idx"));

            BoardDao boardDao = new BoardDao();
            ReplyDao replyDao = new ReplyDao();

            Board board = boardDao.getContent(idx);
            List<Reply> replyList = replyDao.list(idx);

            request.setAttribute("idx", idx);
            request.setAttribute("board", board);
            request.setAttribute("replyList", replyList);

            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_detail.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forward;
    }
}
