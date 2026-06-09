package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

public class BoardWriteService implements Action {
    private static final int TEMP_TEST_EMPNO = 1001;

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            Integer empno = BoardFormUtil.getLoginEmpno(request);
            if (empno == null) {
                empno = TEMP_TEST_EMPNO;
                System.out.println("[BoardWrite] login empno is missing. use temp empno=" + empno);
            }

            Board board = Board.builder()
                    .empno(empno)
                    .subject(request.getParameter("subject"))
                    .content(request.getParameter("content"))
                    .lat(BoardFormUtil.parseFloatOrNull(request, "lat"))
                    .lng(BoardFormUtil.parseFloatOrNull(request, "lng"))
                    .build();

            BoardDao dao = BoardDao.getInstance();
            int idx = dao.insertBoard(board);
            System.out.println("[BoardWrite] empno=" + empno + ", inserted idx=" + idx);
            forward.setRedirect(true);
            String path = request.getContextPath() + (idx > 0 ? "/BoardDetail.do?idx=" + idx : "/BoardWriteForm.do?writeFail=1");
            System.out.println("[BoardWrite] redirect=" + path);
            forward.setPath(path);
        } catch (Exception e) {
            System.out.println("[BoardWrite] write failed: " + e.getMessage());
            e.printStackTrace();
            forward.setRedirect(true);
            forward.setPath(request.getContextPath() + "/BoardWriteForm.do?writeFail=1");
        }
        return forward;
    }
}
