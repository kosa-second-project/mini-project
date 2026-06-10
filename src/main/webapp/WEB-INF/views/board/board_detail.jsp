<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세</title>
<link href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/folioone-theme.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css?v=20260609-map-layout">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/reply.css">
</head>
<body class="app-shell">
<main class="board-page">
    <c:choose>
        <c:when test="${empty board}">
            <h1>게시글을 찾을 수 없습니다.</h1>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </c:when>
        <c:otherwise>
            <article class="board-detail">
                <h1><c:out value="${board.subject}" /></h1>
                <div class="meta">
                    <span>글 번호 ${board.idx}</span>
                    <span>작성자 <c:out value="${empty board.ename ? board.empno : board.ename}" /></span>
                    <c:if test="${not empty board.deptname}">
                        <span>부서 <c:out value="${board.deptname}" /></span>
                    </c:if>
                    <span>작성일 ${board.writedate}</span>
                    <span>조회수 ${board.readnum}</span>
                </div>
                <textarea id="boardContentSource" class="editor-source"><c:out value="${board.content}" /></textarea>
                <div id="boardContent" class="content rich-content" data-editor-mode="detail"></div>

                <div class="actions">
                    <a href="${pageContext.request.contextPath}/BoardEditForm.do?idx=${board.idx}">수정</a>
                    <a href="${pageContext.request.contextPath}/BoardDeleteForm.do?idx=${board.idx}">삭제</a>
                    <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
                </div>
            </article>

            <jsp:include page="/WEB-INF/views/board/reply_section.jsp"/>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/include/quickMenu.jsp" />
<c:if test="${not empty kakaoMapKey}">
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}"></script>
</c:if>
<script src="${pageContext.request.contextPath}/js/board-editor.js?v=20260610-inline-map"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
