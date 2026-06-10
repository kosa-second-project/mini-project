<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="loginEmpno" value="${sessionScope.loginUser.empno}" />
<c:set var="loginName" value="${empty sessionScope.loginUser.ename ? sessionScope.loginUser.empno : sessionScope.loginUser.ename}" />
<c:set var="loginDeptname" value="${sessionScope.loginUser.deptname}" />
<c:set var="loginAvatarName" value="${loginName}" />
<c:if test="${not empty sessionScope.loginUser.ename and fn:length(sessionScope.loginUser.ename) > 1}">
    <c:set var="loginAvatarName" value="${fn:substring(sessionScope.loginUser.ename, 1, fn:length(sessionScope.loginUser.ename))}" />
</c:if>
<c:set var="boardIdx" value="${not empty board ? board.idx : idx}" />

<section id="replySection" class="reply-section mt-5 pt-3"
    data-board-idx="${boardIdx}"
    data-write-url="${pageContext.request.contextPath}/ReplyWriteAjax"
    data-update-url="${pageContext.request.contextPath}/ReplyUpdateAjax"
    data-delete-url="${pageContext.request.contextPath}/ReplyDeleteAjax">
    <div class="d-flex align-items-center justify-content-between mb-2">
        <h3 class="reply-title mb-0">댓글 <span class="reply-count"><c:out value="${fn:length(replyList)}"/></span></h3>
    </div>

    <c:choose>
        <c:when test="${not empty loginEmpno}">
            <form action="ReplyWrite.do" method="post" class="reply-write-form d-flex gap-3 mb-3 p-3 border rounded-2"
                data-ajax-url="${pageContext.request.contextPath}/ReplyWriteAjax">
                <input type="hidden" name="idx_fk" value="${boardIdx}">

                <div class="reply-avatar"><c:out value="${loginAvatarName}"/></div>
                <div class="flex-grow-1 min-w-0">
                    <div class="fw-bold small mb-2"><c:out value="${loginName}"/><c:if test="${not empty loginDeptname}"> (<c:out value="${loginDeptname}"/>)</c:if></div>
                    <textarea name="content" rows="3" class="form-control reply-textarea" placeholder="댓글을 입력하세요." required></textarea>
                    <div class="d-flex justify-content-end mt-2">
                        <button type="submit" class="btn btn-primary btn-sm">등록</button>
                    </div>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <div class="reply-login-message mb-3 p-3 border rounded-2 text-muted">로그인하면 댓글을 작성할 수 있습니다.</div>
        </c:otherwise>
    </c:choose>

    <div class="reply-list">
        <c:choose>
            <c:when test="${not empty replyList}">
                <c:forEach var="reply" items="${replyList}">
                    <c:set var="replyName" value="${empty reply.ename ? reply.empno : reply.ename}" />
                    <c:set var="replyAvatarName" value="${replyName}" />
                    <c:if test="${not empty reply.ename and fn:length(reply.ename) > 1}">
                        <c:set var="replyAvatarName" value="${fn:substring(reply.ename, 1, fn:length(reply.ename))}" />
                    </c:if>
                    <article class="reply-item d-flex gap-3 py-3 border-bottom">
                        <div class="reply-avatar"><c:out value="${replyAvatarName}"/></div>

                        <div class="flex-grow-1 min-w-0">
                            <div class="d-flex align-items-baseline gap-2 mb-1">
                                <strong class="small"><c:out value="${replyName}"/><c:if test="${not empty reply.deptname}"> (<c:out value="${reply.deptname}"/>)</c:if></strong>
                                <span class="text-muted small"><c:out value="${reply.writedate}"/></span>
                            </div>

                            <div class="reply-view">
                                <p class="reply-content mb-2"><c:out value="${reply.content}"/></p>

                                <c:if test="${not empty loginEmpno and reply.empno == loginEmpno}">
                                    <div class="d-flex align-items-center gap-2">
                                        <button type="button" class="btn btn-link btn-sm reply-action-btn reply-edit-open">
                                            <i class="bi bi-pencil-square"></i>
                                            수정
                                        </button>
                                        <form action="ReplyDelete.do" method="post" class="reply-delete-form m-0"
                                            data-ajax-url="${pageContext.request.contextPath}/ReplyDeleteAjax">
                                            <input type="hidden" name="no" value="${reply.no}">
                                            <input type="hidden" name="idx_fk" value="${boardIdx}">
                                            <button type="submit" class="btn btn-link btn-sm reply-action-btn reply-danger-btn">
                                                <i class="bi bi-trash3"></i>
                                                삭제
                                            </button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>

                            <c:if test="${not empty loginEmpno and reply.empno == loginEmpno}">
                                <form action="ReplyUpdate.do" method="post" class="reply-edit-form" hidden
                                    data-ajax-url="${pageContext.request.contextPath}/ReplyUpdateAjax">
                                    <input type="hidden" name="no" value="${reply.no}">
                                    <input type="hidden" name="idx_fk" value="${boardIdx}">
                                    <textarea name="content" rows="3" class="form-control reply-textarea" required><c:out value="${reply.content}"/></textarea>
                                    <div class="d-flex justify-content-end gap-2 mt-2">
                                        <button type="button" class="btn btn-outline-secondary btn-sm reply-edit-cancel">취소</button>
                                        <button type="submit" class="btn btn-primary btn-sm">저장</button>
                                    </div>
                                </form>
                            </c:if>
                        </div>
                    </article>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="reply-empty py-4 border-bottom text-center text-muted">등록된 댓글이 없습니다.</div>
            </c:otherwise>
        </c:choose>
    </div>
</section>
