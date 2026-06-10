<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/quickMenu.css?v=<%=System.currentTimeMillis()%>">
<script src="${pageContext.request.contextPath}/js/quickMenu.js?v=<%=System.currentTimeMillis()%>" defer></script>

<div class="quick-menu-wrapper" id="quickMenuWrapper" data-context-path="${pageContext.request.contextPath}">
    <ul class="quick-menu-list">
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">메인 페이지</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onHomeClick()" aria-label="메인 페이지로 이동">
                    <i class="bi bi-house-door quick-menu-icon" aria-hidden="true"></i>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">게시판</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onBoardClick()" aria-label="게시판으로 이동">
                    <i class="bi bi-journal-text quick-menu-icon" aria-hidden="true"></i>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">오늘의 날씨</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onWeatherClick()" aria-label="오늘의 날씨로 이동">
                    <i class="bi bi-cloud-sun quick-menu-icon" aria-hidden="true"></i>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <span class="quick-menu-label">최단경로 조회</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onPathClick()" aria-label="최단경로 조회로 이동">
                    <i class="bi bi-signpost-split quick-menu-icon" aria-hidden="true"></i>
                </a>
            </div>
        </li>
    </ul>

    <button type="button" class="quick-menu-trigger" id="quickMenuTrigger" aria-label="메뉴 열기">
        <span class="trigger-icon"></span>
    </button>
</div>
