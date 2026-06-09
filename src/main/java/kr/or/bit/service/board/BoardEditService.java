package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

public class BoardEditService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        int idx = 0;
        try {
            idx = BoardFormUtil.parseInt(request, "idx");
            Integer empno = BoardFormUtil.getLoginEmpno(request);
            if (empno == null) {
                forward.setRedirect(true);
                forward.setPath(request.getContextPath() + "/Login.emp");
                return forward;
            }

            BoardDao dao = new BoardDao();
            Board original = dao.getContent(idx);
//            if (empno == null || original == null || original.isDeleted() || original.getEmpno() != empno
//                    || !new EmpService().validatePassword(empno, password)) {
//                request.setAttribute("message", "로그인 사원 정보 또는 비밀번호가 일치하지 않습니다.");
//                BoardFormUtil.setKakaoMapKey(request);
//                request.setAttribute("board", original);
//                forward.setRedirect(false);
//                forward.setPath("/WEB-INF/views/board/board_edit.jsp");
//                return forward;
//            }

            Board board = Board.builder()
                    .idx(idx)
                    .subject(request.getParameter("subject"))
                    .content(request.getParameter("content"))
                    .lat(BoardFormUtil.parseFloatOrNull(request, "lat"))
                    .lng(BoardFormUtil.parseFloatOrNull(request, "lng"))
                    .build();
            dao.update(board);

            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardDetail.do?idx=" + idx);
        } catch (Exception e) {
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + (idx > 0 ? "/BoardEditForm.do?idx=" + idx : "/BoardList.do"));
        }
        return forward;
    }
}
