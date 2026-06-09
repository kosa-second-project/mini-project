package kr.or.bit.service.emp;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.DeptDao;
import kr.or.bit.dto.Dept;

public class DeptListService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            try {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"로그인이 필요합니다.\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        try {
            DeptDao dao = DeptDao.getInstance();
            List<Dept> list = dao.getDeptList();
            String json = deptListToJson(list);
            response.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"Server error\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private String deptListToJson(List<Dept> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            Dept d = list.get(i);
            sb.append("{");
            sb.append("\"deptno\":").append(d.getDeptno()).append(",");
            sb.append("\"deptname\":\"").append(escapeJson(d.getDeptname())).append("\",");
            sb.append("\"loc\":\"").append(escapeJson(d.getLoc())).append("\"");
            sb.append("}");
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
