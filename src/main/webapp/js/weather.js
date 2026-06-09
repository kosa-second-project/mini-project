/**
 * 기상청 초단기예보(getUltraSrtFcst) API 연동 스크립트
 * 대상 지역: 서울특별시 송파구 가락동 (가락시장역 nx=62, ny=126)
 */

document.addEventListener("DOMContentLoaded", function() {
    // 공공데이터포털에서 발급받은 인코딩된 인증키 사용 (더블 인코딩 방지)
    const serviceKey = "T48sPzRyTh1HbSib%2Bkf6MN50dBZ8aQJO7h5YkYC%2FPNtIHGUBdQag2eTvj2%2FripL8xZxV%2BQl9jtGSWxofDPLM1g%3D%3D";
    
    // 날씨 조회 시작
    loadTodayWeather(serviceKey);
});

/**
 * 날씨 정보를 가져오는 주 함수
 */
async function loadTodayWeather(key) {
    const loader = document.getElementById("weatherLoader");
    const container = document.getElementById("weatherContainer");
    const errorBox = document.getElementById("weatherError");

    try {
        // 1. 발표 시간 및 날짜 계산
        const { baseDate, baseTime } = getBaseDateTime();
        console.log(`기상청 API 요청 파라미터 - baseDate: ${baseDate}, baseTime: ${baseTime}`);

        // 2. API 데이터 수신
        const data = await fetchWeatherData(key, baseDate, baseTime);
        
        // 3. UI 렌더링
        renderWeather(data);
        
        // 로더 숨기고 컨테이너 표시
        loader.style.display = "none";
        container.style.display = "block";
    } catch (err) {
        console.error("날씨 정보 로드 실패:", err);
        loader.style.display = "none";
        errorBox.style.display = "flex";
        document.getElementById("errorMsg").textContent = err.message || "데이터를 불러오는 중 오류가 발생했습니다.";
    }
}

/**
 * 기상청 발표 기점 시간 계산기
 * 초단기 예보는 매시간 45분에 공식 발표(업데이트)되며, 데이터 상의 기준 시각은 매시 30분입니다.
 */
function getBaseDateTime() {
    const now = new Date();
    let year = now.getFullYear();
    let month = now.getMonth() + 1;
    let day = now.getDate();
    let hours = now.getHours();
    let minutes = now.getMinutes();

    // 매시 45분 이전인 경우, 전 시간 데이터를 사용해야 함
    if (minutes < 45) {
        hours -= 1;
        if (hours < 0) {
            // 자정이 지나 45분 전이면 날짜를 어제로 백
            const yesterday = new Date(now.setDate(now.getDate() - 1));
            year = yesterday.getFullYear();
            month = yesterday.getMonth() + 1;
            day = yesterday.getDate();
            hours = 23;
        }
    }

    const baseDate = `${year}${String(month).padStart(2, '0')}${String(day).padStart(2, '0')}`;
    const baseTime = `${String(hours).padStart(2, '0')}30`; // API가 요구하는 30분 단위 설정
    return { baseDate, baseTime };
}

/**
 * 기상청 OpenAPI 네트워크 요청 (CORS 프록시 우회 백업 적용)
 */
async function fetchWeatherData(key, baseDate, baseTime) {
    const baseUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
    
    // nx, ny는 가락시장역 기준 (62, 126) 고정
    const nx = 62;
    const ny = 126;
    
    // URL 수동 조립하여 인코딩 오류 예방 (이미 인코딩된 키이므로 그대로 사용)
    const targetUrl = `${baseUrl}?serviceKey=${key}&dataType=JSON&numOfRows=60&pageNo=1&base_date=${baseDate}&base_time=${baseTime}&nx=${nx}&ny=${ny}`;
    
    // 1차 시도: 직접 Fetch
    try {
        const response = await fetch(targetUrl);
        if (response.ok) {
            const data = await response.json();
            if (isValidResponse(data)) {
                return parseWeatherResponse(data);
            }
        }
    } catch (e) {
        console.log("기상청 API 직접 호출 실패 (CORS 또는 네트워크). 프록시로 우회 시도합니다...");
    }

    // 2차 시도: CORS 우회 프록시 (allorigins)
    const proxyUrl = `https://api.allorigins.win/raw?url=${encodeURIComponent(targetUrl)}`;
    const responseProxy = await fetch(proxyUrl);
    if (!responseProxy.ok) {
        throw new Error("날씨 API 호출 및 프록시 우회 요청에 모두 실패했습니다.");
    }
    
    const dataProxy = await responseProxy.json();
    if (isValidResponse(dataProxy)) {
        return parseWeatherResponse(dataProxy);
    } else {
        // 응답 상세 에러 판별
        const header = dataProxy.header || (dataProxy.response && dataProxy.response.header);
        const errMsg = header ? header.resultMsg : "인증 실패 또는 잘못된 요청 파라미터입니다.";
        throw new Error(errMsg);
    }
}

/**
 * 공공데이터 응답 유효성 체크
 */
function isValidResponse(data) {
    const header = data.header || (data.response && data.response.header);
    return header && (header.resultCode === "00" || header.resultCode === "0");
}

