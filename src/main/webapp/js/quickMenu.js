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
    var path = getContextPath() + "/Main.emp";
    location.href = path;
}

function onBoardClick() {
    var path = getContextPath() + "/BoardList.do";
    location.href = path;
}

function onWeatherClick() {
    var path = getContextPath() + "/Weather.do";
    location.href = path;
}

function onPathClick() {
    var path = getContextPath() + "/Subway.do";
    location.href = path;
}

function getContextPath() {
    var wrapper = document.getElementById("quickMenuWrapper");
    return wrapper ? wrapper.dataset.contextPath || "" : "";
}
