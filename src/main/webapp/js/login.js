document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
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
    }
});
