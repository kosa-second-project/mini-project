package kr.or.bit.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kr.or.bit.action.AjaxAction;
import kr.or.bit.ajax.emp.*;

@WebServlet("*.ajax")
public class EmpAjaxController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String urlCommand = requestUri.substring(contextPath.length());

        System.out.println("EmpAjaxController urlCommand = " + urlCommand);

        AjaxAction action = null;

        if (urlCommand.equals("/LoginOk.ajax")) {
            action = new LoginOkService();
        } else if (urlCommand.equals("/EmpList.ajax")) {
            action = new EmpListService();
        } else if (urlCommand.equals("/EmpDetail.ajax")) {
            action = new EmpDetailService();
        } else if (urlCommand.equals("/EmpInsert.ajax")) {
            action = new EmpInsertService();
        } else if (urlCommand.equals("/EmpUpdate.ajax")) {
            action = new EmpUpdateService();
        } else if (urlCommand.equals("/EmpDelete.ajax")) {
            action = new EmpDeleteService();
        } else if (urlCommand.equals("/DeptList.ajax")) {
            action = new DeptListService();
        }

        if (action != null) {
            try {
                action.execute(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\": \"fail\", \"message\": \"서버 통신 중 오류가 발생했습니다.\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\": \"fail\", \"message\": \"요청한 API를 찾을 수 없습니다.\"}");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }
}
