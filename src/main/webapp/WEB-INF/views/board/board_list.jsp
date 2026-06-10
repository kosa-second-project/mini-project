<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="kr.or.bit.dto.Board" %>
<jsp:include page="/include/header.jsp">
    <jsp:param name="pageTitle" value="게시글 목록" />
    <jsp:param name="pageCss" value="/assets/css/board.css" />
</jsp:include>
<body class="app-shell">
<%
    List<Board> boardList = (List<Board>) request.getAttribute("boardList");
    Integer totalboardcount = (Integer) request.getAttribute("totalboardcount");
    Integer cpage = (Integer) request.getAttribute("cpage");
    Integer pagesize = (Integer) request.getAttribute("pagesize");
    Integer pagecount = (Integer) request.getAttribute("pagecount");

    if (totalboardcount == null) totalboardcount = 0;
    if (cpage == null) cpage = 1;
    if (pagesize == null) pagesize = 5;
    if (pagecount == null) pagecount = 0;

    int pagerSize = 5;
    int startPage = ((cpage - 1) / pagerSize) * pagerSize + 1;
    int endPage = startPage + pagerSize - 1;
    if (endPage > pagecount) endPage = pagecount;
%>

<div class="container mt-5 board-list-page"
     data-context-path="${pageContext.request.contextPath}"
     data-login-url="${pageContext.request.contextPath}/Login.emp"
     data-login-message="로그인한 사용자만 글을 작성할 수 있습니다."
     data-logged-in="${boardWriteAllowed}">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="mb-1">게시글 목록</h2>
            <span class="text-muted" id="boardListStatus">전체 <%= totalboardcount %>건 / 현재 <%= cpage %>페이지</span>
        </div>
        <a href="BoardWriteForm.do" class="btn btn-primary board-write-link">글쓰기</a>
    </div>

    <div class="d-flex justify-content-end mb-3">
        <form action="BoardList.do" method="get" class="d-flex align-items-center gap-2" id="boardPageSizeForm">
            <label for="ps" class="form-label mb-0">페이지 크기</label>
            <select name="ps" id="ps" class="form-select form-select-sm w-auto">
                <option value="5" <%= pagesize == 5 ? "selected" : "" %>>5개</option>
                <option value="10" <%= pagesize == 10 ? "selected" : "" %>>10개</option>
                <option value="20" <%= pagesize == 20 ? "selected" : "" %>>20개</option>
            </select>
            <input type="hidden" name="cp" value="1">
        </form>
    </div>

    <div class="table-responsive">
        <table class="table table-hover table-bordered align-middle text-center">
            <thead class="table-light">
                <tr>
                    <th style="width: 90px;">번호</th>
                    <th>제목</th>
                    <th style="width: 180px;">작성자</th>
                    <th style="width: 160px;">작성일</th>
                    <th style="width: 100px;">조회수</th>
                </tr>
            </thead>
            <tbody id="boardListBody">
            <%
                if (boardList != null && !boardList.isEmpty()) {
                    for (Board board : boardList) {
            %>
                <tr>
                    <td><%= board.getIdx() %></td>
                    <td class="text-start">
                        <span class="board-reply-indent" style="--reply-depth:<%= board.getDepth() %>"></span>
                        <% if (board.getDepth() > 0) { %><span class="board-reply-marker">↳</span><% } %>
                        <% if (board.isDeleted()) { %>
                            <span class="board-deleted-title">삭제된 게시판입니다.</span>
                        <% } else { %>
                            <a href="BoardDetail.do?idx=<%= board.getIdx() %>" class="board-title-link">
                                <%= board.getSubject() %>
                            </a>
                        <% } %>
                    </td>
                    <td><% if (board.isDeleted()) { %>-<% } else { %><%= (board.getEname() == null || board.getEname().isEmpty()) ? board.getEmpno() : board.getEname() %><% if (board.getDeptname() != null && !board.getDeptname().isEmpty()) { %> (<%= board.getDeptname() %>)<% } %><% } %></td>
                    <td><%= board.getWritedate() %></td>
                    <td><%= board.getReadnum() %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="5" class="text-muted py-4">등록된 게시글이 없습니다.</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

    <% if (pagecount > 0) { %>
        <nav class="board-pagination" aria-label="게시글 페이지">
            <ul class="pagination justify-content-center">
                <li class="page-item <%= cpage == 1 ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=1&ps=<%= pagesize %>" aria-label="처음">
                        <i class="bi bi-chevron-double-left"></i>
                    </a>
                </li>
                <li class="page-item <%= cpage == 1 ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=<%= cpage - 1 %>&ps=<%= pagesize %>" aria-label="이전">
                        <i class="bi bi-chevron-left"></i>
                    </a>
                </li>

                <% for (int i = startPage; i <= endPage; i++) { %>
                    <li class="page-item <%= i == cpage ? "active" : "" %>">
                        <a class="page-link" href="BoardList.do?cp=<%= i %>&ps=<%= pagesize %>"><%= i %></a>
                    </li>
                <% } %>

                <li class="page-item <%= cpage == pagecount ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=<%= cpage + 1 %>&ps=<%= pagesize %>" aria-label="다음">
                        <i class="bi bi-chevron-right"></i>
                    </a>
                </li>
                <li class="page-item <%= cpage == pagecount ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=<%= pagecount %>&ps=<%= pagesize %>" aria-label="마지막">
                        <i class="bi bi-chevron-double-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
    <% } %>
</div>

<jsp:include page="/include/quickMenu.jsp" />
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/board-list.js?v=<%=System.currentTimeMillis()%>"></script>
</body>
</html>