/**
 * 기상청 카테고리별 시간 데이터 가공 및 그룹화
 */
function parseWeatherResponse(data) {
    const body = data.body || (data.response && data.response.body);
    if (!body || !body.items || !body.items.item) {
        throw new Error("날씨 데이터의 본문(Body)을 가져올 수 없습니다.");
    }

    const rawItems = body.items.item;
    const forecastMap = {};

    // 시간 단위로 카테고리 매핑
    rawItems.forEach(item => {
        const time = item.fcstTime; // 예: "1800"
        if (!forecastMap[time]) {
            forecastMap[time] = {
                time: time,
                date: item.fcstDate
            };
        }
        forecastMap[time][item.category] = item.fcstValue;
    });

    // 시간순으로 정렬된 예보 목록 배열 변환
    const sortedForecast = Object.values(forecastMap).sort((a, b) => a.time.localeCompare(b.time));
    
    return sortedForecast;
}

/**
 * 날씨 화면 UI 렌더링
 */
function renderWeather(forecastList) {
    if (forecastList.length === 0) return;

    // 1. 현재 시점 기상 정보 (목록의 첫 번째 데이터 기준)
    const current = forecastList[0];
    const curTimeFormatted = formatTimeLabel(current.time);
    
    // 카테고리 정보 정리
    const temp = current.T1H; // 기온
    const reh = current.REH; // 습도
    const wsd = current.WSD; // 풍속
    const rn1 = current.RN1; // 1시간 강수량 (예: "강수없음", "1mm")

    const weatherInfo = getWeatherStatus(current.SKY, current.PTY);

    // 2. 메인 카드 업데이트
    document.getElementById("heroIcon").textContent = weatherInfo.icon;
    document.getElementById("heroTemp").textContent = `${temp}°C`;
    document.getElementById("heroStatus").textContent = weatherInfo.text;
    document.getElementById("heroTime").textContent = `기상청 발표 기준: ${curTimeFormatted}`;

    // 세부 수치 업데이트
    document.getElementById("valHumid").textContent = `${reh}%`;
    document.getElementById("valWind").textContent = `${wsd} m/s`;
    document.getElementById("valRain").textContent = rn1 === "강수없음" ? "0 mm" : rn1;
    document.getElementById("valSky").textContent = weatherInfo.text;

    // 3. 시간별 단기 예보 타임라인 렌더링
    const timeline = document.getElementById("forecastTimeline");
    timeline.innerHTML = "";

    forecastList.forEach((fcst) => {
        const fcstWeather = getWeatherStatus(fcst.SKY, fcst.PTY);
        const fcstTemp = fcst.T1H;
        const fcstRain = fcst.RN1;

        const card = document.createElement("div");
        card.className = "forecast-card";

        // 시간 포맷 변경 (예: "1800" -> "오후 6시")
        const timeStr = formatTimeLabel(fcst.time);

        // 강수량이 있으면 표기
        const rainBadge = fcstRain !== "강수없음" 
            ? `<span class="forecast-rain-prob"><i data-lucide="droplet" style="width: 10px; height: 10px;"></i> ${fcstRain}</span>`
            : "";

        card.innerHTML = `
            <span class="forecast-time">${timeStr}</span>
            <span class="forecast-icon">${fcstWeather.icon}</span>
            <span class="forecast-temp">${fcstTemp}°C</span>
            ${rainBadge}
        `;

        timeline.appendChild(card);
    });

    // Lucide 아이콘 초기화 (강수 뱃지 아이콘 렌더링용)
    lucide.createIcons();
}

/**
 * 기상청 날씨 상태 코드 조합 (SKY: 하늘상태, PTY: 강수형태)
 */
function getWeatherStatus(sky, pty) {
    // 1. 강수 상태(PTY) 우선 판별
    const ptyCode = String(pty);
    if (ptyCode === "1") {
        return { text: "비", icon: "🌧️" };
    } else if (ptyCode === "2") {
        return { text: "비 또는 눈", icon: "🌨️" };
    } else if (ptyCode === "3") {
        return { text: "눈", icon: "❄️" };
    } else if (ptyCode === "4") {
        return { text: "소나기", icon: "🌦️" };
    }

    // 2. 강수 형태가 없을 때 하늘 상태(SKY) 판별
    const skyCode = String(sky);
    if (skyCode === "1") {
        return { text: "맑음", icon: "☀️" };
    } else if (skyCode === "3") {
        return { text: "구름많음", icon: "⛅" };
    } else if (skyCode === "4") {
        return { text: "흐림", icon: "☁️" };
    }

    return { text: "정보 없음", icon: "❓" };
}

/**
 * 24시간 표기법 시간 변환 (예: "1800" -> "오후 6시", "0900" -> "오전 9시")
 */
function formatTimeLabel(timeStr) {
    if (!timeStr || timeStr.length < 2) return "";
    const hour = parseInt(timeStr.substring(0, 2), 10);
    const ampm = hour < 12 ? "오전" : "오후";
    const displayHour = hour === 0 ? 12 : (hour > 12 ? hour - 12 : hour);
    return `${ampm} ${displayHour}시`;
}
