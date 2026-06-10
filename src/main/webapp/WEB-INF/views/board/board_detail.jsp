<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/include/header.jsp">
    <jsp:param name="pageTitle" value="게시글 상세" />
    <jsp:param name="pageCss" value="/assets/css/board.css" />
    <jsp:param name="pageCss2" value="/assets/css/reply.css" />
</jsp:include>
<body class="app-shell">
<jsp:include page="/include/appHeader.jsp" />
<main class="board-page">
    <c:choose>
        <c:when test="${empty board}">
            <h1>게시글을 찾을 수 없습니다.</h1>
            <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
        </c:when>
        <c:otherwise>
            <article class="board-detail">
                <h1><c:out value="${board.subject}" /></h1>
                <div class="meta">
                    <span>글 번호 ${board.idx}</span>
                    <span>작성자 <c:out value="${empty board.ename ? board.empno : board.ename}" /><c:if test="${not empty board.deptname}"> (<c:out value="${board.deptname}" />)</c:if></span>
                    <span>작성일 ${board.writedate}</span>
                    <span>조회수 ${board.readnum}</span>
                </div>
                <textarea id="boardContentSource" class="editor-source"><c:out value="${board.content}" /></textarea>
                <div id="boardContent" class="content rich-content" data-editor-mode="detail"></div>

                <div class="actions">
                    <c:if test="${not empty sessionScope.loginUser and not board.deleted}">
                        <a href="${pageContext.request.contextPath}/BoardWriteForm.do?parentIdx=${board.idx}">답글</a>
                    </c:if>
                    <c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser.empno == board.empno}">
                        <a href="${pageContext.request.contextPath}/BoardEditForm.do?idx=${board.idx}">수정</a>
                        <a href="${pageContext.request.contextPath}/BoardDeleteForm.do?idx=${board.idx}">삭제</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/BoardList.do">목록</a>
                </div>
            </article>

            <jsp:include page="/WEB-INF/views/board/reply_section.jsp"/>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/include/quickMenu.jsp" />
<c:if test="${not empty kakaoMapKey}">
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}"></script>
</c:if>
<script src="${pageContext.request.contextPath}/js/board-editor.js?v=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/js/reply.js?v=<%=System.currentTimeMillis()%>"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
