package kr.or.bit.ajax.emp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.dao.DeptDao;
import kr.or.bit.dto.Dept;
import kr.or.bit.utils.SessionUtil;

@WebServlet("/DeptListAjax")
public class DeptListAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (!SessionUtil.isLoggedIn(request)) {
                SessionUtil.writeUnauthorizedJson(response, "로그인이 필요합니다.");
                return;
            }

            DeptDao dao = DeptDao.getInstance();
            List<Dept> list = dao.getDeptList();
            String json = deptListToJson(list);
            out.write(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\": \"fail\", \"message\": \"서버 통신 중 오류가 발생했습니다.\"}");
        }
    }

    private String deptListToJson(List<Dept> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (list != null) {
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
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String val) {
        if (val == null) {
            return "";
        }
        return val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
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
}
