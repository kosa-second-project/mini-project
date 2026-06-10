package kr.or.bit.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 비동기(AJAX) 서비스 클래스들이 구현해야 하는 인터페이스입니다.
 * 응답 바디에 데이터를 직접 출력하므로 이동 정보를 가진 ActionForward를 반환할 필요가 없습니다.
 */
public interface AjaxAction {
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
