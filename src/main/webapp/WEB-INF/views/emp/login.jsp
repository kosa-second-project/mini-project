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
            <script>
                document.getElementById('loginForm').addEventListener('submit', function (e) {
                    e.preventDefault();

                    const alertBox = document.getElementById('alertBox');
                    const errorMsg = document.getElementById('errorMsg');
                    const loginCard = document.getElementById('loginCard');
                    const btnSubmit = document.getElementById('btnSubmit');
                    const btnSpinner = document.getElementById('btnSpinner');
                    const btnIcon = document.getElementById('btnIcon');

                    alertBox.classList.add('d-none');

                    const empno = document.getElementById('empno').value.trim();
                    const pwd = document.getElementById('pwd').value.trim();

                    if (!empno || !pwd) {
                        showError('사원번호와 비밀번호를 모두 입력해 주세요.');
                        shakeCard();
                        return;
                    }

                    // UI 로딩 상태 시작
                    btnSubmit.disabled = true;
                    btnSpinner.classList.remove('d-none');
                    btnIcon.classList.add('d-none');

                    // AJAX 요청 전송
                    const params = new URLSearchParams();
                    params.append('empno', empno);
                    params.append('pwd', pwd);

                    fetch('LoginOkAjax', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: params
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('인증 서버 연결 실패');
                            }
                            return response.json();
                        })
                        .then(data => {
                            if (data.status === 'success') {
                                // 로그인 성공 시 메인 화면으로 이동
                                window.location.href = 'Main.emp';
                            } else {
                                showError(data.message || '인증에 실패하였습니다.');
                                shakeCard();
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            showError('서버와의 통신 도중 오류가 발생했습니다.');
                            shakeCard();
                        })
                        .finally(() => {
                            // UI 로딩 상태 종료
                            btnSubmit.disabled = false;
                            btnSpinner.classList.add('d-none');
                            btnIcon.classList.remove('d-none');
                        });

                    function showError(msg) {
                        errorMsg.textContent = msg;
                        alertBox.classList.remove('d-none');
                    }

                    function shakeCard() {
                        loginCard.classList.add('shake');
                        setTimeout(() => {
                            loginCard.classList.remove('shake');
                        }, 500);
                    }
                });
            </script>
        </body>

        </html>