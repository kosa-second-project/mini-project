<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 수정</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/board.css?v=20260609-map-button">
</head>
<body>
<main class="board-page">
    <h1>게시글 수정</h1>
    <c:if test="${not empty message}">
        <p class="error"><c:out value="${message}" /></p>
    </c:if>
    <c:choose>
        <c:when test="${empty board or board.deleted}">
            <p>수정할 수 없는 게시글입니다.</p>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </c:when>
        <c:otherwise>
            <form class="board-form" action="${pageContext.request.contextPath}/BoardEditOk.do" method="post">
                <input type="hidden" name="idx" value="${board.idx}">
                <p class="muted">작성 사원번호 ${board.empno}</p>
<!--                 <label>비밀번호
                    <input type="password" name="password" required>
                </label> -->
                <label>제목
                    <input type="text" name="subject" maxlength="100" value="${fn:escapeXml(board.subject)}" required>
                </label>
                <label>내용
                    <textarea name="content" rows="12" required><c:out value="${board.content}" /></textarea>
                </label>

                <section class="map-tools" data-map-mode="form">
                    <button type="button" id="mapToggleBtn" class="map-toggle-btn" data-open-text="지도 닫기" data-closed-text="지도 추가">지도 추가</button>
                    <div id="mapPanel" class="map-panel <c:if test="${empty board.lat or empty board.lng}">hidden</c:if>">
                        <div id="placeSearchForm" class="place-search-form">
                            <label for="keyword">장소 검색</label>
                            <div class="place-search-row">
                                <input type="text" id="keyword" placeholder="장소명을 입력하세요">
                                <button type="button" id="placeSearchBtn">검색</button>
                            </div>
                        </div>
                        <ul id="placesList" class="place-results"></ul>
                        <div id="map" class="map-box"></div>
                    </div>
                    <input type="hidden" name="lat" id="lat" value="${board.lat}">
                    <input type="hidden" name="lng" id="lng" value="${board.lng}">
                </section>

                <div class="actions">
                    <button type="submit">수정</button>
                    <a href="${pageContext.request.contextPath}/BoardDetail.do?idx=${board.idx}">취소</a>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/include/quickMenu.jsp" />
<c:if test="${not empty kakaoMapKey}">
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}&libraries=services"></script>
<script src="${pageContext.request.contextPath}/js/board-map.js?v=20260609-map-button"></script>
</c:if>
</body>
</html>
