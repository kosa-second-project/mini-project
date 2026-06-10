package kr.or.bit.service.board;

import jakarta.servlet.http.HttpServletRequest;

final class BoardFormUtil {
    private BoardFormUtil() {
    }

    static int parseInt(HttpServletRequest request, String name) {
        return Integer.parseInt(request.getParameter(name));
    }
}
