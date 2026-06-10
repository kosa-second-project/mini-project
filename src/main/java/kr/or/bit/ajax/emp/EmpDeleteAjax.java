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

@WebServlet("/EmpDeleteAjax")
public class EmpDeleteAjax extends HttpServlet {
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
                SessionUtil.writeForbiddenJson(response, "사원 삭제 권한이 없습니다.");
                return;
            }

            String empnoStr = request.getParameter("empno");
            if (empnoStr == null || empnoStr.trim().isEmpty()) {
                out.write("{\"status\": \"fail\", \"message\": \"사번 정보가 누락되었습니다.\"}");
                return;
            }

            int empno = Integer.parseInt(empnoStr.trim());

            if (loginUser.getEmpno() == empno) {
                out.write("{\"status\": \"fail\", \"message\": \"본인 계정은 삭제할 수 없습니다.\"}");
                return;
            }

            EmpDao dao = EmpDao.getInstance();
            int row = dao.deleteEmp(empno);
            if (row > 0) {
                out.write("{\"status\": \"success\"}");
            } else {
                out.write("{\"status\": \"fail\", \"message\": \"사원 삭제에 실패했습니다.\"}");
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
