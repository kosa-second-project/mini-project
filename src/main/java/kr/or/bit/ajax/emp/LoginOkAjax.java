package kr.or.bit.ajax.emp;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/LoginOkAjax")
public class LoginOkAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String empnoStr = request.getParameter("empno");
            String pwd = request.getParameter("pwd");

            if (empnoStr == null || empnoStr.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
                out.write("{\"status\": \"fail\", \"message\": \"사원번호와 비밀번호를 모두 입력해주세요.\"}");
                return;
            }

            int empno = Integer.parseInt(empnoStr.trim());
            EmpDao dao = EmpDao.getInstance();
            Emp emp = dao.getEmpByEmpno(empno);

            if (emp != null && BCrypt.checkpw(pwd, emp.getPwd())) {
                // 로그인 성공 시 세션에 저장
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", emp);
                out.write("{\"status\": \"success\"}");
            } else {
                out.write("{\"status\": \"fail\", \"message\": \"사원번호 또는 비밀번호가 일치하지 않습니다.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\": \"fail\", \"message\": \"서버 통신 중 오류가 발생했습니다.\"}");
        }
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
