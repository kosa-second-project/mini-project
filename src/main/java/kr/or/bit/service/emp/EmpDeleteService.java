package kr.or.bit.service.emp;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;

public class EmpDeleteService implements Action {
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

        Emp loginUser = (Emp) session.getAttribute("loginUser");
        if (!"ADMIN".equalsIgnoreCase(loginUser.getRole()) && !"대표".equals(loginUser.getPosition())) {
            try {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 삭제 권한이 없습니다. (대표만 가능)\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        try {
            String empnoStr = request.getParameter("empno");
            if (empnoStr == null || empnoStr.trim().isEmpty()) {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사번 정보가 누락되었습니다.\"}");
                return null;
            }

            int empno = Integer.parseInt(empnoStr.trim());

            // 본인 삭제 방지
            if (loginUser.getEmpno() == empno) {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"본인 계정은 삭제할 수 없습니다.\"}");
                return null;
            }

            EmpDao dao = EmpDao.getInstance();
            int row = dao.deleteEmp(empno);
            if (row > 0) {
                response.getWriter().write("{\"status\": \"success\"}");
            } else {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 삭제에 실패했습니다.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"서버 오류로 인해 삭제하지 못했습니다.\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
