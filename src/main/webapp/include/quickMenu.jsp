<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/quickMenu.css?v=<%=System.currentTimeMillis()%>">
<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/quickMenu.js?v=<%=System.currentTimeMillis()%>" defer></script>

<div class="quick-menu-wrapper" id="quickMenuWrapper">
    <ul class="quick-menu-list">
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">메인 페이지</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onHomeClick()">
                    <span class="quick-menu-icon">홈</span>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">게시판</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onBoardClick()">
                    <span class="quick-menu-icon">글</span>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">오늘의 날씨</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onWeatherClick()">
                    <span class="quick-menu-icon">날</span>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">최단경로 조회</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onPathClick()">
                    <span class="quick-menu-icon">길</span>
                </a>
            </div>
        </li>
    </ul>

    <button type="button" class="quick-menu-trigger" id="quickMenuTrigger" aria-label="메뉴 열기">
        <span class="trigger-icon"></span>
    </button>
</div>
