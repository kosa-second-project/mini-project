package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;
import kr.or.bit.utils.SessionUtil;

public class BoardEditService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        int idx = 0;
        try {
            idx = BoardFormUtil.parseInt(request, "idx");
            Integer empno = SessionUtil.getLoginEmpno(request);
            if (empno == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            BoardDao dao = BoardDao.getInstance();
            Board original = dao.getContent(idx);
            if (original == null || original.isDeleted() || original.getEmpno() != empno) {
                request.setAttribute("message", "본인이 작성한 게시글만 수정할 수 있습니다.");
                request.setAttribute("board", original);
                forward.setRedirect(false);
                forward.setPath("/WEB-INF/views/board/board_edit.jsp");
                return forward;
            }

            Board board = Board.builder()
                    .idx(idx)
                    .empno(empno)
                    .subject(request.getParameter("subject"))
                    .content(request.getParameter("content"))
                    .build();
            int row = dao.update(board);

            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardDetail.do?idx=" + idx + (row > 0 ? "" : "&editFail=1"));
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + (idx > 0 ? "/BoardEditForm.do?idx=" + idx : "/BoardList.do"));
        }
        return forward;
    }
}
