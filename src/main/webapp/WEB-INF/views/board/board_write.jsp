<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시글 작성</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap-icons/bootstrap-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/folioone-theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css?v=20260609-map-button">
</head>
<body class="app-shell">
<c:if test="${loginRequired}">
    <input type="checkbox" id="loginModalToggle" class="modal-toggle" checked>
    <div class="modal-backdrop">
        <section class="modal-box" role="alertdialog" aria-modal="true" aria-labelledby="loginModalTitle">
            <h2 id="loginModalTitle">로그인 필요</h2>
            <p>로그인 후 게시글을 작성할 수 있습니다.</p>
            <label for="loginModalToggle" class="modal-close">확인</label>
        </section>
    </div>
</c:if>

<main class="board-page">
    <h1>게시글 작성</h1>
    <form class="board-form" action="${pageContext.request.contextPath}/BoardWriteOk.do" method="post">
        <label>제목
            <input type="text" name="subject" maxlength="100" required>
        </label>
        <label>내용
            <textarea name="content" rows="12" required></textarea>
        </label>

        <section class="map-tools" data-map-mode="form">
            <button type="button" id="mapToggleBtn" class="map-toggle-btn" data-open-text="지도 닫기" data-closed-text="지도 추가">지도 추가</button>
            <div id="mapPanel" class="map-panel hidden">
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
            <input type="hidden" name="lat" id="lat">
            <input type="hidden" name="lng" id="lng">
        </section>

        <div class="actions">
            <button type="submit">등록</button>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </div>
    </form>
</main>

<jsp:include page="/include/quickMenu.jsp" />
<c:if test="${not empty kakaoMapKey}">
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}&libraries=services"></script>
<script src="${pageContext.request.contextPath}/js/board-map.js?v=20260609-map-button"></script>
</c:if>
</body>
</html>
