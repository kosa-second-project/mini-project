<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/include/header.jsp">
    <jsp:param name="pageTitle" value="게시글 수정" />
    <jsp:param name="pageCss" value="/assets/css/board.css" />
</jsp:include>
<body class="app-shell">
<jsp:include page="/include/appHeader.jsp" />
<main class="board-page">
    <h1>게시글 수정</h1>
    <c:if test="${not empty message}">
        <p class="error"><c:out value="${message}" /></p>
    </c:if>
    <c:choose>
        <c:when test="${empty board or board.deleted}">
            <p>수정할 수 없는 게시글입니다.</p>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </c:when>
        <c:otherwise>
            <form class="board-form" action="${pageContext.request.contextPath}/BoardEditOk.do" method="post">
                <input type="hidden" name="idx" value="${board.idx}">
                <p class="muted">작성자 <c:out value="${empty board.ename ? board.empno : board.ename}" /><c:if test="${not empty board.deptname}"> (<c:out value="${board.deptname}" />)</c:if></p>
                <label>제목
                    <input type="text" name="subject" maxlength="100" value="${fn:escapeXml(board.subject)}" required>
                </label>
                <section class="board-editor" data-editor-mode="form">
                    <div class="editor-toolbar">
                        <span class="editor-label">내용</span>
                    </div>
                    <textarea name="content" id="contentInput" class="editor-source" required><c:out value="${board.content}" /></textarea>
                    <div id="contentEditor" class="content-editor" contenteditable="true" data-placeholder="내용을 입력하세요."></div>
                </section>

                <div class="actions">
                    <button type="submit">수정</button>
                    <a href="${pageContext.request.contextPath}/BoardDetail.do?idx=${board.idx}">취소</a>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/include/quickMenu.jsp" />
<script src="${pageContext.request.contextPath}/js/board-editor.js?v=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
