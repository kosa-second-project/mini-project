(function () {
    const DEFAULT_LAT = 37.5665;
    const DEFAULT_LNG = 126.9780;
    const MAP_BLOCK_CLASS = "content-map";

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
        const text = String(value || "").replace(/\r\n/g, "\n");
        if (!text.trim()) {
            return "<p><br></p>";
        }
        return text.split(/\n{2,}/).map(function (block) {
            return "<p>" + escapeHtml(block).replace(/\n/g, "<br>") + "</p>";
        }).join("");
    }

    function sanitizeEditorHtml(value) {
        const template = document.createElement("template");
        template.innerHTML = value || "";
        const output = document.createElement("div");

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
            if (node.nodeType === Node.TEXT_NODE && node.textContent.trim()) {
                const p = document.createElement("p");
                p.textContent = node.textContent;
                output.appendChild(p);
                return;
            }
            if (node.nodeType !== Node.ELEMENT_NODE) {
                return;
            }

            if (node.classList.contains(MAP_BLOCK_CLASS)) {
                const lat = Number(node.dataset.lat);
                const lng = Number(node.dataset.lng);
                if (!Number.isNaN(lat) && !Number.isNaN(lng)) {
                    output.insertAdjacentHTML("beforeend", createMapBlockHtml(String(lat), String(lng), node.dataset.placeName || "선택한 위치"));
                }
                return;
            }

            const p = document.createElement("p");
            appendInlineText(node, p);
            output.appendChild(p);
        });

        return output.innerHTML || "<p><br></p>";
    }

    function ensureTrailingParagraph(editor) {
        const last = editor.lastElementChild;
        if (!last || last.classList.contains(MAP_BLOCK_CLASS)) {
            editor.insertAdjacentHTML("beforeend", "<p><br></p>");
        }
    }

    function placeCaretAtEnd(element) {
        const range = document.createRange();
        const selection = window.getSelection();
        range.selectNodeContents(element);
        range.collapse(false);
        selection.removeAllRanges();
        selection.addRange(range);
    }

    function createMapBlockHtml(lat, lng, placeName) {
        const label = escapeHtml(placeName || "선택한 위치");
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

    function renderMapBlock(block) {
        const canvas = block.querySelector(".content-map-canvas");
        if (!canvas || canvas.dataset.rendered === "true" || !hasKakaoMap()) {
            return;
        }

        const lat = Number(block.dataset.lat);
        const lng = Number(block.dataset.lng);
        if (Number.isNaN(lat) || Number.isNaN(lng)) {
            return;
        }

        const position = new kakao.maps.LatLng(lat, lng);
        const map = new kakao.maps.Map(canvas, { center: position, level: 4 });
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
        const clone = editor.cloneNode(true);
        clone.querySelectorAll("." + MAP_BLOCK_CLASS).forEach(function (block) {
            const lat = block.dataset.lat;
            const lng = block.dataset.lng;
            const title = block.querySelector(".content-map-header strong");
            const placeName = block.dataset.placeName || (title ? title.textContent : "") || "선택한 위치";
            block.outerHTML = createMapBlockHtml(lat, lng, placeName);
        });
        return sanitizeEditorHtml(clone.innerHTML).trim();
    }

    function initDetailContent() {
        const content = document.querySelector("[data-editor-mode='detail']");
        const source = document.getElementById("boardContentSource");
        if (!content) {
            return;
        }

        const raw = source ? source.value.trim() : content.textContent.trim();
        if (!isEditorHtml(raw)) {
            content.innerHTML = textToHtml(raw);
        } else {
            content.innerHTML = sanitizeEditorHtml(raw);
        }
        renderContentMaps(content);
    }

    function initFormEditor() {
        const form = document.querySelector(".board-form");
        const editor = document.getElementById("contentEditor");
        const source = document.getElementById("contentInput");
        const insertMapBtn = document.getElementById("insertMapBtn");
        const mapPanel = document.getElementById("mapPanel");
        const keywordInput = document.getElementById("keyword");
        const searchBtn = document.getElementById("placeSearchBtn");
        const listEl = document.getElementById("placesList");
        const mapEl = document.getElementById("map");
        const latInput = document.getElementById("lat");
        const lngInput = document.getElementById("lng");

        if (!form || !editor || !source) {
            return;
        }

        let savedRange = null;
        let map;
        let marker;
        let placesService;
        let infowindow;
        let resultMarkers = [];

        editor.innerHTML = isEditorHtml(source.value) ? sanitizeEditorHtml(source.value) : textToHtml(source.value);
        ensureTrailingParagraph(editor);
        renderContentMaps(editor);

        function saveSelection() {
            const selection = window.getSelection();
            if (!selection || selection.rangeCount === 0) {
                return;
            }
            const range = selection.getRangeAt(0);
            if (editor.contains(range.commonAncestorContainer)) {
                savedRange = range.cloneRange();
            }
        }

        function restoreSelection() {
            editor.focus();
            const selection = window.getSelection();
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
            if (!hasKakaoPlaces()) {
                if (listEl) {
                    listEl.innerHTML = '<li class="place-result-message">지도 서비스를 불러올 수 없습니다.</li>';
                }
                return false;
            }
            if (map) {
                return true;
            }

            const center = new kakao.maps.LatLng(DEFAULT_LAT, DEFAULT_LNG);
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

        function insertMapBlock(place) {
            const lat = place.y;
            const lng = place.x;
            restoreSelection();
            document.execCommand("insertHTML", false, createMapBlockHtml(lat, lng, place.place_name));
            ensureTrailingParagraph(editor);
            renderContentMaps(editor);
            mapPanel.classList.add("hidden");
            clearResults();
            syncContent();
            editor.focus();
        }

        function selectPlace(place, resultMarker) {
            const position = new kakao.maps.LatLng(Number(place.y), Number(place.x));
            marker.setMap(map);
            marker.setPosition(position);
            map.setCenter(position);
            map.setLevel(3);
            showInfo(resultMarker || marker, place.place_name);
            insertMapBlock(place);
        }

        function addResult(place) {
            const position = new kakao.maps.LatLng(place.y, place.x);
            const resultMarker = new kakao.maps.Marker({ map: map, position: position });
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

        function syncContent() {
            ensureTrailingParagraph(editor);
            source.value = serializeEditorContent(editor);

            const firstMap = editor.querySelector("." + MAP_BLOCK_CLASS);
            latInput.value = firstMap ? firstMap.dataset.lat : "";
            lngInput.value = firstMap ? firstMap.dataset.lng : "";
        }

        editor.addEventListener("keyup", saveSelection);
        editor.addEventListener("mouseup", saveSelection);
        editor.addEventListener("input", syncContent);
        editor.addEventListener("click", function (event) {
            if (event.target.classList.contains("content-map-remove")) {
                const block = event.target.closest("." + MAP_BLOCK_CLASS);
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
