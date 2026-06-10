package kr.or.bit.ajax.emp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;
import kr.or.bit.utils.SessionUtil;

@WebServlet("/EmpListAjax")
public class EmpListAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Emp loginUser = SessionUtil.getLoginUser(request);
            if (loginUser == null) {
                SessionUtil.writeUnauthorizedJson(response, "Unauthorized access");
                return;
            }

            EmpDao dao = EmpDao.getInstance();
            List<Emp> list;

            int cpage = 1;
            int pagesize = 5;

            String cpStr = request.getParameter("cp");
            String psStr = request.getParameter("ps");

            if (cpStr != null && !cpStr.trim().isEmpty()) {
                try {
                    cpage = Integer.parseInt(cpStr);
                    if (cpage < 1) cpage = 1;
                } catch (NumberFormatException e) {
                    cpage = 1;
                }
            }
            if (psStr != null && !psStr.trim().isEmpty()) {
                try {
                    int parsedPs = Integer.parseInt(psStr);
                    if (parsedPs == 5 || parsedPs == 10 || parsedPs == 20) {
                        pagesize = parsedPs;
                    }
                } catch (NumberFormatException e) {
                    pagesize = 5;
                }
            }

            int totalCount = 0;
            if (SessionUtil.isAdminOrRepresentative(loginUser)) {
                totalCount = dao.getEmpCount();
            } else {
                totalCount = dao.getEmpCountByDept(loginUser.getDeptno());
            }

            int pageCount = (totalCount / pagesize) + (totalCount % pagesize == 0 ? 0 : 1);
            if (pageCount == 0) pageCount = 1;
            if (cpage > pageCount) cpage = pageCount;

            if (SessionUtil.isAdminOrRepresentative(loginUser)) {
                list = dao.getEmpList(cpage, pagesize);
            } else {
                list = dao.getEmpListByDept(loginUser.getDeptno(), cpage, pagesize);
            }

            String listJson = listToJson(list);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"empList\":").append(listJson).append(",");
            sb.append("\"paging\":{");
            sb.append("\"totalCount\":").append(totalCount).append(",");
            sb.append("\"cpage\":").append(cpage).append(",");
            sb.append("\"pagesize\":").append(pagesize).append(",");
            sb.append("\"pagecount\":").append(pageCount);
            sb.append("}");
            sb.append("}");

            out.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\": \"fail\", \"message\": \"서버 통신 중 오류가 발생했습니다.\"}");
        }
    }

    private String listToJson(List<Emp> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Emp e = list.get(i);
                sb.append("{");
                sb.append("\"seq\":").append(e.getSeq()).append(",");
                sb.append("\"empno\":").append(e.getEmpno()).append(",");
                sb.append("\"ename\":\"").append(escapeJson(e.getEname())).append("\",");
                sb.append("\"job\":\"").append(escapeJson(e.getJob())).append("\",");
                sb.append("\"position\":\"").append(escapeJson(e.getPosition())).append("\",");
                sb.append("\"mgr\":").append(e.getMgr()).append(",");
                sb.append("\"hiredate\":\"").append(escapeJson(e.getHiredate())).append("\",");
                sb.append("\"sal\":").append(e.getSal()).append(",");
                sb.append("\"deptno\":").append(e.getDeptno()).append(",");
                sb.append("\"deptname\":\"").append(escapeJson(e.getDeptname())).append("\",");
                sb.append("\"role\":\"").append(escapeJson(e.getRole())).append("\"");
                sb.append("}");
                if (i < list.size() - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
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
