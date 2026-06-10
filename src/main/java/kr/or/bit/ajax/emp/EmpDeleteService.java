package kr.or.bit.ajax.emp;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.AjaxAction;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;

public class EmpDeleteService implements AjaxAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"로그인이 필요합니다.\"}");
            return;
        }

        Emp loginUser = (Emp) session.getAttribute("loginUser");
        if (!"ADMIN".equalsIgnoreCase(loginUser.getRole()) && !"대표".equals(loginUser.getPosition())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 삭제 권한이 없습니다. (대표만 가능)\"}");
            return;
        }

        String empnoStr = request.getParameter("empno");
        if (empnoStr == null || empnoStr.trim().isEmpty()) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사번 정보가 누락되었습니다.\"}");
            return;
        }

        int empno = Integer.parseInt(empnoStr.trim());

        // 본인 삭제 방지
        if (loginUser.getEmpno() == empno) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"본인 계정은 삭제할 수 없습니다.\"}");
            return;
        }

        EmpDao dao = EmpDao.getInstance();
        int row = dao.deleteEmp(empno);
        if (row > 0) {
            response.getWriter().write("{\"status\": \"success\"}");
        } else {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 삭제에 실패했습니다.\"}");
        }
    }
}
