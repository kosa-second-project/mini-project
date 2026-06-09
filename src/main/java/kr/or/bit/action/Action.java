package kr.or.bit.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 서비스 액션 클래스들이 구현해야 하는 인터페이스입니다.
 */
public interface Action {
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response);
}
