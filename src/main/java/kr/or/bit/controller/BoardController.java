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
import kr.or.bit.service.board.BoardDetailService;
import kr.or.bit.service.board.BoardListService;
import kr.or.bit.service.board.ReplyDeleteService;
import kr.or.bit.service.board.ReplyUpdateService;
import kr.or.bit.service.board.ReplyWriteService;

/**
 * 모든 *.do 요청을 중앙에서 가로채 교통정리하는 대문 서블릿(Front Controller)입니다.
 */
@WebServlet("*.do")
public class BoardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String urlCommand = requestUri.substring(contextPath.length());

        System.out.println("urlCommand = " + urlCommand);

        Action action = null;
        ActionForward forward = null;

        // 1. 게시글 목록 조회
        if (urlCommand.equals("/BoardList.do")) {
            action = new BoardListService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/BoardDetail.do")) {
            action = new BoardDetailService();
            forward = action.execute(request, response);
        }
        // 2. 원글 쓰기 화면으로 이동 (단순 이동)
        else if (urlCommand.equals("/BoardWriteForm.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_write.jsp");
        }
        // 5. 답글 쓰기 화면으로 이동
        else if (urlCommand.equals("/BoardRewriteForm.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_rewrite.jsp");
        }
     
        // 7. 게시글 삭제 페이지로 이동 (단순 이동)
        else if (urlCommand.equals("/BoardDeleteForm.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/board/board_delete.jsp");
        } else if (urlCommand.equals("/ReplyWrite.do")) {
            action = new ReplyWriteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/ReplyDelete.do")) {
            action = new ReplyDeleteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/ReplyUpdate.do")) {
            action = new ReplyUpdateService();
            forward = action.execute(request, response);
        }
        // 8. 지하철 최단경로 조회 페이지 이동
        else if (urlCommand.equals("/Subway.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/subway/subway.jsp");
        }
        // 9. 오늘의 날씨 조회 페이지 이동
        else if (urlCommand.equals("/Weather.do")) {
            forward = new ActionForward();
            forward.setRedirect(false);
            forward.setPath("/WEB-INF/views/weather/weather.jsp");
        }

        // 공통 페이지 이동 처리
        if (forward != null) {
            if (forward.isRedirect()) {
                response.sendRedirect(forward.getPath());
            } else {
                RequestDispatcher dis = request.getRequestDispatcher(forward.getPath());
                dis.forward(request, response);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }
}
