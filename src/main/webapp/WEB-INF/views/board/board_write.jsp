<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/include/header.jsp">
    <jsp:param name="pageTitle" value="게시글 작성" />
    <jsp:param name="pageCss" value="/assets/css/board.css" />
</jsp:include>
<body class="app-shell">
<jsp:include page="/include/appHeader.jsp" />
<c:if test="${loginRequired}">
    <input type="checkbox" id="loginModalToggle" class="modal-toggle" checked>
    <div class="modal-backdrop">
        <section class="modal-box" role="alertdialog" aria-modal="true" aria-labelledby="loginModalTitle">
            <h2 id="loginModalTitle">로그인 필요</h2>
            <p>로그인해야 게시글을 작성할 수 있습니다.</p>
            <label for="loginModalToggle" class="modal-close">확인</label>
        </section>
    </div>
</c:if>

<main class="board-page">
    <h1>게시글 작성</h1>
    <form class="board-form" action="${pageContext.request.contextPath}/BoardWriteOk.do" method="post">
        <label>제목
            <input type="text" name="subject" maxlength="100" required>
        </label>
        <section class="board-editor" data-editor-mode="form">
            <div class="editor-toolbar">
                <span class="editor-label">내용</span>
            </div>
            <textarea name="content" id="contentInput" class="editor-source" required></textarea>
            <div id="contentEditor" class="content-editor" contenteditable="true" data-placeholder="내용을 입력하세요."></div>
        </section>

        <div class="actions">
            <button type="submit">등록</button>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </div>
    </form>
</main>

<jsp:include page="/include/quickMenu.jsp" />
<script src="${pageContext.request.contextPath}/js/board-editor.js?v=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
