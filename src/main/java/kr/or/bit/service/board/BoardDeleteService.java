package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

public class BoardDeleteService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            int idx = BoardFormUtil.parseInt(request, "idx");
            Integer empno = BoardFormUtil.getLoginEmpno(request);
            if (empno == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            BoardDao dao = new BoardDao();
            Board board = dao.getContent(idx);
//            if (empno == null || board == null || board.getEmpno() != empno
//                    || !new EmpService().validatePassword(empno, password)) {
//                request.setAttribute("message", "로그인 사원 정보 또는 비밀번호가 일치하지 않습니다.");
//                request.setAttribute("board", board);
//                forward.setRedirect(false);
//                forward.setPath("/WEB-INF/views/board/board_delete.jsp");
//                return forward;
//            }

            dao.delete(idx);
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
