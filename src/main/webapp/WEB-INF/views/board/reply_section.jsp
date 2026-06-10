<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="loginEmpno" value="${sessionScope.loginUser.empno}" />
<c:set var="boardIdx" value="${not empty board ? board.idx : idx}" />

<section id="replySection" class="reply-section mt-5 pt-3">
    <div class="d-flex align-items-center justify-content-between mb-2">
        <h3 class="reply-title mb-0">댓글 <span class="reply-count"><c:out value="${fn:length(replyList)}"/></span></h3>
    </div>

    <c:choose>
        <c:when test="${not empty loginEmpno}">
            <form action="ReplyWrite.do" method="post" class="reply-write-form d-flex gap-3 mb-3 p-3 border rounded-2">
                <input type="hidden" name="idx_fk" value="${boardIdx}">

                <div class="reply-avatar"><c:out value="${loginEmpno}"/></div>
                <div class="flex-grow-1 min-w-0">
                    <div class="fw-bold small mb-2">사번 <c:out value="${loginEmpno}"/></div>
                    <textarea name="content" rows="3" class="form-control reply-textarea" placeholder="댓글을 입력하세요" required></textarea>
                    <div class="d-flex justify-content-end mt-2">
                        <button type="submit" class="btn btn-primary btn-sm">등록</button>
                    </div>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <div class="reply-login-message mb-3 p-3 border rounded-2 text-muted">로그인 후 댓글을 작성할 수 있습니다.</div>
        </c:otherwise>
    </c:choose>

    <div class="reply-list">
        <c:choose>
            <c:when test="${not empty replyList}">
                <c:forEach var="reply" items="${replyList}">
                    <article class="reply-item d-flex gap-3 py-3 border-bottom">
                        <div class="reply-avatar"><c:out value="${reply.empno}"/></div>

                        <div class="flex-grow-1 min-w-0">
                            <div class="d-flex align-items-baseline gap-2 mb-1">
                                <strong class="small">사번 <c:out value="${reply.empno}"/></strong>
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
                                        <form action="ReplyDelete.do" method="post" class="m-0">
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
                                <form action="ReplyUpdate.do" method="post" class="reply-edit-form" hidden>
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

<script>
(function() {
    function closeEdit(item) {
        var view = item.querySelector(".reply-view");
        var form = item.querySelector(".reply-edit-form");
        if (view && form) {
            view.hidden = false;
            form.hidden = true;
        }
    }

    document.querySelectorAll("#replySection .reply-edit-open").forEach(function(button) {
        button.addEventListener("click", function() {
            document.querySelectorAll("#replySection .reply-item").forEach(closeEdit);

            var item = button.closest(".reply-item");
            var view = item.querySelector(".reply-view");
            var form = item.querySelector(".reply-edit-form");

            view.hidden = true;
            form.hidden = false;
            form.querySelector("textarea").focus();
        });
    });

    document.querySelectorAll("#replySection .reply-edit-cancel").forEach(function(button) {
        button.addEventListener("click", function() {
            closeEdit(button.closest(".reply-item"));
        });
    });
})();
</script>
