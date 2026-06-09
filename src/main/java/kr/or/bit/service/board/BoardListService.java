package kr.or.bit.service.board;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;
import kr.or.bit.utils.ThePager;

public class BoardListService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            BoardDao dao = new BoardDao();

            int totalboardcount = dao.totalBoardCount();

            String ps = request.getParameter("ps");
            String cp = request.getParameter("cp");

            if (ps == null || ps.trim().isEmpty()) {
                ps = "5";
            }

            if (cp == null || cp.trim().isEmpty()) {
                cp = "1";
            }

            int pagesize = Integer.parseInt(ps);
            int cpage = Integer.parseInt(cp);
            int pagecount = 0;

            if (totalboardcount % pagesize == 0) {
                pagecount = totalboardcount / pagesize;
            } else {
                pagecount = (totalboardcount / pagesize) + 1;
            }

            List<Board> list = dao.list(cpage, pagesize);
            ThePager pager = new ThePager(totalboardcount, cpage, pagesize, 3, "BoardList.do");

            request.setAttribute("totalboardcount", totalboardcount);
            request.setAttribute("pagesize", pagesize);
            request.setAttribute("cpage", cpage);
            request.setAttribute("pagecount", pagecount);
            request.setAttribute("boardList", list);
            request.setAttribute("pager", pager.toString());

            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_list.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forward;
    }
}
