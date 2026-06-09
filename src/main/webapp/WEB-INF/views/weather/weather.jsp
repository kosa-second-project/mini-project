<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Garak Weather - 오늘의 날씨 예보</title>
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    
    <!-- Lucide Icons (CDN) -->
    <script src="https://unpkg.com/lucide@latest"></script>
    
    <!-- 캐시 방지 적용한 스타일시트 링크 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/weather.css?v=<%=System.currentTimeMillis()%>">
</head>
<body>

    <header>
        <div class="logo">
            <i data-lucide="cloud-sun"></i>
            <span>GARAK WEATHER</span>
        </div>
        <div class="api-badge">
            <span class="badge-dot"></span>
            <span>기상청 단기예보 동기화</span>
        </div>
    </header>

    <main>
        <!-- 로딩 스피너 영역 -->
        <div class="loader-container" id="weatherLoader">
            <div class="loader-spinner"></div>
            <p>기상청으로부터 실시간 초단기예보 데이터를 수집하는 중...</p>
        </div>

        <!-- 에러 알림 영역 -->
        <div class="error-container" id="weatherError" style="display: none;">
            <i data-lucide="alert-circle"></i>
            <h3>날씨 정보를 불러오지 못했습니다</h3>
            <p id="errorMsg">네트워크 상태를 확인하거나 잠시 후 다시 시도해 주세요.</p>
        </div>

        <!-- 본문 날씨 대시보드 영역 -->
        <div id="weatherContainer" style="display: none;">
            
            <!-- 섹션 1: 실시간 기상 현황 -->
            <div class="section-title">
                <i data-lucide="sun"></i>
                <span>실시간 기상 현황 (가락시장역 기준)</span>
            </div>
            
            <div class="weather-main-panel">
                <!-- 왼쪽: 기온 및 상태 메인 요약 -->
                <div class="weather-hero">
                    <span class="weather-big-icon" id="heroIcon">☀️</span>
                    <span class="weather-temp-now" id="heroTemp">--°C</span>
                    <span class="weather-status-text" id="heroStatus">맑음</span>
                    <span class="weather-location" id="heroTime">
                        <i data-lucide="clock" style="width: 14px; height: 14px;"></i>
                        조회 중...
                    </span>
                </div>

                <!-- 오른쪽: 세부 수치 목록 -->
                <div class="weather-details-grid">
                    <div class="detail-box">
                        <div class="detail-icon-wrapper">
                            <i data-lucide="thermometer"></i>
                        </div>
                        <div class="detail-info">
                            <span class="detail-label">현재 기온</span>
                            <span class="detail-value" id="valSky">--</span>
                        </div>
                    </div>
                    <div class="detail-box">
                        <div class="detail-icon-wrapper">
                            <i data-lucide="droplets"></i>
                        </div>
                        <div class="detail-info">
                            <span class="detail-label">현재 습도</span>
                            <span class="detail-value" id="valHumid">--%</span>
                        </div>
                    </div>
                    <div class="detail-box">
                        <div class="detail-icon-wrapper">
                            <i data-lucide="wind"></i>
                        </div>
                        <div class="detail-info">
                            <span class="detail-label">풍속</span>
                            <span class="detail-value" id="valWind">-- m/s</span>
                        </div>
                    </div>
                    <div class="detail-box">
                        <div class="detail-icon-wrapper">
                            <i data-lucide="umbrella"></i>
                        </div>
                        <div class="detail-info">
                            <span class="detail-label">강수량</span>
                            <span class="detail-value" id="valRain">-- mm</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 섹션 2: 향후 6시간 동안의 시간별 날씨 예보 -->
            <div class="section-title" style="margin-top: 20px;">
                <i data-lucide="calendar-days"></i>
                <span>향후 6시간 상세 예보 (기상청 초단기 예보)</span>
            </div>

            <div class="forecast-timeline-container">
                <div class="forecast-grid" id="forecastTimeline">
                    <!-- 자바스크립트로 동적 렌더링 카드 주입 -->
                </div>
            </div>
            
        </div>
    </main>

    <!-- 공통 플로팅 퀵메뉴 연동 -->
    <jsp:include page="/include/quickMenu.jsp" />

    <!-- 캐시 방지 적용한 외부 스크립트 링크 -->
    <script src="${pageContext.request.contextPath}/js/weather.js?v=<%=System.currentTimeMillis()%>"></script>
    <script>
        // 초기 헤더 아이콘 렌더링
        lucide.createIcons();
    </script>
</body>
</html>
