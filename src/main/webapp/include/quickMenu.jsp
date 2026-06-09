<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 
  공통 퀵메뉴 (토글 플로팅 버튼)
  어느 JSP 페이지든 아래 코드를 추가하면 화면 우측 하단에 플로팅 버튼이 렌더링됩니다:
  <jsp:include page="/include/quickMenu.jsp" />
-->
<style>
/* 다른 스타일과 충돌 방지를 위해 quick-menu-wrapper 하위로 스타일 스코프 제한 */
.quick-menu-wrapper {
    position: fixed;
    right: 30px;
    bottom: 30px;
    z-index: 9999;
    font-family: 'Malgun Gothic', '맑은 고딕', sans-serif;
}

/* 메인 토글 버튼 (원형 플러스 버튼) */
.quick-menu-trigger {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, #6366f1, #4f46e5); /* Sleek Indigo Gradient */
    color: #ffffff;
    border: none;
    outline: none;
    cursor: pointer;
    box-shadow: 0 10px 25px rgba(79, 70, 229, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 30px;
    transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    user-select: none;
}

.quick-menu-trigger:hover {
    transform: scale(1.08);
    box-shadow: 0 12px 28px rgba(79, 70, 229, 0.55);
}

.quick-menu-trigger:active {
    transform: scale(0.95);
}

/* 액티브 상태 (플러스가 엑스로 회전) */
.quick-menu-wrapper.active .quick-menu-trigger {
    transform: rotate(135deg);
    background: linear-gradient(135deg, #ef4444, #dc2626); /* Active Red Gradient */
    box-shadow: 0 10px 25px rgba(220, 38, 38, 0.4);
}

/* 서브 메뉴 리스트 (위로 슬라이딩하며 팝업) */
.quick-menu-list {
    position: absolute;
    bottom: 75px;
    right: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
    list-style: none;
    margin: 0;
    padding: 0;
    opacity: 0;
    transform: translateY(20px);
    pointer-events: none;
    transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
}

/* 활성화 상태의 서브 메뉴 리스트 */
.quick-menu-wrapper.active .quick-menu-list {
    opacity: 1;
    transform: translateY(0);
    pointer-events: auto;
}

/* 서브 메뉴 개별 버튼 (캡슐 디자인 + Glassmorphism) */
.quick-menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.25);
    padding: 10px 18px;
    border-radius: 30px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.2s ease;
    white-space: nowrap;
    text-decoration: none;
    color: #1f2937;
    font-size: 14px;
    font-weight: 600;
}

.quick-menu-item:hover {
    background: #ffffff;
    transform: translateX(-5px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
    color: #4f46e5;
}

.quick-menu-item:active {
    transform: translateX(-5px) scale(0.97);
}

/* 아이콘 스타일 */
.quick-menu-icon {
    font-size: 18px;
}
</style>

<div class="quick-menu-wrapper" id="quickMenuWrapper">
    <!-- 서브 메뉴 목록 -->
    <ul class="quick-menu-list">
        <li>
            <a href="javascript:void(0);" class="quick-menu-item" onclick="onWeatherClick()">
                <span class="quick-menu-icon">☀️</span>
                <span>오늘의 날씨</span>
            </a>
        </li>
        <li>
            <a href="javascript:void(0);" class="quick-menu-item" onclick="onPathClick()">
                <span class="quick-menu-icon">📍</span>
                <span>최단경로 조회</span>
            </a>
        </li>
    </ul>

    <!-- 메인 토글 플러스 버튼 -->
    <button type="button" class="quick-menu-trigger" id="quickMenuTrigger">
        +
    </button>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    var wrapper = document.getElementById("quickMenuWrapper");
    var trigger = document.getElementById("quickMenuTrigger");

    // 버튼 클릭 시 active 클래스 토글
    trigger.addEventListener("click", function(e) {
        e.stopPropagation();
        wrapper.classList.toggle("active");
    });

    // 외부 영역 클릭 시 서브메뉴 닫기
    document.addEventListener("click", function(e) {
        if (!wrapper.contains(e.target)) {
            wrapper.classList.remove("active");
        }
    });
});

// 날씨 버튼 클릭 이벤트 (임시/테스트용)
function onWeatherClick() {
    alert("오늘의 날씨 공공데이터 API 연동 페이지/기능 실행 준비 중...");
}

// 최단경로 버튼 클릭 이벤트 (임시/테스트용)
function onPathClick() {
    alert("최단경로 API 연동 페이지/기능 실행 준비 중...");
}
</script>
