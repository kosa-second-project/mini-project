package kr.or.bit.ajax.emp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.AjaxAction;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.utils.SessionUtil;
import org.mindrot.jbcrypt.BCrypt;

public class EmpUpdateService implements AjaxAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        Emp loginUser = SessionUtil.getLoginUser(request);
        if (loginUser == null) {
            SessionUtil.writeUnauthorizedJson(response, "로그인이 필요합니다.");
            return;
        }

        if (!SessionUtil.isAdminOrRepresentative(loginUser)) {
            SessionUtil.writeForbiddenJson(response, "사원 수정 권한이 없습니다.");
            return;
        }

        String empnoStr = request.getParameter("empno");
        String ename = request.getParameter("ename");
        String job = request.getParameter("job");
        String position = request.getParameter("position");
        String mgrStr = request.getParameter("mgr");
        String hiredate = request.getParameter("hiredate");
        String salStr = request.getParameter("sal");
        String deptnoStr = request.getParameter("deptno");
        String pwd = request.getParameter("pwd");
        String role = request.getParameter("role");

        if (empnoStr == null || empnoStr.trim().isEmpty()
                || ename == null || ename.trim().isEmpty()) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"필수 입력 항목이 누락되었습니다.\"}");
            return;
        }

        int empno = Integer.parseInt(empnoStr.trim());
        int mgr = 0;
        if (mgrStr != null && !mgrStr.trim().isEmpty()) {
            mgr = Integer.parseInt(mgrStr.trim());
        }
        int sal = 0;
        if (salStr != null && !salStr.trim().isEmpty()) {
            sal = Integer.parseInt(salStr.trim());
        }
        int deptno = 0;
        if (deptnoStr != null && !deptnoStr.trim().isEmpty()) {
            deptno = Integer.parseInt(deptnoStr.trim());
        }

        EmpDao dao = EmpDao.getInstance();
        Emp target = dao.getEmpByEmpno(empno);
        if (target == null) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"존재하지 않는 사원입니다.\"}");
            return;
        }

        String hashedPwd = null;
        if (pwd != null && !pwd.trim().isEmpty()) {
            hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
        }

        Emp updatedEmp = Emp.builder()
                .empno(empno)
                .ename(ename)
                .job(job)
                .position(position)
                .mgr(mgr)
                .hiredate(hiredate)
                .sal(sal)
                .deptno(deptno)
                .pwd(hashedPwd)
                .role(role == null || role.trim().isEmpty() ? target.getRole() : role)
                .build();

        int row = dao.updateEmp(updatedEmp);
        if (row > 0) {
            if (loginUser.getEmpno() == empno) {
                Emp refreshed = dao.getEmpByEmpno(empno);
                SessionUtil.setLoginUser(request, refreshed);
            }
            response.getWriter().write("{\"status\": \"success\"}");
        } else {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 정보 수정에 실패했습니다.\"}");
        }
    }
}
