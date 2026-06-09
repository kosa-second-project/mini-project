package kr.or.bit.service;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;
import kr.or.bit.utils.ThePager;

/**
 * 게시글 목록을 페이징 처리하여 보여주는 서비스입니다.
 */
public class BoardService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        try {
            BoardDao dao = BoardDao.getInstance();

            // 1. 게시물 총 건수 조회
            int totalboardcount = dao.totalBoardCount();

            // 2. 페이징 관련 파라미터 받기 (기본값 설정)
            String ps = request.getParameter("ps"); // Page size
            String cp = request.getParameter("cp"); // Current page

            if (ps == null || ps.trim().isEmpty()) {
                ps = "5"; // 기본값: 5개씩 보기
            }
            if (cp == null || cp.trim().isEmpty()) {
                cp = "1"; // 기본값: 1페이지 보기
            }

            int pagesize = Integer.parseInt(ps);
            int cpage = Integer.parseInt(cp);
            int pagecount = 0;

            // 3. 총 페이지 수 계산
            if (totalboardcount % pagesize == 0) {
                pagecount = totalboardcount / pagesize;
            } else {
                pagecount = (totalboardcount / pagesize) + 1;
            }

            // 4. 해당 페이지에 보여줄 데이터 목록 가져오기
            List<Board> list = dao.list(cpage, pagesize);

            // 5. 페이징 네비게이션(페이저) 바 생성
            ThePager pager = new ThePager(totalboardcount, cpage, pagesize, 3, "BoardList.do");

            // 6. JSP에서 사용할 수 있게 request에 데이터 담기
            request.setAttribute("totalboardcount", totalboardcount);
            request.setAttribute("pagesize", pagesize);
            request.setAttribute("cpage", cpage);
            request.setAttribute("pagecount", pagecount);
            request.setAttribute("boardList", list);
            request.setAttribute("pager", pager.toString());

            // 7.이동 경로 및 방식 설정 (Forward)
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_list.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return forward;
    }
}
