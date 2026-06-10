(function () {
    var DEFAULT_LAT = 37.5665;
    var DEFAULT_LNG = 126.9780;
    var MAP_BLOCK_CLASS = "content-map";

    function ready(callback) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", callback);
        } else {
            callback();
        }
    }

    function hasKakaoMap() {
        return window.kakao && kakao.maps;
    }

    function hasKakaoPlaces() {
        return hasKakaoMap() && kakao.maps.services;
    }

    function escapeHtml(value) {
        return String(value || "")
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#39;");
    }

    function isEditorHtml(value) {
        return /<([a-z][\w:-]*)(\s|>)/i.test(value || "");
    }

    function textToHtml(value) {
        var text = String(value || "").replace(/\r\n/g, "\n");
        if (!text.trim()) {
            return "<p><br></p>";
        }
        return text.split(/\n{2,}/).map(function (block) {
            return "<p>" + escapeHtml(block).replace(/\n/g, "<br>") + "</p>";
        }).join("");
    }

    function createMapBlockHtml(lat, lng, placeName) {
        var label = escapeHtml(placeName || "선택한 위치");
        return [
            '<div class="' + MAP_BLOCK_CLASS + '" contenteditable="false" data-lat="' + escapeHtml(lat) + '" data-lng="' + escapeHtml(lng) + '" data-place-name="' + label + '">',
            '  <div class="content-map-header">',
            '    <strong>' + label + '</strong>',
            '    <button type="button" class="content-map-remove" aria-label="지도 삭제">삭제</button>',
            '  </div>',
            '  <div class="content-map-canvas"></div>',
            '</div>',
            '<p><br></p>'
        ].join("");
    }

    function sanitizeEditorHtml(value) {
        var template = document.createElement("template");
        template.innerHTML = value || "";
        var output = document.createElement("div");

        function appendInlineText(source, target) {
            source.childNodes.forEach(function (node) {
                if (node.nodeType === Node.TEXT_NODE) {
                    target.appendChild(document.createTextNode(node.textContent));
                } else if (node.nodeType === Node.ELEMENT_NODE && node.tagName === "BR") {
                    target.appendChild(document.createElement("br"));
                } else if (node.nodeType === Node.ELEMENT_NODE) {
                    appendInlineText(node, target);
                }
            });
        }

        template.content.childNodes.forEach(function (node) {
            var paragraph;
            var lat;
            var lng;

            if (node.nodeType === Node.TEXT_NODE && node.textContent.trim()) {
                paragraph = document.createElement("p");
                paragraph.textContent = node.textContent;
                output.appendChild(paragraph);
                return;
            }
            if (node.nodeType !== Node.ELEMENT_NODE) {
                return;
            }

            if (node.classList.contains(MAP_BLOCK_CLASS)) {
                lat = Number(node.dataset.lat);
                lng = Number(node.dataset.lng);
                if (!Number.isNaN(lat) && !Number.isNaN(lng)) {
                    output.insertAdjacentHTML("beforeend", createMapBlockHtml(String(lat), String(lng), node.dataset.placeName || "선택한 위치"));
                }
                return;
            }

            paragraph = document.createElement("p");
            appendInlineText(node, paragraph);
            output.appendChild(paragraph);
        });

        return output.innerHTML || "<p><br></p>";
    }

    function ensureTrailingParagraph(editor) {
        var last = editor.lastElementChild;
        if (!last || last.classList.contains(MAP_BLOCK_CLASS)) {
            editor.insertAdjacentHTML("beforeend", "<p><br></p>");
        }
    }

    function placeCaretAtEnd(element) {
        var range = document.createRange();
        var selection = window.getSelection();
        range.selectNodeContents(element);
        range.collapse(false);
        selection.removeAllRanges();
        selection.addRange(range);
    }

    function renderMapBlock(block) {
        var canvas = block.querySelector(".content-map-canvas");
        var lat;
        var lng;
        var position;
        var map;

        if (!canvas || canvas.dataset.rendered === "true" || !hasKakaoMap()) {
            return;
        }

        lat = Number(block.dataset.lat);
        lng = Number(block.dataset.lng);
        if (Number.isNaN(lat) || Number.isNaN(lng)) {
            return;
        }

        position = new kakao.maps.LatLng(lat, lng);
        map = new kakao.maps.Map(canvas, { center: position, level: 4 });
        new kakao.maps.Marker({ map: map, position: position });
        canvas.dataset.rendered = "true";
        setTimeout(function () {
            map.relayout();
            map.setCenter(position);
        }, 0);
    }

    function renderContentMaps(root) {
        root.querySelectorAll("." + MAP_BLOCK_CLASS).forEach(renderMapBlock);
    }

    function serializeEditorContent(editor) {
        var clone = editor.cloneNode(true);
        clone.querySelectorAll("." + MAP_BLOCK_CLASS).forEach(function (block) {
            var lat = block.dataset.lat;
            var lng = block.dataset.lng;
            var title = block.querySelector(".content-map-header strong");
            var placeName = block.dataset.placeName || (title ? title.textContent : "") || "선택한 위치";
            block.outerHTML = createMapBlockHtml(lat, lng, placeName);
        });
        return sanitizeEditorHtml(clone.innerHTML).trim();
    }

    function initDetailContent() {
        var content = document.querySelector("[data-editor-mode='detail']");
        var source = document.getElementById("boardContentSource");
        var raw;

        if (!content) {
            return;
        }

        raw = source ? source.value.trim() : content.textContent.trim();
        content.innerHTML = isEditorHtml(raw) ? sanitizeEditorHtml(raw) : textToHtml(raw);
        renderContentMaps(content);
    }

    function initFormEditor() {
        var form = document.querySelector(".board-form");
        var editor = document.getElementById("contentEditor");
        var source = document.getElementById("contentInput");
        var insertMapBtn = document.getElementById("insertMapBtn");
        var mapPanel = document.getElementById("mapPanel");
        var keywordInput = document.getElementById("keyword");
        var searchBtn = document.getElementById("placeSearchBtn");
        var listEl = document.getElementById("placesList");
        var mapEl = document.getElementById("map");
        var savedRange = null;
        var map;
        var marker;
        var placesService;
        var infowindow;
        var resultMarkers = [];

        if (!form || !editor || !source) {
            return;
        }

        editor.innerHTML = isEditorHtml(source.value) ? sanitizeEditorHtml(source.value) : textToHtml(source.value);
        ensureTrailingParagraph(editor);
        renderContentMaps(editor);

        function saveSelection() {
            var selection = window.getSelection();
            var range;
            if (!selection || selection.rangeCount === 0) {
                return;
            }
            range = selection.getRangeAt(0);
            if (editor.contains(range.commonAncestorContainer)) {
                savedRange = range.cloneRange();
            }
        }

        function restoreSelection() {
            var selection;
            editor.focus();
            selection = window.getSelection();
            selection.removeAllRanges();
            if (savedRange) {
                selection.addRange(savedRange);
            } else {
                placeCaretAtEnd(editor);
            }
        }

        function clearResults() {
            if (listEl) {
                listEl.innerHTML = "";
            }
            resultMarkers.forEach(function (resultMarker) {
                resultMarker.setMap(null);
            });
            resultMarkers = [];
            if (infowindow) {
                infowindow.close();
            }
        }

        function createSearchMap() {
            var center;
            if (!hasKakaoPlaces()) {
                if (listEl) {
                    listEl.innerHTML = '<li class="place-result-message">지도 서비스를 불러올 수 없습니다.</li>';
                }
                return false;
            }
            if (map) {
                return true;
            }

            center = new kakao.maps.LatLng(DEFAULT_LAT, DEFAULT_LNG);
            map = new kakao.maps.Map(mapEl, { center: center, level: 4 });
            marker = new kakao.maps.Marker({ position: center });
            placesService = new kakao.maps.services.Places();
            infowindow = new kakao.maps.InfoWindow({ zIndex: 1 });
            return true;
        }

        function showInfo(resultMarker, title) {
            infowindow.setContent('<div class="map-info-window">' + escapeHtml(title) + '</div>');
            infowindow.open(map, resultMarker);
        }

        function syncContent() {
            ensureTrailingParagraph(editor);
            source.value = serializeEditorContent(editor);
        }

        function insertMapBlock(place) {
            restoreSelection();
            document.execCommand("insertHTML", false, createMapBlockHtml(place.y, place.x, place.place_name));
            ensureTrailingParagraph(editor);
            renderContentMaps(editor);
            mapPanel.classList.add("hidden");
            clearResults();
            syncContent();
            editor.focus();
        }

        function selectPlace(place, resultMarker) {
            var position = new kakao.maps.LatLng(Number(place.y), Number(place.x));
            marker.setMap(map);
            marker.setPosition(position);
            map.setCenter(position);
            map.setLevel(3);
            showInfo(resultMarker || marker, place.place_name);
            insertMapBlock(place);
        }

        function addResult(place) {
            var position = new kakao.maps.LatLng(place.y, place.x);
            var resultMarker = new kakao.maps.Marker({ map: map, position: position });
            var item = document.createElement("li");
            var title = document.createElement("strong");
            var address = document.createElement("span");

            resultMarkers.push(resultMarker);
            item.className = "place-result-item";
            title.textContent = place.place_name;
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
            var bounds = new kakao.maps.LatLngBounds();
            clearResults();
            data.slice(0, 8).forEach(function (place) {
                bounds.extend(addResult(place));
            });
            if (data.length > 0) {
                map.setBounds(bounds);
            }
        }

        function searchPlaces() {
            var keyword = keywordInput.value.trim();
            if (!keyword) {
                keywordInput.focus();
                return;
            }
            if (!createSearchMap()) {
                return;
            }

            placesService.keywordSearch(keyword, function (data, status) {
                if (status === kakao.maps.services.Status.OK) {
                    renderResults(data);
                } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
                    clearResults();
                    listEl.innerHTML = '<li class="place-result-message">검색 결과가 없습니다.</li>';
                } else {
                    clearResults();
                    listEl.innerHTML = '<li class="place-result-message">검색 중 오류가 발생했습니다.</li>';
                }
            });
        }

        editor.addEventListener("keyup", saveSelection);
        editor.addEventListener("mouseup", saveSelection);
        editor.addEventListener("input", syncContent);
        editor.addEventListener("click", function (event) {
            var block;
            if (event.target.classList.contains("content-map-remove")) {
                block = event.target.closest("." + MAP_BLOCK_CLASS);
                if (block) {
                    block.remove();
                    ensureTrailingParagraph(editor);
                    syncContent();
                }
            } else {
                saveSelection();
            }
        });

        if (insertMapBtn && mapPanel) {
            insertMapBtn.addEventListener("click", function () {
                saveSelection();
                mapPanel.classList.toggle("hidden");
                if (!mapPanel.classList.contains("hidden") && createSearchMap()) {
                    setTimeout(function () {
                        map.relayout();
                    }, 0);
                    keywordInput.focus();
                }
            });
        }

        if (searchBtn) {
            searchBtn.addEventListener("click", searchPlaces);
        }
        if (keywordInput) {
            keywordInput.addEventListener("keydown", function (event) {
                if (event.key === "Enter") {
                    event.preventDefault();
                    searchPlaces();
                }
            });
        }

        form.addEventListener("submit", syncContent);
        syncContent();
    }

    ready(function () {
        initFormEditor();
        initDetailContent();
    });
})();
