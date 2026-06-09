package kr.or.bit.service.emp;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import org.mindrot.jbcrypt.BCrypt;

public class EmpUpdateService implements Action {
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
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 수정 권한이 없습니다. (대표만 가능)\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        try {
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

            if (empnoStr == null || empnoStr.trim().isEmpty() ||
                ename == null || ename.trim().isEmpty()) {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"필수 입력 항목(사번, 이름)이 누락되었습니다.\"}");
                return null;
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

            // 수정 대상 사원 조회
            EmpDao dao = EmpDao.getInstance();
            Emp target = dao.getEmpByEmpno(empno);
            if (target == null) {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"존재하지 않는 사원입니다.\"}");
                return null;
            }

            // 비밀번호 변경 여부 처리
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
                    .pwd(hashedPwd) // NULL인 경우 패스워드 제외하고 수정
                    .role(role == null || role.trim().isEmpty() ? target.getRole() : role)
                    .build();

            int row = dao.updateEmp(updatedEmp);
            if (row > 0) {
                // 만약 로그인된 자신이 자신을 수정한 경우 세션을 새로고침해줄 수 있습니다.
                if (loginUser.getEmpno() == empno) {
                    Emp refreshed = dao.getEmpByEmpno(empno);
                    session.setAttribute("loginUser", refreshed);
                }
                response.getWriter().write("{\"status\": \"success\"}");
            } else {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 정보 수정에 실패했습니다.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"서버 오류로 인해 수정을 완료하지 못했습니다.\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
