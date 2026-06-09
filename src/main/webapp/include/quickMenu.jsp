<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 
  공통 퀵메뉴 (토글 플로팅 버튼)
  디자인(CSS)과 스크립트(JS)가 분리된 정석 구조의 JSP 컴포넌트입니다.
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/quickMenu.css?v=<%=System.currentTimeMillis()%>">
<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/quickMenu.js?v=<%=System.currentTimeMillis()%>" defer></script>

<div class="quick-menu-wrapper" id="quickMenuWrapper">
    <!-- 서브 메뉴 목록 -->
    <ul class="quick-menu-list">
        <li>
            <div class="quick-menu-item-wrapper">
                <!-- 마우스 오버 시 왼쪽에 뜰 텍스트 라벨 -->
                <span class="quick-menu-label">메인 페이지</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onHomeClick()">
                    <span class="quick-menu-icon">🏠</span>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <!-- 마우스 오버 시 왼쪽에 뜰 텍스트 라벨 -->
                <span class="quick-menu-label">오늘의 날씨</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onWeatherClick()">
                    <span class="quick-menu-icon">☀️</span>
                </a>
            </div>
        </li>
        <li>
            <div class="quick-menu-item-wrapper">
                <!-- 마우스 오버 시 왼쪽에 뜰 텍스트 라벨 -->
                <span class="quick-menu-label">최단경로 조회</span>
                <a href="javascript:void(0);" class="quick-menu-item" onclick="onPathClick()">
                    <span class="quick-menu-icon">📍</span>
                </a>
            </div>
        </li>
    </ul>

    <!-- 메인 토글 버튼 (플러스 아이콘 구조화) -->
    <button type="button" class="quick-menu-trigger" id="quickMenuTrigger" aria-label="메뉴 열기">
        <span class="trigger-icon"></span>
    </button>
</div>
