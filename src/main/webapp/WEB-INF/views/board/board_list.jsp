<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="kr.or.bit.dto.Board" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>&#44172;&#49884;&#44544; &#47785;&#47197;</title>
<link href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/folioone-theme.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/board.css" rel="stylesheet">
</head>
<body class="app-shell">
<%
    List<Board> boardList = (List<Board>) request.getAttribute("boardList");
    Integer totalboardcount = (Integer) request.getAttribute("totalboardcount");
    Integer cpage = (Integer) request.getAttribute("cpage");
    Integer pagesize = (Integer) request.getAttribute("pagesize");
    Integer pagecount = (Integer) request.getAttribute("pagecount");

    if (totalboardcount == null) {
        totalboardcount = 0;
    }
    if (cpage == null) {
        cpage = 1;
    }
    if (pagesize == null) {
        pagesize = 5;
    }
    if (pagecount == null) {
        pagecount = 0;
    }

    int pagerSize = 5;
    int startPage = ((cpage - 1) / pagerSize) * pagerSize + 1;
    int endPage = startPage + pagerSize - 1;
    if (endPage > pagecount) {
        endPage = pagecount;
    }
%>

<div class="container mt-5 board-list-page" data-context-path="${pageContext.request.contextPath}">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="mb-1">&#44172;&#49884;&#44544; &#47785;&#47197;</h2>
            <span class="text-muted" id="boardListStatus">&#51204;&#52404; <%= totalboardcount %>&#44148; / &#54788;&#51116; <%= cpage %>&#54168;&#51060;&#51648;</span>
        </div>
        <a href="BoardWriteForm.do" class="btn btn-primary">&#44544;&#50416;&#44592;</a>
    </div>

    <div class="d-flex justify-content-end mb-3">
        <form action="BoardList.do" method="get" class="d-flex align-items-center gap-2" id="boardPageSizeForm">
            <label for="ps" class="form-label mb-0">&#54168;&#51060;&#51648; &#53356;&#44592;</label>
            <select name="ps" id="ps" class="form-select form-select-sm w-auto">
                <option value="5" <%= pagesize == 5 ? "selected" : "" %>>5&#44060;</option>
                <option value="10" <%= pagesize == 10 ? "selected" : "" %>>10&#44060;</option>
                <option value="20" <%= pagesize == 20 ? "selected" : "" %>>20&#44060;</option>
            </select>
            <input type="hidden" name="cp" value="1">
        </form>
    </div>

    <div class="table-responsive">
        <table class="table table-hover table-bordered align-middle text-center">
            <thead class="table-light">
                <tr>
                    <th style="width: 90px;">&#48264;&#54840;</th>
                    <th>&#51228;&#47785;</th>
                    <th style="width: 120px;">&#49324;&#48264;</th>
                    <th style="width: 160px;">&#51089;&#49457;&#51068;</th>
                    <th style="width: 100px;">&#51312;&#54924;&#49688;</th>
                </tr>
            </thead>
            <tbody id="boardListBody">
            <%
                if (boardList != null && !boardList.isEmpty()) {
                    for (Board board : boardList) {
            %>
                <tr>
                    <td><%= board.getIdx() %></td>
                    <td class="text-start">
                        <a href="BoardDetail.do?idx=<%= board.getIdx() %>" class="board-title-link">
                            <%= board.getSubject() %>
                        </a>
                    </td>
                    <td><%= board.getEmpno() %></td>
                    <td><%= board.getWritedate() %></td>
                    <td><%= board.getReadnum() %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="5" class="text-muted py-4">&#46321;&#47197;&#46108; &#44172;&#49884;&#44544;&#51060; &#50630;&#49845;&#45768;&#45796;.</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

    <% if (pagecount > 0) { %>
        <nav class="board-pagination" aria-label="게시글 페이지">
            <ul class="pagination justify-content-center">
                <li class="page-item <%= cpage == 1 ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=1&ps=<%= pagesize %>" aria-label="처음">
                        <i class="bi bi-chevron-double-left"></i>
                    </a>
                </li>
                <li class="page-item <%= cpage == 1 ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=<%= cpage - 1 %>&ps=<%= pagesize %>" aria-label="이전">
                        <i class="bi bi-chevron-left"></i>
                    </a>
                </li>

                <% for (int i = startPage; i <= endPage; i++) { %>
                    <li class="page-item <%= i == cpage ? "active" : "" %>">
                        <a class="page-link" href="BoardList.do?cp=<%= i %>&ps=<%= pagesize %>"><%= i %></a>
                    </li>
                <% } %>

                <li class="page-item <%= cpage == pagecount ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=<%= cpage + 1 %>&ps=<%= pagesize %>" aria-label="다음">
                        <i class="bi bi-chevron-right"></i>
                    </a>
                </li>
                <li class="page-item <%= cpage == pagecount ? "disabled" : "" %>">
                    <a class="page-link" href="BoardList.do?cp=<%= pagecount %>&ps=<%= pagesize %>" aria-label="마지막">
                        <i class="bi bi-chevron-double-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
    <% } %>
</div>

<jsp:include page="/include/quickMenu.jsp" />
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
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

    if (!pagination) {
        pagination = document.createElement("nav");
        pagination.className = "board-pagination";
        pagination.setAttribute("aria-label", "board page");
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

    function renderRows(boardList) {
        if (!boardList || boardList.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-muted py-4">&#46321;&#47197;&#46108; &#44172;&#49884;&#44544;&#51060; &#50630;&#49845;&#45768;&#45796;.</td></tr>';
            return;
        }

        tbody.innerHTML = boardList.map(function(board) {
            return '<tr>'
                + '<td>' + escapeHtml(board.idx) + '</td>'
                + '<td class="text-start">'
                + '<a href="BoardDetail.do?idx=' + encodeURIComponent(board.idx) + '" class="board-title-link">'
                + escapeHtml(board.subject)
                + '</a>'
                + '</td>'
                + '<td>' + escapeHtml(board.empno) + '</td>'
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
        html += pageItem('<i class="bi bi-chevron-double-left"></i>', 1, data.pagesize, data.cpage === 1, false, "first");
        html += pageItem('<i class="bi bi-chevron-left"></i>', data.cpage - 1, data.pagesize, data.cpage === 1, false, "previous");

        for (var i = data.startPage; i <= data.endPage; i++) {
            html += pageItem(i, i, data.pagesize, false, i === data.cpage, "");
        }

        html += pageItem('<i class="bi bi-chevron-right"></i>', data.cpage + 1, data.pagesize, data.cpage === data.pagecount, false, "next");
        html += pageItem('<i class="bi bi-chevron-double-right"></i>', data.pagecount, data.pagesize, data.cpage === data.pagecount, false, "last");
        html += '</ul>';
        pagination.innerHTML = html;
    }

    function renderBoardList(data) {
        statusEl.textContent = "\uC804\uCCB4 " + data.totalboardcount + "\uAC74 / \uD604\uC7AC " + data.cpage + "\uD398\uC774\uC9C0";
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
                    throw new Error("Board list request failed.");
                }
                return response.json();
            })
            .then(function(data) {
                if (!data.success) {
                    throw new Error(data.message || "Board list request failed.");
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
</script>
</body>
</html>
