<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/board.css?v=20260609-map-layout">
</head>
<body>
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
                    <span>사원번호 ${board.empno}</span>
                    <span>작성일 ${board.writedate}</span>
                    <span>조회수 ${board.readnum}</span>
                </div>
                <pre class="content"><c:out value="${board.content}" /></pre>

                <c:if test="${not empty board.lat and not empty board.lng}">
                    <section class="map-section" data-map-mode="detail" data-lat="${board.lat}" data-lng="${board.lng}">
                        <h2>위치</h2>
                        <div id="map" class="map-box"></div>
                    </section>
                </c:if>

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
<c:if test="${not empty kakaoMapKey and not empty board.lat and not empty board.lng}">
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}"></script>
<script src="${pageContext.request.contextPath}/js/board-map.js?v=20260609-map-layout"></script>
</c:if>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
