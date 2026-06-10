(function() {
    var page = document.querySelector(".board-list-page");
    if (!page) {
        return;
    }

    var contextPath = page.dataset.contextPath || "";
    var statusEl = document.getElementById("boardListStatus");
    var pageSizeSelect = document.getElementById("ps");
    var pageSizeForm = document.getElementById("boardPageSizeForm");
    var tbody = document.getElementById("boardListBody");
    var pagination = document.querySelector(".board-pagination");
    var writeLink = document.querySelector(".board-write-link");

    if (!pagination) {
        pagination = document.createElement("nav");
        pagination.className = "board-pagination";
        pagination.setAttribute("aria-label", "게시글 페이지");
        page.appendChild(pagination);
    }

    function escapeHtml(value) {
        return String(value == null ? "" : value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function boardListUrl(cp, ps) {
        return "BoardList.do?cp=" + encodeURIComponent(cp) + "&ps=" + encodeURIComponent(ps);
    }

    function writerLabel(board) {
        if (board.deleted) {
            return "-";
        }
        var name = board.ename || board.empno;
        return board.deptname ? name + " (" + board.deptname + ")" : name;
    }

    function subjectCell(board) {
        var depth = Number(board.depth || 0);
        var indent = '<span class="board-reply-indent" style="--reply-depth:' + depth + '"></span>';
        var marker = depth > 0 ? '<span class="board-reply-marker">↳</span>' : "";
        if (board.deleted) {
            return indent + marker + '<span class="board-deleted-title">삭제된 게시판입니다.</span>';
        }
        return indent + marker
            + '<a href="BoardDetail.do?idx=' + encodeURIComponent(board.idx) + '" class="board-title-link">'
            + escapeHtml(board.subject)
            + '</a>'
            + (Number(board.replyCount || 0) > 0 ? ' <span class="board-reply-count">[' + escapeHtml(board.replyCount) + ']</span>' : '');
    }

    function renderRows(boardList) {
        if (!boardList || boardList.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-muted py-4">등록된 게시글이 없습니다.</td></tr>';
            return;
        }

        tbody.innerHTML = boardList.map(function(board) {
            return '<tr>'
                + '<td>' + escapeHtml(board.idx) + '</td>'
                + '<td class="text-start">'
                + subjectCell(board)
                + '</td>'
                + '<td>' + escapeHtml(writerLabel(board)) + '</td>'
                + '<td>' + escapeHtml(board.writedate) + '</td>'
                + '<td>' + escapeHtml(board.readnum) + '</td>'
                + '</tr>';
        }).join("");
    }

    function pageItem(label, cp, ps, disabled, active, ariaLabel) {
        var classes = "page-item" + (disabled ? " disabled" : "") + (active ? " active" : "");
        var href = disabled ? "#" : boardListUrl(cp, ps);
        return '<li class="' + classes + '">'
            + '<a class="page-link" href="' + href + '"' + (ariaLabel ? ' aria-label="' + ariaLabel + '"' : "") + ' data-cp="' + cp + '">'
            + label
            + '</a>'
            + '</li>';
    }

    function renderPagination(data) {
        if (!data.pagecount || data.pagecount < 1) {
            pagination.innerHTML = "";
            return;
        }

        var html = '<ul class="pagination justify-content-center">';
        html += pageItem('<i class="bi bi-chevron-double-left"></i>', 1, data.pagesize, data.cpage === 1, false, "처음");
        html += pageItem('<i class="bi bi-chevron-left"></i>', data.cpage - 1, data.pagesize, data.cpage === 1, false, "이전");

        for (var i = data.startPage; i <= data.endPage; i++) {
            html += pageItem(i, i, data.pagesize, false, i === data.cpage, "");
        }

        html += pageItem('<i class="bi bi-chevron-right"></i>', data.cpage + 1, data.pagesize, data.cpage === data.pagecount, false, "다음");
        html += pageItem('<i class="bi bi-chevron-double-right"></i>', data.pagecount, data.pagesize, data.cpage === data.pagecount, false, "마지막");
        html += '</ul>';
        pagination.innerHTML = html;
    }

    function renderBoardList(data) {
        statusEl.textContent = "전체 " + data.totalboardcount + "건 / 현재 " + data.cpage + "페이지";
        pageSizeSelect.value = String(data.pagesize);
        renderRows(data.boardList);
        renderPagination(data);
        history.replaceState(null, "", boardListUrl(data.cpage, data.pagesize));
    }

    function loadBoardList(cp, ps) {
        var url = contextPath + "/BoardListAjax.do?cp=" + encodeURIComponent(cp) + "&ps=" + encodeURIComponent(ps);
        return fetch(url, { headers: { "Accept": "application/json" } })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error("게시글 목록 요청 실패");
                }
                return response.json();
            })
            .then(function(data) {
                if (!data.success) {
                    throw new Error(data.message || "게시글 목록 요청 실패");
                }
                renderBoardList(data);
            });
    }

    pageSizeForm.addEventListener("submit", function(event) {
        event.preventDefault();
        loadBoardList(1, pageSizeSelect.value).catch(function() {
            pageSizeForm.submit();
        });
    });

    pageSizeSelect.addEventListener("change", function() {
        loadBoardList(1, pageSizeSelect.value).catch(function() {
            pageSizeForm.submit();
        });
    });

    if (writeLink) {
        writeLink.addEventListener("click", function(event) {
            if (page.dataset.loggedIn === "true") {
                return;
            }
            event.preventDefault();
            alert(page.dataset.loginMessage || "로그인한 사용자만 글을 작성할 수 있습니다.");
            window.location.href = page.dataset.loginUrl || (contextPath + "/Login.emp");
        });
    }

    pagination.addEventListener("click", function(event) {
        var link = event.target.closest("a");
        if (!link || link.parentElement.classList.contains("disabled") || link.parentElement.classList.contains("active")) {
            return;
        }

        var targetUrl = new URL(link.href, window.location.href);
        var cp = link.dataset.cp || targetUrl.searchParams.get("cp");
        if (!cp) {
            return;
        }

        event.preventDefault();
        loadBoardList(cp, pageSizeSelect.value).catch(function() {
            window.location.href = link.href;
        });
    });
})();
