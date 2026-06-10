package kr.or.bit.service.emp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;

public class LoginService implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        // 로그인 페이지로 포워딩
        ActionForward forward = new ActionForward();
        forward.setRedirect(false);
        forward.setPath("/WEB-INF/views/emp/login.jsp");
        return forward;
    }
}
