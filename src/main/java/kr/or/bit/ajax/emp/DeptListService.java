package kr.or.bit.ajax.emp;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.AjaxAction;
import kr.or.bit.dao.DeptDao;
import kr.or.bit.dto.Dept;
import kr.or.bit.utils.SessionUtil;

public class DeptListService implements AjaxAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        if (!SessionUtil.isLoggedIn(request)) {
            SessionUtil.writeUnauthorizedJson(response, "로그인이 필요합니다.");
            return;
        }

        DeptDao dao = DeptDao.getInstance();
        List<Dept> list = dao.getDeptList();
        String json = deptListToJson(list);
        response.getWriter().write(json);
    }

    private String deptListToJson(List<Dept> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            Dept d = list.get(i);
            sb.append("{");
            sb.append("\"deptno\":").append(d.getDeptno()).append(",");
            sb.append("\"deptname\":\"").append(escapeJson(d.getDeptname())).append("\",");
            sb.append("\"loc\":\"").append(escapeJson(d.getLoc())).append("\"");
            sb.append("}");
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String val) {
        if (val == null) {
            return "";
        }
        return val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
