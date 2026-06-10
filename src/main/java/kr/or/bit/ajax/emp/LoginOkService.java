package kr.or.bit.ajax.emp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.AjaxAction;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import org.mindrot.jbcrypt.BCrypt;

public class LoginOkService implements AjaxAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        String empnoStr = request.getParameter("empno");
        String pwd = request.getParameter("pwd");

        if (empnoStr == null || empnoStr.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"사원번호와 비밀번호를 모두 입력해주세요.\"}");
            return;
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
    }
}
