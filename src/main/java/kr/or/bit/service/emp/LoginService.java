package kr.or.bit.service.emp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();

        if (method.equalsIgnoreCase("GET")) {
            // GET 요청인 경우 로그인 페이지로 포워딩
            ActionForward forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/emp/login.jsp");
            return forward;
        } else {
            // POST 요청인 경우 AJAX 로그인 인증 수행
            response.setContentType("application/json;charset=UTF-8");
            try {
                String empnoStr = request.getParameter("empno");
                String pwd = request.getParameter("pwd");

                if (empnoStr == null || empnoStr.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
                    response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원번호와 비밀번호를 모두 입력해주세요.\"}");
                    return null;
                }

                int empno = Integer.parseInt(empnoStr.trim());
                EmpDao dao = EmpDao.getInstance();
                Emp emp = dao.getEmpByEmpno(empno);

                if (emp != null && BCrypt.checkpw(pwd, emp.getPwd())) {
                    // 로그인 성공 시 세션에 저장
                    HttpSession session = request.getSession();
                    session.setAttribute("loginUser", emp);
                    response.getWriter().write("{\"status\": \"success\"}");
                } else {
                    response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원번호 또는 비밀번호가 일치하지 않습니다.\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    response.getWriter().write("{\"status\": \"fail\", \"message\": \"로그인 중 서버 오류가 발생했습니다.\"}");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null; // AJAX 요청은 컨트롤러에서 포워딩하지 않음
        }
    }
}
