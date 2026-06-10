<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시글 삭제</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap-icons/bootstrap-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/folioone-theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css?v=20260609-map-layout">
</head>
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
            <p class="muted">작성자 <c:out value="${empty board.ename ? board.empno : board.ename}" /><c:if test="${not empty board.deptname}"> / <c:out value="${board.deptname}" /></c:if></p>
            <form class="board-form" action="${pageContext.request.contextPath}/BoardDeleteOk.do" method="post">
                <input type="hidden" name="idx" value="${board.idx}">
<!--                 <label>비밀번호
                    <input type="password" name="password" required>
                </label> -->
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
