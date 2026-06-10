<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/include/header.jsp">
    <jsp:param name="pageTitle" value="게시글 삭제" />
    <jsp:param name="pageCss" value="/assets/css/board.css" />
</jsp:include>
<body class="app-shell">
<main class="board-page narrow">
    <h1>게시글 삭제</h1>
    <c:if test="${not empty message}">
        <p class="error"><c:out value="${message}" /></p>
    </c:if>
    <c:choose>
        <c:when test="${empty board}">
            <p>삭제할 게시글을 찾을 수 없습니다.</p>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </c:when>
        <c:otherwise>
            <p><strong><c:out value="${board.subject}" /></strong> 게시글을 삭제하시겠습니까?</p>
            <p class="muted">작성자 <c:out value="${empty board.ename ? board.empno : board.ename}" /><c:if test="${not empty board.deptname}"> (<c:out value="${board.deptname}" />)</c:if></p>
            <form class="board-form" action="${pageContext.request.contextPath}/BoardDeleteOk.do" method="post">
                <input type="hidden" name="idx" value="${board.idx}">
                <div class="actions">
                    <button type="submit">삭제</button>
                    <a href="${pageContext.request.contextPath}/BoardDetail.do?idx=${board.idx}">취소</a>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</main>
<jsp:include page="/include/quickMenu.jsp" />
</body>
</html>
