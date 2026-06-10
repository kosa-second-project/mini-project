package kr.or.bit.ajax.emp;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.AjaxAction;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;

public class EmpDetailService implements AjaxAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"Unauthorized access\"}");
            return;
        }

        String empnoStr = request.getParameter("empno");
        if (empnoStr == null || empnoStr.trim().isEmpty()) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사번 정보가 누락되었습니다.\"}");
            return;
        }

        int empno = Integer.parseInt(empnoStr.trim());
        EmpDao dao = EmpDao.getInstance();
        Emp emp = dao.getEmpByEmpno(empno);

        if (emp != null) {
            String json = empToJson(emp);
            response.getWriter().write(json);
        } else {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"존재하지 않는 사원입니다.\"}");
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
}
