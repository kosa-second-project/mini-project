package kr.or.bit.service.board;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;
import kr.or.bit.utils.SessionUtil;

public class BoardListService implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            BoardDao dao = BoardDao.getInstance();

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

            if (pagesize <= 0) {
                pagesize = 5;
            }

            int pagecount = totalboardcount % pagesize == 0
                    ? totalboardcount / pagesize
                    : (totalboardcount / pagesize) + 1;

            if (cpage < 1) {
                cpage = 1;
            }

            if (pagecount > 0 && cpage > pagecount) {
                cpage = pagecount;
            }

            List<Board> list = dao.list(cpage, pagesize);

            request.setAttribute("totalboardcount", totalboardcount);
            request.setAttribute("pagesize", pagesize);
            request.setAttribute("cpage", cpage);
            request.setAttribute("pagecount", pagecount);
            request.setAttribute("boardList", list);
            request.setAttribute("boardWriteAllowed", SessionUtil.getLoginEmpno(request) != null);

            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_list.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forward;
    }
}
