/**
 * 공통 퀵메뉴 (토글 플로팅 버튼) 자바스크립트 로직
 */
document.addEventListener("DOMContentLoaded", function() {
    var wrapper = document.getElementById("quickMenuWrapper");
    var trigger = document.getElementById("quickMenuTrigger");

    if (wrapper && trigger) {
        // 메인 토글 버튼 클릭 이벤트
        trigger.addEventListener("click", function(e) {
            e.stopPropagation();
            wrapper.classList.toggle("active");
        });

        // 메뉴 영역 바깥 클릭 시 서브메뉴 닫기
        document.addEventListener("click", function(e) {
            if (!wrapper.contains(e.target)) {
                wrapper.classList.remove("active");
            }
        });
    }
});

/**
 * 오늘의 날씨 클릭 이벤트 핸들러
 */
function onWeatherClick() {
    alert("오늘의 날씨 조회 기능 준비 중...");
}

/**
 * 최단경로 조회 클릭 이벤트 핸들러
 */
function onPathClick() {
    alert("최단경로 조회 기능 준비 중...");
}
