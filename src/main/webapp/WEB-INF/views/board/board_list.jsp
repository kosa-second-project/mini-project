<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="kr.or.bit.dto.Board" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#44172;&#49884;&#44544; &#47785;&#47197;</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/style/board.css" rel="stylesheet">
</head>
<body>
<%
    List<Board> boardList = (List<Board>) request.getAttribute("boardList");
    Integer totalboardcount = (Integer) request.getAttribute("totalboardcount");
    Integer cpage = (Integer) request.getAttribute("cpage");
    Integer pagesize = (Integer) request.getAttribute("pagesize");
    String pager = (String) request.getAttribute("pager");

    if (totalboardcount == null) {
        totalboardcount = 0;
    }
    if (cpage == null) {
        cpage = 1;
    }
    if (pagesize == null) {
        pagesize = 5;
    }
%>

<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="mb-1">&#44172;&#49884;&#44544; &#47785;&#47197;</h2>
            <span class="text-muted">&#51204;&#52404; <%= totalboardcount %>&#44148; / &#54788;&#51116; <%= cpage %>&#54168;&#51060;&#51648;</span>
        </div>
        <a href="BoardWriteForm.do" class="btn btn-primary">&#44544;&#50416;&#44592;</a>
    </div>

    <div class="d-flex justify-content-end mb-3">
        <form action="BoardList.do" method="get" class="d-flex align-items-center gap-2">
            <label for="ps" class="form-label mb-0">&#54168;&#51060;&#51648; &#53356;&#44592;</label>
            <select name="ps" id="ps" class="form-select form-select-sm w-auto" onchange="this.form.submit()">
                <option value="5" <%= pagesize == 5 ? "selected" : "" %>>5&#44060;</option>
                <option value="10" <%= pagesize == 10 ? "selected" : "" %>>10&#44060;</option>
                <option value="20" <%= pagesize == 20 ? "selected" : "" %>>20&#44060;</option>
            </select>
            <input type="hidden" name="cp" value="1">
        </form>
    </div>

    <div class="table-responsive">
        <table class="table table-hover table-bordered align-middle text-center">
            <thead class="table-light">
                <tr>
                    <th style="width: 90px;">&#48264;&#54840;</th>
                    <th>&#51228;&#47785;</th>
                    <th style="width: 120px;">&#49324;&#48264;</th>
                    <th style="width: 160px;">&#51089;&#49457;&#51068;</th>
                    <th style="width: 100px;">&#51312;&#54924;&#49688;</th>
                </tr>
            </thead>
            <tbody>
            <%
                if (boardList != null && !boardList.isEmpty()) {
                    for (Board board : boardList) {
            %>
                <tr>
                    <td><%= board.getIdx() %></td>
                    <td class="text-start">
                        <a href="BoardDetail.do?idx=<%= board.getIdx() %>" class="link-dark text-decoration-none">
                            <%= board.getSubject() %>
                        </a>
                    </td>
                    <td><%= board.getEmpno() %></td>
                    <td><%= board.getWritedate() %></td>
                    <td><%= board.getReadnum() %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="5" class="text-muted py-4">&#46321;&#47197;&#46108; &#44172;&#49884;&#44544;&#51060; &#50630;&#49845;&#45768;&#45796;.</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

    <div class="text-center mt-4">
        <%= pager == null ? "" : pager %>
    </div>
</div>

<jsp:include page="/include/quickMenu.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
