package kr.or.bit.action;

/**
 * 컨트롤러가 비즈니스 로직 처리 후 이동할 페이지(Path)와 이동 방식(Redirect 여부)을 지정하는 클래스입니다.
 */
public class ActionForward {
    private boolean isRedirect = false; // true: redirect, false: forward
    private String path; // 이동할 경로

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean isRedirect) {
        this.isRedirect = isRedirect;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
