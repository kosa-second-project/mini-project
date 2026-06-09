document.addEventListener("DOMContentLoaded", function() {
    var wrapper = document.getElementById("quickMenuWrapper");
    var trigger = document.getElementById("quickMenuTrigger");

    if (wrapper && trigger) {
        trigger.addEventListener("click", function(e) {
            e.stopPropagation();
            wrapper.classList.toggle("active");
        });

        document.addEventListener("click", function(e) {
            if (!wrapper.contains(e.target)) {
                wrapper.classList.remove("active");
            }
        });
    }
});

function onHomeClick() {
    var path = (window.contextPath || "") + "/Main.emp";
    location.href = path;
}

function onBoardClick() {
    var path = (window.contextPath || "") + "/BoardList.do";
    location.href = path;
}

function onWeatherClick() {
    var path = (window.contextPath || "") + "/Weather.do";
    location.href = path;
}

function onPathClick() {
    var path = (window.contextPath || "") + "/Subway.do";
    location.href = path;
}
