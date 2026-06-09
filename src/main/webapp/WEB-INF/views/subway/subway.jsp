<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Wayfinder - 지하철 최단경로 탐색기</title>
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    
    <!-- Lucide Icons (CDN) -->
    <script src="https://unpkg.com/lucide@latest"></script>
    
    <!-- Context Path 적용한 외부 스타일시트 링크 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/subway.css">
</head>
<body>

    <header>
        <div class="logo">
            <i data-lucide="subway"></i>
            <span>METRO WAYFINDER</span>
        </div>
        <div id="modeBadge" class="api-badge api-real">
            <span id="badgeDot" class="badge-dot pulse" style="background-color: var(--accent);"></span>
            <span id="badgeText">실시간 공공데이터 연동</span>
        </div>
    </header>

    <main>
        <!-- Left Column: Search & Settings -->
        <div class="panel">
            <div class="panel-title">
                <i data-lucide="search-code"></i>
                <span>경로 탐색 설정</span>
            </div>

            <!-- Inputs -->
            <div class="input-group">
                <label for="dptreInput">출발역 (Departure)</label>
                <div class="input-wrapper">
                    <input type="text" id="dptreInput" placeholder="출발할 지하철역 입력 (예: 서울역, 강남)" autocomplete="off">
                    <i data-lucide="navigation"></i>
                </div>
                <div id="dptreSuggestions" class="autocomplete-suggestions"></div>
            </div>

            <button class="btn-swap" id="btnSwap" title="출발역 / 도착역 교환" aria-label="출발역과 도착역 변경">
                <i data-lucide="arrow-up-down"></i>
            </button>

            <div class="input-group">
                <label for="arvlInput">목적역 (Destination)</label>
                <div class="input-wrapper">
                    <input type="text" id="arvlInput" placeholder="도착할 지하철역 입력 (예: 신도림, 홍대입구)" autocomplete="off">
                    <i data-lucide="map-pin"></i>
                </div>
                <div id="arvlSuggestions" class="autocomplete-suggestions"></div>
            </div>

            <button class="btn-search" id="btnSearch">
                <i data-lucide="route"></i>
                <span>경로 탐색 시작</span>
            </button>

            <!-- Quick Selection -->
            <div class="quick-links">
                <div class="quick-links-title">주요 노선 및 빠른 검색</div>
                <div class="quick-chips">
                    <button class="chip" onclick="quickSelect('서울역', '신도림')">서울역 → 신도림</button>
                    <button class="chip" onclick="quickSelect('강남', '홍대입구')">강남 → 홍대입구</button>
                    <button class="chip" onclick="quickSelect('고속터미널', '성수')">고속터미널 → 성수</button>
                    <button class="chip" onclick="quickSelect('종로3가', '사당')">종로3가 → 사당</button>
                </div>
            </div>

            <!-- API Secret Details -->
            <div class="settings-toggle" id="settingsToggle">
                <span>공공데이터 API 설정 변경</span>
                <i data-lucide="chevron-down" id="settingsChevron"></i>
            </div>

            <div class="settings-content" id="settingsContent">
                <div class="input-group">
                    <label for="apiKeyInput">인증키 (Service Key)</label>
                    <div class="input-wrapper">
                        <input type="text" id="apiKeyInput" value="vHBYWqU149oaFLCprcymaJOpENMdDOPzzLdjjbZDv8jZBSzOim1gsSWrNaaEfNK02dC5Ti%2B4agRjQo8%2B5vwGlA%3D%3D" placeholder="인증키 입력">
                        <i data-lucide="key"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Right Column: Results -->
        <div class="results-container">
            <!-- Results View -->
            <div class="result-card" id="resultCard">
                <div class="panel-title">
                    <i data-lucide="info"></i>
                    <span>탐색 결과</span>
                </div>

                <div class="stats-grid">
                    <div class="stat-box">
                        <span class="stat-value" id="statTime">--</span>
                        <span class="stat-label">소요 시간</span>
                    </div>
                    <div class="stat-box">
                        <span class="stat-value" id="statStations">--</span>
                        <span class="stat-label">정차역 수</span>
                    </div>
                    <div class="stat-box">
                        <span class="stat-value" id="statTransfers">--</span>
                        <span class="stat-label">환승 횟수</span>
                    </div>
                    <div class="stat-box">
                        <span class="stat-value" id="statDistance">--</span>
                        <span class="stat-label">총 거리</span>
                    </div>
                </div>

                <!-- Train Animation Zone -->
                <div class="animation-zone">
                    <div class="rail-track"></div>
                    <div class="rail-sleeper"></div>
                    
                    <div class="station-node start">
                        <span class="station-node-label start-label" id="animStartName">출발역</span>
                    </div>
                    
                    <div class="animated-train" id="animTrain">
                        <div class="train-body">
                            <div class="train-window"></div>
                            <div class="train-window"></div>
                            <div class="train-window"></div>
                            <div class="train-glow"></div>
                            <div class="train-wheel wheel-left"></div>
                            <div class="train-wheel wheel-right"></div>
                        </div>
                    </div>
                    
                    <div class="station-node end">
                        <span class="station-node-label end-label" id="animEndName">도착역</span>
                    </div>
                </div>

                <!-- Route Timeline -->
                <div class="route-timeline" id="routeTimeline">
                    <!-- Dynamic rendering -->
                </div>
            </div>

            <!-- Placeholder View -->
            <div class="placeholder-card" id="placeholderCard">
                <i data-lucide="train"></i>
                <h2>이동하실 경로를 입력해 주세요</h2>
                <p>출발역과 도착역을 입력하고 '경로 탐색 시작' 버튼을 누르시면,<br>지하철 최단 경로와 실시간 예상 주행 정보가 표시됩니다.</p>
            </div>
        </div>
    </main>

    <!-- 공통 플로팅 퀵메뉴 연동 -->
    <jsp:include page="/include/quickMenu.jsp" />

    <!-- Context Path 적용한 외부 스크립트 링크 -->
    <script src="${pageContext.request.contextPath}/js/stations.js"></script>
    <script src="${pageContext.request.contextPath}/js/subway.js"></script>
</body>
</html>
