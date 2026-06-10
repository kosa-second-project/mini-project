package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;
import kr.or.bit.utils.SessionUtil;

public class BoardWriteService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            Integer empno = SessionUtil.getLoginEmpno(request);
            if (empno == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            Board board = Board.builder()
                    .empno(empno)
                    .subject(request.getParameter("subject"))
                    .content(request.getParameter("content"))
                    .build();

            BoardDao dao = BoardDao.getInstance();
            int parentIdx = parseParentIdx(request);
            int row = parentIdx > 0 ? dao.writeReply(parentIdx, board) : dao.write(board);

            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + (row > 0 ? "/BoardList.do" : "/BoardWriteForm.do?writeFail=1"));
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardWriteForm.do?writeFail=1");
        }
        return forward;
    }

    private int parseParentIdx(HttpServletRequest request) {
        String value = request.getParameter("parentIdx");
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }
}
