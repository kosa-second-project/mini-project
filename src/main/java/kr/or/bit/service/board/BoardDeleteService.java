package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;
import kr.or.bit.utils.SessionUtil;

public class BoardDeleteService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            int idx = BoardFormUtil.parseInt(request, "idx");
            Integer empno = SessionUtil.getLoginEmpno(request);
            if (empno == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            BoardDao dao = BoardDao.getInstance();
            Board board = dao.getContent(idx);
            if (board == null || board.getEmpno() != empno) {
                request.setAttribute("message", "본인이 작성한 게시글만 삭제할 수 있습니다.");
                request.setAttribute("board", board);
                forward.setRedirect(false);
                forward.setPath("/WEB-INF/views/board/board_delete.jsp");
                return forward;
            }

            dao.delete(idx, empno);
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardList.do");
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardList.do");
        }
        return forward;
    }
}
