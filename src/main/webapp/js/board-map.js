(function () {
    const DEFAULT_LAT = 37.5665;
    const DEFAULT_LNG = 126.9780;

    function ready(callback) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", callback);
        } else {
            callback();
        }
    }

    function hasKakaoPlaces() {
        return window.kakao && kakao.maps && kakao.maps.services;
    }

    function initFormMap() {
        const section = document.querySelector("[data-map-mode='form']");
        if (!section) {
            return;
        }

        const toggleBtn = document.getElementById("mapToggleBtn");
        const mapPanel = document.getElementById("mapPanel");
        const keywordInput = document.getElementById("keyword");
        const searchBtn = document.getElementById("placeSearchBtn");
        const listEl = document.getElementById("placesList");
        const mapEl = document.getElementById("map");
        const latInput = document.getElementById("lat");
        const lngInput = document.getElementById("lng");

        let map;
        let marker;
        let placesService;
        let infowindow;
        let resultMarkers = [];

        function isOpen() {
            return !mapPanel.classList.contains("hidden");
        }

        function updateToggleText() {
            toggleBtn.textContent = isOpen() ? toggleBtn.dataset.openText : toggleBtn.dataset.closedText;
        }

        function showMapUi(enabled) {
            mapPanel.classList.toggle("hidden", !enabled);
            updateToggleText();
        }

        function createMap() {
            if (!hasKakaoPlaces()) {
                console.log("[BoardMap] Kakao map failed to load. Check JavaScript key and Web platform domain.");
                return false;
            }
            if (map) {
                return true;
            }

            const hasPosition = latInput.value && lngInput.value;
            const center = hasPosition
                    ? new kakao.maps.LatLng(Number(latInput.value), Number(lngInput.value))
                    : new kakao.maps.LatLng(DEFAULT_LAT, DEFAULT_LNG);

            map = new kakao.maps.Map(mapEl, { center: center, level: 4 });
            marker = new kakao.maps.Marker({ position: center });
            placesService = new kakao.maps.services.Places();
            infowindow = new kakao.maps.InfoWindow({ zIndex: 1 });

            if (hasPosition) {
                marker.setMap(map);
            }
            return true;
        }

        function clearResults() {
            listEl.innerHTML = "";
            resultMarkers.forEach(function (resultMarker) {
                resultMarker.setMap(null);
            });
            resultMarkers = [];
            if (infowindow) {
                infowindow.close();
            }
        }

        function resetSelection() {
            latInput.value = "";
            lngInput.value = "";
            clearResults();
            if (marker) {
                marker.setMap(null);
            }
        }

        function showInfo(resultMarker, title) {
            infowindow.setContent('<div class="map-info-window">' + title + '</div>');
            infowindow.open(map, resultMarker);
        }

        function selectPlace(place, resultMarker) {
            const lat = place.y;
            const lng = place.x;
            const position = new kakao.maps.LatLng(Number(lat), Number(lng));

            latInput.value = lat;
            lngInput.value = lng;
            marker.setMap(map);
            marker.setPosition(position);
            map.setCenter(position);
            map.setLevel(3);
            showInfo(resultMarker || marker, place.place_name);
            console.log("[BoardMap] selected place=" + place.place_name + ", lat=" + lat + ", lng=" + lng);
        }

        function addResult(place) {
            const position = new kakao.maps.LatLng(place.y, place.x);
            const resultMarker = new kakao.maps.Marker({
                map: map,
                position: position
            });
            resultMarkers.push(resultMarker);

            const item = document.createElement("li");
            item.className = "place-result-item";

            const title = document.createElement("strong");
            title.textContent = place.place_name;

            const address = document.createElement("span");
            address.textContent = place.road_address_name || place.address_name || "";

            item.appendChild(title);
            item.appendChild(address);
            item.addEventListener("click", function () {
                selectPlace(place, resultMarker);
            });

            kakao.maps.event.addListener(resultMarker, "click", function () {
                selectPlace(place, resultMarker);
            });

            listEl.appendChild(item);
            return position;
        }

        function renderResults(data) {
            const bounds = new kakao.maps.LatLngBounds();
            clearResults();

            data.slice(0, 8).forEach(function (place) {
                bounds.extend(addResult(place));
            });

            if (data.length > 0) {
                map.setBounds(bounds);
            }
        }

        function searchPlaces() {
            const keyword = keywordInput.value.trim();
            if (!keyword) {
                keywordInput.focus();
                return;
            }
            if (!createMap()) {
                return;
            }

            placesService.keywordSearch(keyword, function (data, status) {
                if (status === kakao.maps.services.Status.OK) {
                    renderResults(data);
                    console.log("[BoardMap] search success. count=" + data.length);
                } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
                    clearResults();
                    console.log("[BoardMap] no search results.");
                } else {
                    clearResults();
                    console.log("[BoardMap] search error. status=" + status);
                }
            });
        }

        updateToggleText();
        if (isOpen()) {
            createMap();
        }

        toggleBtn.addEventListener("click", function () {
            const enabled = !isOpen();
            showMapUi(enabled);

            if (!enabled) {
                resetSelection();
                return;
            }

            if (createMap()) {
                setTimeout(function () {
                    map.relayout();
                }, 0);
            }
        });

        searchBtn.addEventListener("click", searchPlaces);
        keywordInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                searchPlaces();
            }
        });
    }

    function initDetailMap() {
        const section = document.querySelector("[data-map-mode='detail']");
        const mapEl = document.getElementById("map");
        if (!section || !mapEl || !window.kakao || !kakao.maps) {
            return;
        }

        const lat = Number(section.dataset.lat);
        const lng = Number(section.dataset.lng);
        if (Number.isNaN(lat) || Number.isNaN(lng)) {
            return;
        }

        const position = new kakao.maps.LatLng(lat, lng);
        const map = new kakao.maps.Map(mapEl, { center: position, level: 4 });
        new kakao.maps.Marker({ map: map, position: position });
    }

    ready(function () {
        initFormMap();
        initDetailMap();
    });
})();
