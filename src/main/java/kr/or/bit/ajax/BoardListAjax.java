package kr.or.bit.ajax;

import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.BoardDao;
import kr.or.bit.dto.Board;

public class BoardListAjax implements Action {
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int PAGER_SIZE = 5;

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");

        try {
            BoardDao dao = new BoardDao();
            int totalboardcount = dao.totalBoardCount();
            int pagesize = parsePositiveInt(request.getParameter("ps"), DEFAULT_PAGE_SIZE);
            int pagecount = calculatePageCount(totalboardcount, pagesize);
            int cpage = parsePositiveInt(request.getParameter("cp"), 1);

            if (pagecount > 0 && cpage > pagecount) {
                cpage = pagecount;
            }

            List<Board> boardList = dao.list(cpage, pagesize);
            response.getWriter().write(toJson(boardList, totalboardcount, cpage, pagesize, pagecount));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("{\"success\":false,\"message\":\"Board list load failed.\"}");
            } catch (Exception ignore) {
            }
        }

        return null;
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
                if (i > 0) {
                    json.append(",");
                }
                json.append("{");
                json.append("\"idx\":").append(board.getIdx()).append(",");
                json.append("\"empno\":").append(board.getEmpno()).append(",");
                json.append("\"subject\":\"").append(escapeJson(board.getSubject())).append("\",");
                json.append("\"writedate\":\"")
                        .append(board.getWritedate() == null ? "" : dateFormat.format(board.getWritedate()))
                        .append("\",");
                json.append("\"readnum\":").append(board.getReadnum());
                json.append("}");
            }
        }

        json.append("]");
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (ch < 32) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                    break;
            }
        }
        return escaped.toString();
    }
}
