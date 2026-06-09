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
import kr.or.bit.service.emp.*;

@WebServlet("*.emp")
public class EmpController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String urlCommand = requestUri.substring(contextPath.length());

        System.out.println("EmpController urlCommand = " + urlCommand);

        Action action = null;
        ActionForward forward = null;

        if (urlCommand.equals("/Login.emp") || urlCommand.equals("/LoginOk.emp")) {
            action = new LoginService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/Logout.emp")) {
            action = new LogoutService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/Main.emp")) {
            action = new MainService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/EmpList.emp")) {
            action = new EmpListService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/EmpDetail.emp")) {
            action = new EmpDetailService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/EmpInsert.emp")) {
            action = new EmpInsertService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/EmpUpdate.emp")) {
            action = new EmpUpdateService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/EmpDelete.emp")) {
            action = new EmpDeleteService();
            forward = action.execute(request, response);
        } else if (urlCommand.equals("/DeptList.emp")) {
            action = new DeptListService();
            forward = action.execute(request, response);
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
