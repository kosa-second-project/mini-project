// Lucide Icons Render
        lucide.createIcons();

        // Autocomplete suggestions binding
        setupAutocomplete("dptreInput", "dptreSuggestions");
        setupAutocomplete("arvlInput", "arvlSuggestions");

        function setupAutocomplete(inputId, suggestionsId) {
            const input = document.getElementById(inputId);
            const suggestions = document.getElementById(suggestionsId);

            if (!input || !suggestions) return;

            input.addEventListener("input", () => {
                const query = input.value.trim();
                suggestions.innerHTML = "";

                if (!query) {
                    suggestions.style.display = "none";
                    return;
                }

                const filtered = stationDatabase.filter(stn => 
                    stn.name.includes(query)
                );

                if (filtered.length === 0) {
                    suggestions.style.display = "none";
                    return;
                }

                filtered.forEach(stn => {
                    const div = document.createElement("div");
                    div.className = "suggestion-item";
                    div.innerHTML = `
                        <span>${stn.name}</span>
                        <span class="suggestion-line" style="background-color: ${stn.color}">${stn.line}</span>
                    `;
                    div.addEventListener("click", () => {
                        input.value = stn.name;
                        suggestions.style.display = "none";
                    });
                    suggestions.appendChild(div);
                });

                suggestions.style.display = "block";
            });

            // Close suggestions dropdown when clicking outside
            document.addEventListener("click", (e) => {
                if (e.target !== input && e.target !== suggestions) {
                    suggestions.style.display = "none";
                }
            });
        }

        // Toggle Settings Panel (안전 검사 추가)
        const settingsToggle = document.getElementById("settingsToggle");
        const settingsContent = document.getElementById("settingsContent");
        const settingsChevron = document.getElementById("settingsChevron");

        if (settingsToggle && settingsContent && settingsChevron) {
            settingsToggle.addEventListener("click", () => {
                const isVisible = settingsContent.style.display === "flex";
                if (isVisible) {
                    settingsContent.style.display = "none";
                    settingsChevron.style.transform = "rotate(0deg)";
                } else {
                    settingsContent.style.display = "flex";
                    settingsChevron.style.transform = "rotate(180deg)";
                }
            });
        }

        // Swap departure and arrival
        document.getElementById("btnSwap").addEventListener("click", () => {
            const dptre = document.getElementById("dptreInput");
            const arvl = document.getElementById("arvlInput");
            const temp = dptre.value;
            dptre.value = arvl.value;
            arvl.value = temp;
        });

        // Quick Selector Helper
        function quickSelect(dptre, arvl) {
            document.getElementById("dptreInput").value = dptre;
            document.getElementById("arvlInput").value = arvl;
            document.getElementById("btnSearch").click();
        }

        // Search Action
        document.getElementById("btnSearch").addEventListener("click", async () => {
            const dptre = document.getElementById("dptreInput").value.trim();
            const arvl = document.getElementById("arvlInput").value.trim();

            if (!dptre || !arvl) {
                alert("출발역과 도착역을 모두 입력해주세요.");
                return;
            }

            // 버튼 상태를 검색 중으로 변경 (HTML 자체를 교체하여 오작동 방지)
            const btn = document.getElementById("btnSearch");
            btn.innerHTML = `<i data-lucide="loader-2" class="spin"></i> <span>경로 검색 중...</span>`;
            lucide.createIcons();

            const apiKey = document.getElementById("apiKeyInput").value.trim();

            try {
                const routeData = await fetchSubwayPath(apiKey, dptre, arvl);
                displayResult(routeData);
            } catch (e) {
                console.error("API Fetch Failed:", e);
                alert("실시간 최단경로 탐색에 실패했습니다:\n" + e.message);
            } finally {
                // 버튼 상태 복원 (아이콘 회전 제거)
                btn.innerHTML = `<i data-lucide="route"></i> <span>경로 탐색 시작</span>`;
                lucide.createIcons();
            }
        });

        // Real API Fetch Logic with CORS proxy backup
        async function fetchSubwayPath(key, dptre, arvl) {
            const baseUrl = `https://apis.data.go.kr/B553766/path2/getShtrmPath2`;
            
            // Generate current date-time in 'yyyy-MM-dd HH:mm:ss' format
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            const seconds = String(now.getSeconds()).padStart(2, '0');
            const searchDt = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;

            // Build URL manually to prevent double-encoding of ServiceKey by URLSearchParams
            const targetUrl = `${baseUrl}?ServiceKey=${key}&dataType=JSON&stationValueType=name&dptreStn=${encodeURIComponent(dptre)}&arvlStn=${encodeURIComponent(arvl)}&searchDt=${encodeURIComponent(searchDt)}`;
            
            // Try direct fetch first
            try {
                const response = await fetch(targetUrl);
                if (response.ok) {
                    const data = await response.json();
                    if (isValidResponse(data)) return parseApiResponse(data);
                }
            } catch (err) {
                console.log("Direct API Fetch failed with CORS or Network issue. Trying CORS Proxy...");
            }

            // CORS Proxy Fallback (allorigins)
            const proxyUrl = `https://api.allorigins.win/raw?url=${encodeURIComponent(targetUrl)}`;
            const responseProxy = await fetch(proxyUrl);
            if (!responseProxy.ok) throw new Error("CORS Proxy requests failed as well.");
            
            const dataProxy = await responseProxy.json();
            if (isValidResponse(dataProxy)) {
                return parseApiResponse(dataProxy);
            } else {
                throw new Error("Invalid API Response or Unauthorized service key.");
            }
        }

        // Validate API response
        function isValidResponse(data) {
            const header = data.header || (data.response && data.response.header);
            return header && (header.resultCode === "00" || header.resultCode === "0");
        }

        // Parse API Response robustly
        function parseApiResponse(data) {
            const body = data.body || (data.response && data.response.body);
            if (!body) {
                throw new Error("No body returned in API response.");
            }

            // Map variables dynamically based on potential casing variations
            const totalTime = body.totalReqHr || body.totalreqHr || body.totalMovTime || 0;
            const totalTrsfCnt = body.trsitNmtm !== undefined ? body.trsitNmtm : (body.totalTrsfCnt || body.transferCount || 0);
            const totalMovDstnc = body.totalDstc || body.totalMovDstnc || body.totalDistance || 0;

            const paths = body.paths || body.items || body.shtrmPath || [];
            if (paths.length === 0) {
                throw new Error("No path segments returned.");
            }

            const pathList = [];
            // Parse segment list (paths is an array of segments)
            if (paths[0] && paths[0].dptreStn) {
                // It's the new paths segment structure
                const firstSegment = paths[0];
                pathList.push({
                    name: firstSegment.dptreStn.stnNm,
                    line: firstSegment.dptreStn.lineNm,
                    color: getLineInfo(firstSegment.dptreStn.lineNm).color,
                    isTransfer: false
                });

                paths.forEach((segment, idx) => {
                    const lineStyle = getLineInfo(segment.arvlStn.lineNm);
                    const isTransfer = segment.trsitYn === "Y";
                    pathList.push({
                        name: segment.arvlStn.stnNm,
                        line: segment.arvlStn.lineNm,
                        color: lineStyle.color,
                        isTransfer: isTransfer,
                        transferLine: isTransfer ? (paths[idx + 1] ? paths[idx + 1].dptreStn.lineNm : "") : ""
                    });
                });
            } else {
                // Fallback to legacy flat list structure if needed
                paths.forEach(stn => {
                    const stnName = stn.dptreStnNm || stn.stationName || stn.name || "";
                    const rawLine = stn.subwayLine || stn.line || "";
                    const lineStyle = getLineInfo(rawLine);
                    pathList.push({
                        name: stnName,
                        line: lineStyle.name,
                        color: lineStyle.color,
                        isTransfer: stn.isTransfer || (stn.trsfSubwayLine ? true : false),
                        transferLine: stn.trsfSubwayLine || ""
                    });
                });
            }

            return {
                dptre: pathList[0]?.name || "",
                arvl: pathList[pathList.length - 1]?.name || "",
                totalTimeMin: Math.round(totalTime / 60) || 10,
                stationCount: pathList.length,
                transferCount: totalTrsfCnt,
                distanceKm: (totalMovDstnc / 1000).toFixed(1),
                path: pathList
            };
        }

        // Return details for styling subway lines
        function getLineInfo(lineStr) {
            const cleanLine = String(lineStr).replace("호선", "").trim();
            const colors = {
                "1": { name: "1호선", color: "var(--line-1)" },
                "2": { name: "2호선", color: "var(--line-2)" },
                "3": { name: "3호선", color: "var(--line-3)" },
                "4": { name: "4호선", color: "var(--line-4)" },
                "5": { name: "5호선", color: "var(--line-5)" },
                "6": { name: "6호선", color: "var(--line-6)" },
                "7": { name: "7호선", color: "var(--line-7)" },
                "8": { name: "8호선", color: "var(--line-8)" },
                "9": { name: "9호선", color: "var(--line-9)" }
            };
            return colors[cleanLine] || { name: cleanLine + "호선", color: "var(--line-default)" };
        }

        // Display results in Result Card
        function displayResult(data) {
            // Hide placeholder card, show result card
            document.getElementById("placeholderCard").style.display = "none";
            const resultCard = document.getElementById("resultCard");
            resultCard.style.display = "flex";

            // Stats
            document.getElementById("statTime").textContent = `${data.totalTimeMin}분`;
            document.getElementById("statStations").textContent = `${data.stationCount}개`;
            document.getElementById("statTransfers").textContent = `${data.transferCount}회`;
            document.getElementById("statDistance").textContent = `${data.distanceKm}km`;

            // Node Animation Text
            document.getElementById("animStartName").textContent = data.dptre;
            document.getElementById("animEndName").textContent = data.arvl;

            // Subway Animation Trigger
            const train = document.getElementById("animTrain");
            // Reset position instantly
            train.style.transition = "none";
            train.style.left = "5%";
            
            // Force redraw/reflow
            void train.offsetWidth;

            // Trigger smooth slide to destination node
            train.style.transition = "left 3s cubic-bezier(0.25, 1, 0.5, 1)";
            train.style.left = "calc(95% - 48px)";

            // Timeline rendering
            const timeline = document.getElementById("routeTimeline");
            timeline.innerHTML = "";

            data.path.forEach((stn, idx) => {
                const div = document.createElement("div");
                div.className = "timeline-item";
                
                // Set bullet color according to subway line
                const bullet = document.createElement("div");
                bullet.className = `timeline-bullet ${stn.isTransfer ? 'transfer' : ''}`;
                bullet.style.borderColor = stn.color;
                
                if (stn.isTransfer) {
                    bullet.style.boxShadow = `0 0 10px ${stn.color}`;
                }
                div.appendChild(bullet);

                // Station name layout
                const nameDiv = document.createElement("div");
                nameDiv.className = "timeline-station-name";
                nameDiv.innerHTML = `
                    <span>${stn.name}</span>
                    <span class="timeline-line-badge" style="background-color: ${stn.color}">${stn.line}</span>
                `;
                div.appendChild(nameDiv);

                // Transfer info if applicable
                if (stn.isTransfer) {
                    const transInfo = document.createElement("div");
                    transInfo.className = "timeline-transfer-info";
                    transInfo.innerHTML = `
                        <i data-lucide="shuffle" style="width: 14px; height: 14px;"></i>
                        <span>${stn.transferLine || '다른 호선'}으로 환승</span>
                    `;
                    div.appendChild(transInfo);
                }

                // Append to timeline container
                timeline.appendChild(div);
            });

            // Initialize icons inside new timeline items
            lucide.createIcons();

            // Smooth scroll results into view
            resultCard.scrollIntoView({ behavior: "smooth" });
        }