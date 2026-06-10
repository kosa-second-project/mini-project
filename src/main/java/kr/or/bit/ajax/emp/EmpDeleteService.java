package kr.or.bit.ajax.emp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.AjaxAction;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.utils.SessionUtil;

public class EmpDeleteService implements AjaxAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        Emp loginUser = SessionUtil.getLoginUser(request);
        if (loginUser == null) {
            SessionUtil.writeUnauthorizedJson(response, "로그인이 필요합니다.");
            return;
        }

        if (!SessionUtil.isAdminOrRepresentative(loginUser)) {
            SessionUtil.writeForbiddenJson(response, "사원 삭제 권한이 없습니다.");
            return;
        }

        String empnoStr = request.getParameter("empno");
        if (empnoStr == null || empnoStr.trim().isEmpty()) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사번 정보가 누락되었습니다.\"}");
            return;
        }

        int empno = Integer.parseInt(empnoStr.trim());

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
