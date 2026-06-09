package kr.or.bit.service.emp;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.dao.EmpDao;
import kr.or.bit.dto.Emp;

public class EmpListService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            try {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"Unauthorized access\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        try {
            Emp loginUser = (Emp) session.getAttribute("loginUser");
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
            if ("ADMIN".equalsIgnoreCase(loginUser.getRole()) || "대표".equals(loginUser.getPosition())) {
                totalCount = dao.getEmpCount();
            } else {
                totalCount = dao.getEmpCountByDept(loginUser.getDeptno());
            }

            int pageCount = (totalCount / pagesize) + (totalCount % pagesize == 0 ? 0 : 1);
            if (pageCount == 0) pageCount = 1;
            if (cpage > pageCount) cpage = pageCount;

            if ("ADMIN".equalsIgnoreCase(loginUser.getRole()) || "대표".equals(loginUser.getPosition())) {
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

            response.getWriter().write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"Server error\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private String listToJson(List<Emp> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
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
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
