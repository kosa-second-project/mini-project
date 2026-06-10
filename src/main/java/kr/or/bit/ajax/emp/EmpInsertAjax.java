package kr.or.bit.ajax.emp;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.utils.SessionUtil;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/EmpInsertAjax")
public class EmpInsertAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Emp loginUser = SessionUtil.getLoginUser(request);
            if (loginUser == null) {
                SessionUtil.writeUnauthorizedJson(response, "로그인이 필요합니다.");
                return;
            }

            if (!SessionUtil.isAdminOrRepresentative(loginUser)) {
                SessionUtil.writeForbiddenJson(response, "사원 등록 권한이 없습니다.");
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
                    || ename == null || ename.trim().isEmpty()
                    || pwd == null || pwd.trim().isEmpty()) {
                out.write("{\"status\": \"fail\", \"message\": \"필수 입력 항목이 누락되었습니다.\"}");
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
            if (dao.getEmpByEmpno(empno) != null) {
                out.write("{\"status\": \"fail\", \"message\": \"이미 존재하는 사원번호입니다.\"}");
                return;
            }

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
                out.write("{\"status\": \"success\"}");
            } else {
                out.write("{\"status\": \"fail\", \"message\": \"사원 등록에 실패했습니다.\"}");
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
