package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;

public final class BoardFormUtil {
    private static final String DEFAULT_KAKAO_MAP_KEY = "b0c25c0b9a953eb61c5e6b443815f027";

    private BoardFormUtil() {
    }

    static int parseInt(HttpServletRequest request, String name) {
        return Integer.parseInt(request.getParameter(name));
    }

    public static void setKakaoMapKey(HttpServletRequest request) {
        String key = System.getProperty("kakao.map.javascript.key");
        if (key == null || key.trim().isEmpty()) {
            key = System.getenv("KAKAO_MAP_JAVASCRIPT_KEY");
        }
        request.setAttribute("kakaoMapKey",
                key == null || key.trim().isEmpty() ? DEFAULT_KAKAO_MAP_KEY : key);
    }
}
