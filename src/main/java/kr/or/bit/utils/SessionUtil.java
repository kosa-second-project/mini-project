package kr.or.bit.utils;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.or.bit.dto.Emp;

public final class SessionUtil {
    public static final String LOGIN_USER = "loginUser";

    private SessionUtil() {
    }

    public static Emp getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object loginUser = session.getAttribute(LOGIN_USER);
        return loginUser instanceof Emp ? (Emp) loginUser : null;
    }

    public static Integer getLoginEmpno(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Integer empno = toEmpno(session.getAttribute(LOGIN_USER));
        if (empno != null) {
            return empno;
        }

        empno = toEmpno(session.getAttribute("empno"));
        if (empno != null) {
            return empno;
        }

        return toEmpno(session.getAttribute("loginEmpno"));
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoginUser(request) != null;
    }

    public static boolean isAdminOrRepresentative(Emp emp) {
        return emp != null && ("ADMIN".equalsIgnoreCase(emp.getRole()) || "대표".equals(emp.getPosition()));
    }

    public static void setLoginUser(HttpServletRequest request, Emp emp) {
        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, emp);
    }

    public static void writeUnauthorizedJson(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        writeJson(response, message);
    }

    public static void writeForbiddenJson(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        writeJson(response, message);
    }

    private static void writeJson(HttpServletResponse response, String message) throws IOException {
        response.getWriter().write("{\"status\": \"fail\", \"message\": \"" + escapeJson(message) + "\"}");
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

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
