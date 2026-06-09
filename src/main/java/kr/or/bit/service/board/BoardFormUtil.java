package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

final class BoardFormUtil {
    static final String DEFAULT_KAKAO_MAP_KEY = "b0c25c0b9a953eb61c5e6b443815f027";

    private BoardFormUtil() {
    }

    static int parseInt(HttpServletRequest request, String name) {
        return Integer.parseInt(request.getParameter(name));
    }

    static Float parseFloatOrNull(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Float.parseFloat(value);
    }

    static Integer getLoginEmpno(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Integer empno = toEmpno(session.getAttribute("loginUser"));
        if (empno != null) {
            return empno;
        }

        empno = toEmpno(session.getAttribute("empno"));
        if (empno != null) {
            return empno;
        }

        return toEmpno(session.getAttribute("loginEmpno"));
    }

    private static Integer toEmpno(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String && !((String) value).trim().isEmpty()) {
            return Integer.parseInt((String) value);
        }
        try {
            Object empno = value.getClass().getMethod("getEmpno").invoke(value);
            return toEmpno(empno);
        } catch (Exception e) {
            return null;
        }
    }

    static void setKakaoMapKey(HttpServletRequest request) {
        String key = System.getProperty("kakao.map.javascript.key");
        if (key == null || key.trim().isEmpty()) {
            key = System.getenv("KAKAO_MAP_JAVASCRIPT_KEY");
        }
        request.setAttribute("kakaoMapKey",
                key == null || key.trim().isEmpty() ? DEFAULT_KAKAO_MAP_KEY : key);
    }
}
