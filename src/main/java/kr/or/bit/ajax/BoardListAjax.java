package kr.or.bit.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

@WebServlet("/BoardListAjax.do")
public class BoardListAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int PAGER_SIZE = 5;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BoardDao dao = BoardDao.getInstance();
            int totalboardcount = dao.totalBoardCount();
            int pagesize = parsePositiveInt(request.getParameter("ps"), DEFAULT_PAGE_SIZE);
            int pagecount = calculatePageCount(totalboardcount, pagesize);
            int cpage = parsePositiveInt(request.getParameter("cp"), 1);

            if (pagecount > 0 && cpage > pagecount) {
                cpage = pagecount;
            }

            List<Board> boardList = dao.list(cpage, pagesize);
            out.print(toJson(boardList, totalboardcount, cpage, pagesize, pagecount));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"게시글 목록을 불러오지 못했습니다.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    private int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int calculatePageCount(int totalboardcount, int pagesize) {
        return totalboardcount % pagesize == 0
                ? totalboardcount / pagesize
                : (totalboardcount / pagesize) + 1;
    }

    private String toJson(List<Board> boardList, int totalboardcount, int cpage, int pagesize, int pagecount) {
        int startPage = ((cpage - 1) / PAGER_SIZE) * PAGER_SIZE + 1;
        int endPage = startPage + PAGER_SIZE - 1;
        if (endPage > pagecount) {
            endPage = pagecount;
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":true,");
        json.append("\"totalboardcount\":").append(totalboardcount).append(",");
        json.append("\"cpage\":").append(cpage).append(",");
        json.append("\"pagesize\":").append(pagesize).append(",");
        json.append("\"pagecount\":").append(pagecount).append(",");
        json.append("\"startPage\":").append(startPage).append(",");
        json.append("\"endPage\":").append(endPage).append(",");
        json.append("\"boardList\":[");

        if (boardList != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < boardList.size(); i++) {
                Board board = boardList.get(i);
                json.append("{");
                json.append("\"idx\":").append(board.getIdx()).append(",");
                json.append("\"empno\":").append(board.getEmpno()).append(",");
                json.append("\"ename\":\"").append(escapeJson(writerName(board))).append("\",");
                json.append("\"deptname\":\"").append(escapeJson(board.getDeptname())).append("\",");
                json.append("\"subject\":\"").append(escapeJson(board.getSubject())).append("\",");
                json.append("\"writedate\":\"")
                        .append(board.getWritedate() == null ? "" : dateFormat.format(board.getWritedate()))
                        .append("\",");
                json.append("\"readnum\":").append(board.getReadnum());
                json.append("}");
                if (i < boardList.size() - 1) json.append(",");
            }
        }

        json.append("]}");
        return json.toString();
    }

    private String writerName(Board board) {
        if (board.getEname() != null && !board.getEname().trim().isEmpty()) {
            return board.getEname();
        }
        return String.valueOf(board.getEmpno());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
