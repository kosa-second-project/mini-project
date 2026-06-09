<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="loginEmpno" value="${1060}" />
<c:set var="boardIdx" value="${not empty board ? board.idx : idx}" />

<section id="replySection" style="margin-top: 40px; border-top: 1px solid #222;">
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 18px 0 12px;">
        <h3 style="margin: 0; font-size: 18px; font-weight: 700;">
            &#45843;&#44544; <span style="color: #03c75a;"><c:out value="${fn:length(replyList)}"/></span>
        </h3>
    </div>

    <form action="ReplyWrite.do" method="post" style="margin: 0 0 10px; padding: 16px; border: 1px solid #ddd; border-radius: 6px; background: #fff;">
        <input type="hidden" name="idx_fk" value="${boardIdx}">

        <div style="display: flex; gap: 12px;">
            <div style="width: 38px; height: 38px; border-radius: 50%; background: #f1f3f5; border: 1px solid #ddd; display: flex; align-items: center; justify-content: center; font-size: 11px; color: #555; flex-shrink: 0;">
                <c:out value="${loginEmpno}"/>
            </div>
            <div style="flex: 1;">
                <div style="margin-bottom: 8px; font-size: 13px; font-weight: 700;">&#49324;&#48264; <c:out value="${loginEmpno}"/></div>
                <textarea name="content" rows="3" placeholder="&#45843;&#44544;&#51012; &#51077;&#47141;&#54616;&#49464;&#50836;" required
                    style="width: 100%; box-sizing: border-box; resize: vertical; border: 1px solid #ddd; border-radius: 4px; padding: 10px; font-size: 14px; line-height: 1.5; outline: none;"></textarea>
                <div style="display: flex; justify-content: flex-end; margin-top: 8px;">
                    <button type="submit" style="border: 0; border-radius: 4px; background: #03c75a; color: #fff; padding: 8px 18px; font-size: 14px; font-weight: 700; cursor: pointer;">&#46321;&#47197;</button>
                </div>
            </div>
        </div>
    </form>

    <div>
        <c:choose>
            <c:when test="${not empty replyList}">
                <c:forEach var="reply" items="${replyList}">
                    <article class="reply-item" style="display: flex; gap: 12px; padding: 18px 0; border-bottom: 1px solid #eee;">
                        <div style="width: 38px; height: 38px; border-radius: 50%; background: #f1f3f5; border: 1px solid #ddd; display: flex; align-items: center; justify-content: center; font-size: 11px; color: #555; flex-shrink: 0;">
                            <c:out value="${reply.empno}"/>
                        </div>

                        <div style="flex: 1; min-width: 0;">
                            <div style="display: flex; align-items: baseline; gap: 8px; margin-bottom: 6px;">
                                <strong style="font-size: 14px;">&#49324;&#48264; <c:out value="${reply.empno}"/></strong>
                                <span style="font-size: 12px; color: #888;"><c:out value="${reply.writedate}"/></span>
                            </div>

                            <div class="reply-view">
                                <p class="reply-content" style="margin: 0 0 8px; font-size: 14px; line-height: 1.6; color: #222; white-space: pre-wrap;"><c:out value="${reply.content}"/></p>

                                <c:if test="${reply.empno == loginEmpno}">
                                    <div class="reply-actions" style="display: flex; align-items: center; gap: 7px; font-size: 13px;">
                                        <button type="button" class="reply-edit-open" style="border: 0; background: transparent; padding: 0; color: #666; cursor: pointer;">&#49688;&#51221;</button>
                                        <span style="color: #ddd;">|</span>
                                        <form action="ReplyDelete.do" method="post" style="display: inline; margin: 0;">
                                            <input type="hidden" name="no" value="${reply.no}">
                                            <input type="hidden" name="idx_fk" value="${boardIdx}">
                                            <button type="submit" style="border: 0; background: transparent; padding: 0; color: #666; cursor: pointer;">&#49325;&#51228;</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>

                            <c:if test="${reply.empno == loginEmpno}">
                                <form action="ReplyUpdate.do" method="post" class="reply-edit-form" hidden style="margin: 0;">
                                    <input type="hidden" name="no" value="${reply.no}">
                                    <input type="hidden" name="idx_fk" value="${boardIdx}">
                                    <textarea name="content" rows="3" required
                                        style="width: 100%; box-sizing: border-box; resize: vertical; border: 1px solid #ddd; border-radius: 4px; padding: 10px; font-size: 14px; line-height: 1.5; outline: none;"><c:out value="${reply.content}"/></textarea>
                                    <div style="display: flex; justify-content: flex-end; gap: 6px; margin-top: 8px;">
                                        <button type="button" class="reply-edit-cancel" style="border: 1px solid #ddd; border-radius: 4px; background: #fff; color: #333; padding: 7px 14px; font-size: 13px; cursor: pointer;">&#52712;&#49548;</button>
                                        <button type="submit" style="border: 0; border-radius: 4px; background: #03c75a; color: #fff; padding: 7px 14px; font-size: 13px; font-weight: 700; cursor: pointer;">&#49688;&#51221;</button>
                                    </div>
                                </form>
                            </c:if>
                        </div>
                    </article>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div style="padding: 36px 0; border-bottom: 1px solid #eee; text-align: center; color: #888; font-size: 14px;">
                    &#46321;&#47197;&#46108; &#45843;&#44544;&#51060; &#50630;&#49845;&#45768;&#45796;.
                </div>
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
