(function() {
    var section = document.getElementById("replySection");
    if (!section) {
        return;
    }

    var replyList = section.querySelector(".reply-list");
    var count = section.querySelector(".reply-count");

    function closeEdit(item) {
        if (!item) {
            return;
        }

        var view = item.querySelector(".reply-view");
        var form = item.querySelector(".reply-edit-form");
        if (view && form) {
            view.hidden = false;
            form.hidden = true;
        }
    }

    function escapeHtml(value) {
        return String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function sendForm(form) {
        var body = new URLSearchParams(new FormData(form));
        return fetch(form.dataset.ajaxUrl, {
            method: "post",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                "Accept": "application/json"
            },
            body: body
        }).then(function(response) {
            return response.json();
        });
    }

    function replyTemplate(reply) {
        var content = escapeHtml(reply.content);
        var writerName = escapeHtml(reply.ename || reply.empno);
        var deptname = reply.deptname ? " (" + escapeHtml(reply.deptname) + ")" : "";
        var actions = "";
        var editForm = "";

        if (reply.canEdit) {
            actions =
                '<div class="d-flex align-items-center gap-2">' +
                    '<button type="button" class="btn btn-link btn-sm reply-action-btn reply-edit-open">' +
                        '<i class="bi bi-pencil-square"></i> 수정' +
                    '</button>' +
                    '<form action="ReplyDelete.do" method="post" class="reply-delete-form m-0" data-ajax-url="' + section.dataset.deleteUrl + '">' +
                        '<input type="hidden" name="no" value="' + reply.no + '">' +
                        '<input type="hidden" name="idx_fk" value="' + reply.idx_fk + '">' +
                        '<button type="submit" class="btn btn-link btn-sm reply-action-btn reply-danger-btn">' +
                            '<i class="bi bi-trash3"></i> 삭제' +
                        '</button>' +
                    '</form>' +
                '</div>';

            editForm =
                '<form action="ReplyUpdate.do" method="post" class="reply-edit-form" data-ajax-url="' + section.dataset.updateUrl + '" hidden>' +
                    '<input type="hidden" name="no" value="' + reply.no + '">' +
                    '<input type="hidden" name="idx_fk" value="' + reply.idx_fk + '">' +
                    '<textarea name="content" rows="3" class="form-control reply-textarea" required>' + content + '</textarea>' +
                    '<div class="d-flex justify-content-end gap-2 mt-2">' +
                        '<button type="button" class="btn btn-outline-secondary btn-sm reply-edit-cancel">취소</button>' +
                        '<button type="submit" class="btn btn-primary btn-sm">저장</button>' +
                    '</div>' +
                '</form>';
        }

        return '' +
            '<article class="reply-item d-flex gap-3 py-3 border-bottom">' +
                '<div class="reply-avatar">' + writerName + '</div>' +
                '<div class="flex-grow-1 min-w-0">' +
                    '<div class="d-flex align-items-baseline gap-2 mb-1">' +
                        '<strong class="small">' + writerName + deptname + '</strong>' +
                        '<span class="text-muted small">' + escapeHtml(reply.writedate) + '</span>' +
                    '</div>' +
                    '<div class="reply-view">' +
                        '<p class="reply-content mb-2">' + content + '</p>' +
                        actions +
                    '</div>' +
                    editForm +
                '</div>' +
            '</article>';
    }

    function renderReplies(data) {
        count.textContent = data.replyCount;

        if (!data.replies || data.replies.length === 0) {
            replyList.innerHTML = '<div class="reply-empty py-4 border-bottom text-center text-muted">등록된 댓글이 없습니다.</div>';
            return;
        }

        replyList.innerHTML = data.replies.map(function(reply) {
            return replyTemplate(reply);
        }).join("");
    }

    section.addEventListener("click", function(event) {
        var openButton = event.target.closest(".reply-edit-open");
        if (openButton) {
            section.querySelectorAll(".reply-item").forEach(closeEdit);

            var item = openButton.closest(".reply-item");
            var view = item.querySelector(".reply-view");
            var form = item.querySelector(".reply-edit-form");

            view.hidden = true;
            form.hidden = false;
            form.querySelector("textarea").focus();
            return;
        }

        var cancelButton = event.target.closest(".reply-edit-cancel");
        if (cancelButton) {
            closeEdit(cancelButton.closest(".reply-item"));
        }
    });

    section.addEventListener("submit", function(event) {
        var form = event.target;
        if (!form.dataset.ajaxUrl) {
            return;
        }

        event.preventDefault();

        if (form.classList.contains("reply-delete-form") && !confirm("댓글을 삭제하시겠습니까?")) {
            return;
        }

        sendForm(form).then(function(data) {
            if (data.status !== "success") {
                alert(data.message || "댓글 처리에 실패했습니다.");
                return;
            }

            renderReplies(data);

            if (form.classList.contains("reply-write-form")) {
                form.reset();
            }
        }).catch(function(error) {
            console.error(error);
            alert("댓글 처리 중 오류가 발생했습니다.");
        });
    });
})();
