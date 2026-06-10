(function () {
    function ready(callback) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", callback);
        } else {
            callback();
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

    function isEditorHtml(value) {
        return /<([a-z][\w:-]*)(\s|>)/i.test(value || "");
    }

    function textToHtml(value) {
        var text = String(value || "").replace(/\r\n/g, "\n");
        if (!text.trim()) {
            return "<p><br></p>";
        }
        return text.split(/\n{2,}/).map(function (block) {
            return "<p>" + escapeHtml(block).replace(/\n/g, "<br>") + "</p>";
        }).join("");
    }

    function sanitizeEditorHtml(value) {
        var template = document.createElement("template");
        template.innerHTML = value || "";
        var output = document.createElement("div");

        function appendInlineText(source, target) {
            source.childNodes.forEach(function (node) {
                if (node.nodeType === Node.TEXT_NODE) {
                    target.appendChild(document.createTextNode(node.textContent));
                } else if (node.nodeType === Node.ELEMENT_NODE && node.tagName === "BR") {
                    target.appendChild(document.createElement("br"));
                } else if (node.nodeType === Node.ELEMENT_NODE) {
                    appendInlineText(node, target);
                }
            });
        }

        template.content.childNodes.forEach(function (node) {
            if (node.nodeType === Node.TEXT_NODE && node.textContent.trim()) {
                var p = document.createElement("p");
                p.textContent = node.textContent;
                output.appendChild(p);
                return;
            }
            if (node.nodeType !== Node.ELEMENT_NODE) {
                return;
            }

            var paragraph = document.createElement("p");
            appendInlineText(node, paragraph);
            output.appendChild(paragraph);
        });

        return output.innerHTML || "<p><br></p>";
    }

    function ensureTrailingParagraph(editor) {
        if (!editor.lastElementChild) {
            editor.insertAdjacentHTML("beforeend", "<p><br></p>");
        }
    }

    function initDetailContent() {
        var content = document.querySelector("[data-editor-mode='detail']");
        var source = document.getElementById("boardContentSource");
        if (!content) {
            return;
        }

        var raw = source ? source.value.trim() : content.textContent.trim();
        content.innerHTML = isEditorHtml(raw) ? sanitizeEditorHtml(raw) : textToHtml(raw);
    }

    function initFormEditor() {
        var form = document.querySelector(".board-form");
        var editor = document.getElementById("contentEditor");
        var source = document.getElementById("contentInput");

        if (!form || !editor || !source) {
            return;
        }

        function syncContent() {
            ensureTrailingParagraph(editor);
            source.value = sanitizeEditorHtml(editor.innerHTML).trim();
        }

        editor.innerHTML = isEditorHtml(source.value) ? sanitizeEditorHtml(source.value) : textToHtml(source.value);
        ensureTrailingParagraph(editor);

        editor.addEventListener("input", syncContent);
        form.addEventListener("submit", syncContent);
        syncContent();
    }

    ready(function () {
        initFormEditor();
        initDetailContent();
    });
})();
