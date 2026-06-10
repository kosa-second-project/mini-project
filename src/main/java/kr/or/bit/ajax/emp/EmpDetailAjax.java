package kr.or.bit.ajax.emp;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.utils.SessionUtil;

@WebServlet("/EmpDetailAjax")
public class EmpDetailAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (!SessionUtil.isLoggedIn(request)) {
                SessionUtil.writeUnauthorizedJson(response, "Unauthorized access");
                return;
            }

            String empnoStr = request.getParameter("empno");
            if (empnoStr == null || empnoStr.trim().isEmpty()) {
                out.write("{\"status\": \"fail\", \"message\": \"사번 정보가 누락되었습니다.\"}");
                return;
            }

            int empno = Integer.parseInt(empnoStr.trim());
            EmpDao dao = EmpDao.getInstance();
            Emp emp = dao.getEmpByEmpno(empno);

            if (emp != null) {
                String json = empToJson(emp);
                out.write(json);
            } else {
                out.write("{\"status\": \"fail\", \"message\": \"존재하지 않는 사원입니다.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\": \"fail\", \"message\": \"서버 통신 중 오류가 발생했습니다.\"}");
        }
    }

    private String empToJson(Emp e) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"status\":\"success\",");
        sb.append("\"data\":{");
        sb.append("\"seq\":").append(e.getSeq()).append(",");
        sb.append("\"empno\":").append(e.getEmpno()).append(",");
        sb.append("\"ename\":\"").append(escapeJson(e.getEname())).append("\",");
        sb.append("\"job\":\"").append(escapeJson(e.getJob())).append("\",");
        sb.append("\"position\":\"").append(escapeJson(e.getPosition())).append("\",");
        sb.append("\"mgr\":").append(e.getMgr()).append(",");
        sb.append("\"hiredate\":\"").append(escapeJson(e.getHiredate())).append("\",");
        sb.append("\"sal\":").append(e.getSal()).append(",");
        sb.append("\"deptno\":").append(e.getDeptno()).append(",");
        sb.append("\"deptname\":\"").append(escapeJson(e.getDeptname())).append("\",");
        sb.append("\"role\":\"").append(escapeJson(e.getRole())).append("\"");
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String val) {
        if (val == null) return "";
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
