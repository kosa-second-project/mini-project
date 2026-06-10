package kr.or.bit.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.bit.action.Action;
import kr.or.bit.action.ActionForward;
import kr.or.bit.service.board.BoardDeleteFormService;
import kr.or.bit.service.board.BoardDeleteService;
import kr.or.bit.service.board.BoardDetailService;
import kr.or.bit.service.board.BoardEditFormService;
import kr.or.bit.service.board.BoardEditService;
import kr.or.bit.service.board.BoardFormUtil;
import kr.or.bit.service.board.BoardListService;
import kr.or.bit.service.board.BoardWriteService;
import kr.or.bit.service.board.ReplyDeleteService;
import kr.or.bit.service.board.ReplyUpdateService;
import kr.or.bit.service.board.ReplyWriteService;
import kr.or.bit.utils.SessionUtil;

@WebServlet("*.do")
public class BoardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String urlCommand = requestUri.substring(contextPath.length());

        Action action = null;
        ActionForward forward = null;

        if (urlCommand.equals("/BoardList.do")) {
            action = new BoardListService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardWriteForm.do")) {
            if (SessionUtil.getLoginEmpno(request) == null) {
                sendLoginRequiredAlert(response, request.getContextPath() + "/Login.emp");
                return;
            }
            request.setAttribute("loginRequired", false);
            BoardFormUtil.setKakaoMapKey(request);
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_write.jsp");
        } else if (urlCommand.equals("/BoardWriteOk.do")) {
            action = new BoardWriteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardDetail.do")) {
            action = new BoardDetailService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardEditForm.do")) {
            action = new BoardEditFormService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardEditOk.do")) {
            action = new BoardEditService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardDeleteForm.do")) {
            action = new BoardDeleteFormService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardDeleteOk.do")) {
            action = new BoardDeleteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/ReplyWrite.do")) {
            action = new ReplyWriteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/ReplyDelete.do")) {
            action = new ReplyDeleteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/ReplyUpdate.do")) {
            action = new ReplyUpdateService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/Subway.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/subway/subway.jsp");
        } else if (urlCommand.equals("/Weather.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/weather/weather.jsp");
        }

        if (forward != null) {
            if (forward.isRedirect()) {
                response.sendRedirect(forward.getPath());
            } else {
                RequestDispatcher dis = request.getRequestDispatcher(forward.getPath());
                dis.forward(request, response);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    private void sendLoginRequiredAlert(HttpServletResponse response, String loginUrl) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>"
                + "<script>"
                + "alert('로그인한 사용자만 글을 작성할 수 있습니다.');"
                + "location.href='" + loginUrl + "';"
                + "</script>"
                + "</body></html>");
    }
}
