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

public class EmpInsertService implements Action {
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
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 등록 권한이 없습니다. (대표만 가능)\"}");
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
                ename == null || ename.trim().isEmpty() ||
                pwd == null || pwd.trim().isEmpty()) {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"필수 입력 항목(사번, 이름, 비밀번호)이 누락되었습니다.\"}");
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

            // 사번 중복 검사
            EmpDao dao = EmpDao.getInstance();
            if (dao.getEmpByEmpno(empno) != null) {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"이미 존재하는 사원번호입니다.\"}");
                return null;
            }

            // 비밀번호 BCrypt 암호화
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());

            Emp newEmp = Emp.builder()
                    .empno(empno)
                    .ename(ename)
                    .job(job)
                    .position(position)
                    .mgr(mgr)
                    .hiredate(hiredate)
                    .sal(sal)
                    .deptno(deptno)
                    .pwd(hashedPwd)
                    .role(role == null || role.trim().isEmpty() ? "USER" : role)
                    .build();

            int row = dao.insertEmp(newEmp);
            if (row > 0) {
                response.getWriter().write("{\"status\": \"success\"}");
            } else {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원 등록에 실패했습니다. 입력값을 확인해주세요.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"서버 오류로 인해 등록에 실패했습니다.\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
