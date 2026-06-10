<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ include file="/include/header.jsp" %>

        <body class="login-wrapper dark-theme">

            <div class="login-card glass-card text-center" id="loginCard">
                <div class="mb-4">
                    <div class="bg-primary bg-gradient text-white rounded-circle d-inline-flex align-items-center justify-content-center"
                        style="width: 64px; height: 64px; box-shadow: 0 8px 20px var(--primary-glow);">
                        <i class="fa-solid fa-users-gear fa-xl"></i>
                    </div>
                    <h3 class="mt-3 fw-bold text-white">EMP Portal</h3>
                    <p class="text-secondary small">사원번호와 비밀번호를 입력해주세요.</p>
                </div>

                <!-- 에러 메시지 알림 -->
                <div id="alertBox" class="alert alert-danger d-none py-2 px-3 small border-0" role="alert"
                    style="border-radius: 8px;">
                    <i class="fa-solid fa-circle-exclamation me-1"></i> <span id="errorMsg"></span>
                </div>

                <form id="loginForm" novalidate>
                    <div class="form-floating mb-3">
                        <input type="number" class="form-control" id="empno" name="empno" placeholder="1000" required>
                        <label for="empno" class="text-dark"><i class="fa-solid fa-id-card me-2"></i>사원 번호</label>
                    </div>
                    <div class="form-floating mb-4">
                        <input type="password" class="form-control" id="pwd" name="pwd" placeholder="Password" required>
                        <label for="pwd" class="text-dark"><i class="fa-solid fa-lock me-2"></i>비밀번호</label>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 d-flex align-items-center justify-content-center"
                        id="btnSubmit">
                        <span class="spinner-border spinner-border-sm me-2 d-none" id="btnSpinner" role="status"
                            aria-hidden="true"></span>
                        <i class="fa-solid fa-right-to-bracket me-2" id="btnIcon"></i>로그인
                    </button>
                </form>

                <div class="mt-4 pt-3 border-top border-secondary border-opacity-25 text-secondary small">
                    <span>© 2026 EMP Administration System</span>
                </div>
            </div>

            <!-- Bootstrap Bundle JS -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            <!-- Context Path 및 캐시 방지 적용한 외부 스크립트 링크 -->
            <script src="${pageContext.request.contextPath}/js/login.js?v=<%=System.currentTimeMillis()%>"></script>
        </body>

        </html>