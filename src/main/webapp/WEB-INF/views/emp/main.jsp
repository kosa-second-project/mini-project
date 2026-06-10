<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/include/header.jsp" %>
<body class="app-shell">

    <!-- 상단 프로필 바 -->
    <nav class="navbar navbar-expand-lg profile-bar py-3 shadow-sm mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold d-flex align-items-center text-primary" href="#">
                <i class="bi bi-people-fill me-2"></i> EMP Portal
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse justify-content-end" id="navbarContent">
                <div class="d-flex align-items-center gap-3">
                    <div class="text-end d-none d-sm-block">
                        <div class="fw-bold text-dark"><c:out value="${sessionScope.loginUser.ename}"/> <span class="small text-muted font-monospace">[<c:out value="${sessionScope.loginUser.empno}"/>]</span></div>
                        <div class="small text-secondary"><c:out value="${sessionScope.loginUser.deptname}"/> · <c:out value="${sessionScope.loginUser.position}"/></div>
                    </div>
                    <span class="badge ${sessionScope.loginUser.role eq 'ADMIN' ? 'badge-admin' : 'badge-user'}">
                        <c:choose>
                            <c:when test="${sessionScope.loginUser.role eq 'ADMIN'}"><i class="bi bi-award me-1"></i>대표</c:when>
                            <c:otherwise><i class="bi bi-person me-1"></i>사원</c:otherwise>
                        </c:choose>
                    </span>
                    <a href="Logout.emp" class="btn btn-outline-danger btn-actions"><i class="bi bi-box-arrow-right me-1"></i>로그아웃</a>
                </div>
            </div>
        </div>
    </nav>

    <!-- 메인 대시보드 영역 -->
    <div class="container pb-5">
        <div class="row align-items-center mb-4">
            <div class="col">
                <h4 class="fw-bold text-dark m-0">
                    <c:choose>
                        <c:when test="${sessionScope.loginUser.role eq 'ADMIN'}">
                            <i class="bi bi-list-check me-2 text-primary"></i>전체 사원 관리 명부
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-people me-2 text-primary"></i>소속 부서 팀원 명부
                        </c:otherwise>
                    </c:choose>
                </h4>
                <p class="text-muted small m-0 mt-1">사원 이름을 누르면 상세 정보 및 관련 작업을 진행할 수 있습니다.</p>
            </div>
            <!-- 대표인 경우 사원 추가 버튼 제공 -->
            <c:if test="${sessionScope.loginUser.role eq 'ADMIN'}">
                <div class="col-auto">
                    <button class="btn btn-primary d-flex align-items-center" onclick="openInsertModal()">
                        <i class="bi bi-person-plus me-2"></i> 사원 추가
                    </button>
                </div>
            </c:if>
        </div>

        <!-- 보기 개수 선택 및 총원 표시 -->
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="text-muted small">
                총 <span id="totalEmpCount" class="fw-bold text-primary">0</span>명
            </div>
            <div class="d-flex align-items-center gap-2">
                <label for="pageSizeSelect" class="form-label small fw-bold m-0 text-secondary" style="white-space: nowrap;">보기 개수:</label>
                <select id="pageSizeSelect" class="form-select form-select-sm" style="width: auto;" onchange="changePageSize(this.value)">
                    <option value="5" selected>5개씩 보기</option>
                    <option value="10">10개씩 보기</option>
                    <option value="20">20개씩 보기</option>
                </select>
            </div>
        </div>

        <!-- 사원 목록 테이블 -->
        <div class="custom-table-container">
            <div class="table-responsive">
                <table class="table table-hover custom-table align-middle">
                    <thead>
                        <tr>
                            <th>사원번호</th>
                            <th>이름</th>
                            <th>부서명</th>
                            <th>직무</th>
                            <th>직급</th>
                            <th>급여 (만원)</th>
                            <th>권한</th>
                            <th>입사일</th>
                        </tr>
                    </thead>
                    <tbody id="empTableBody">
                        <tr>
                            <td colspan="8" class="text-center py-5">
                                <div class="spinner-border text-primary spinner-border-sm me-2" role="status"></div>
                                사원 정보를 로딩하는 중입니다...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!-- 페이징 처리 영역 -->
            <div class="d-flex justify-content-center py-3 border-top" id="paginationContainer">
                <!-- AJAX 페이징 버튼 생성 영역 -->
            </div>
        </div>
    </div>

    <!-- 1. 사원 등록 모달 (대표 전용) -->
    <c:if test="${sessionScope.loginUser.role eq 'ADMIN'}">
        <div class="modal fade" id="insertModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-light">
                        <h5 class="modal-title fw-bold"><i class="bi bi-person-plus text-primary me-2"></i>신규 사원 등록</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="insertForm" novalidate>
                        <div class="modal-body p-4">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">사원번호 (필수)</label>
                                    <input type="number" class="form-control" name="empno" required placeholder="예: 1101">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">사원이름 (필수)</label>
                                    <input type="text" class="form-control" name="ename" required placeholder="홍길동">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">비밀번호 (필수)</label>
                                    <input type="password" class="form-control" name="pwd" required placeholder="초기 비밀번호">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">부서 (선택)</label>
                                    <select class="form-select" name="deptno" id="insertDeptSelect" required>
                                        <option value="">부서 로딩중...</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">직무</label>
                                    <input type="text" class="form-control" name="job" placeholder="예: 백엔드개발">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">직급</label>
                                    <input type="text" class="form-control" name="position" placeholder="예: 사원/대리">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">상사 사원번호</label>
                                    <input type="number" class="form-control" name="mgr" placeholder="예: 1000">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">입사일</label>
                                    <input type="date" class="form-control" name="hiredate">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">급여 (만원)</label>
                                    <input type="number" class="form-control" name="sal" placeholder="예: 4500">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">시스템 권한</label>
                                    <select class="form-select" name="role">
                                        <option value="USER" selected>USER (일반 사원)</option>
                                        <option value="ADMIN">ADMIN (대표)</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer bg-light border-top">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                            <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg me-1"></i>등록 완료</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </c:if>

    <!-- 2. 사원 상세 / 수정 및 삭제 모달 (대표/사원 공용) -->
    <div class="modal fade" id="detailModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold"><i class="bi bi-person-vcard text-primary me-2"></i>사원 상세 정보</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="detailForm" novalidate>
                    <div class="modal-body p-4">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">사원번호</label>
                                <input type="number" class="form-control" id="detailEmpno" name="empno" readonly>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">사원이름</label>
                                <input type="text" class="form-control" id="detailEname" name="ename" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">부서</label>
                                <select class="form-select" id="detailDeptSelect" name="deptno" required>
                                    <!-- AJAX 로딩 -->
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">직무</label>
                                <input type="text" class="form-control" id="detailJob" name="job">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">직급</label>
                                <input type="text" class="form-control" id="detailPosition" name="position">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">상사 사원번호</label>
                                <input type="number" class="form-control" id="detailMgr" name="mgr">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">입사일</label>
                                <input type="date" class="form-control" id="detailHiredate" name="hiredate">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">급여 (만원)</label>
                                <input type="number" class="form-control" id="detailSal" name="sal">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">시스템 권한</label>
                                <select class="form-select" id="detailRole" name="role">
                                    <option value="USER">USER (일반 사원)</option>
                                    <option value="ADMIN">ADMIN (대표)</option>
                                </select>
                            </div>
                            <!-- 대표인 경우 비밀번호 재설정 기능 제공 -->
                            <c:if test="${sessionScope.loginUser.role eq 'ADMIN'}">
                                <div class="col-md-6">
                                    <label class="form-label small fw-bold">비밀번호 변경 (선택)</label>
                                    <input type="password" class="form-control" id="detailPwd" name="pwd" placeholder="비워둘 시 기존 패스워드 유지">
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="modal-footer bg-light border-top justify-content-between">
                        <div>
                            <!-- 대표만 삭제 가능 -->
                            <c:if test="${sessionScope.loginUser.role eq 'ADMIN'}">
                                <button type="button" class="btn btn-danger" onclick="deleteEmp()"><i class="bi bi-trash3 me-1"></i>사원 삭제</button>
                            </c:if>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                            <!-- 대표만 수정 저장 가능 -->
                            <c:if test="${sessionScope.loginUser.role eq 'ADMIN'}">
                                <button type="submit" class="btn btn-primary"><i class="bi bi-floppy me-1"></i>정보 수정</button>
                            </c:if>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- 성공 알림용 Toast 컴포넌트 -->
    <div class="toast-container">
        <div id="liveToast" class="toast custom-toast hide" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header border-0 bg-transparent text-primary">
                <i class="bi bi-check-circle me-2 fs-5 text-success"></i>
                <strong class="me-auto text-dark">시스템 알림</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body pt-0 pb-3" id="toastMessage">
                작업이 성공적으로 수행되었습니다.
            </div>
        </div>
    </div>

    <jsp:include page="/include/quickMenu.jsp" />

    <!-- Bootstrap Bundle JS -->
    <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
    <script>
        // 세션 정보 자바스크립트 캐싱
        const currentUserRole = '${sessionScope.loginUser.role}';
        const currentUserEmpno = parseInt('${sessionScope.loginUser.empno}');

        let bootstrapInsertModal;
        let bootstrapDetailModal;
        let bootstrapToast;

        // 페이징 전역 변수
        let currentPage = 1;
        let currentPageSize = 5;

        document.addEventListener('DOMContentLoaded', function() {
            // 모달 및 토스트 컴포넌트 초기화
            if (document.getElementById('insertModal')) {
                bootstrapInsertModal = new bootstrap.Modal(document.getElementById('insertModal'));
            }
            bootstrapDetailModal = new bootstrap.Modal(document.getElementById('detailModal'));
            bootstrapToast = new bootstrap.Toast(document.getElementById('liveToast'), { delay: 3500 });

            // 사원 리스트 초기 로딩
            loadEmpList(1, 5);

            // 대표 권한 전용 이벤트 바인딩
            if (currentUserRole === 'ADMIN') {
                const insertForm = document.getElementById('insertForm');
                if (insertForm) {
                    insertForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        submitInsertForm();
                    });
                }

                document.getElementById('detailForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    submitUpdateForm();
                });
            } else {
                // 일반 사원인 경우 상세 폼 비활성화
                disableDetailForm();
            }
        });

        // 1. 사원 리스트 AJAX 로드
        function loadEmpList(cpage = 1, pagesize = 5) {
            currentPage = cpage;
            currentPageSize = pagesize;

            const selectEl = document.getElementById('pageSizeSelect');
            if (selectEl) {
                selectEl.value = pagesize;
            }

            fetch(`EmpList.ajax?cp=\${cpage}&ps=\${pagesize}`)
                .then(res => {
                    if (res.status === 401) {
                        window.location.href = 'Login.emp';
                        return;
                    }
                    return res.json();
                })
                .then(data => {
                    const tbody = document.getElementById('empTableBody');
                    tbody.innerHTML = '';

                    const empList = data.empList;
                    const paging = data.paging;

                    // 총 인원 수 표시 업데이트
                    const totalCountEl = document.getElementById('totalEmpCount');
                    if (totalCountEl) {
                        totalCountEl.textContent = paging.totalCount;
                    }

                    if (!empList || empList.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4">조회할 사원이 없습니다.</td></tr>';
                        renderPagination(paging);
                        return;
                    }

                    empList.forEach(emp => {
                        const tr = document.createElement('tr');
                        tr.className = 'clickable-row';
                        tr.onclick = function() { openDetailModal(emp.empno); };

                        const badgeClass = emp.role === 'ADMIN' ? 'badge-admin' : 'badge-user';
                        const badgeText = emp.role === 'ADMIN' ? '대표' : '사원';

                        tr.innerHTML = `
                            <td class="fw-bold text-primary">#\${emp.empno}</td>
                            <td><span class="text-dark fw-medium">\${escapeHTML(emp.ename)}</span></td>
                            <td>\${escapeHTML(emp.deptname || '미배정')}</td>
                            <td><span class="badge bg-light text-secondary border">\${escapeHTML(emp.job || '-')}</span></td>
                            <td>\${escapeHTML(emp.position || '-')}</td>
                            <td>\${emp.sal.toLocaleString()} 만원</td>
                            <td><span class="badge \${badgeClass}">\${badgeText}</span></td>
                            <td class="font-monospace small">\${emp.hiredate}</td>
                        `;
                        tbody.appendChild(tr);
                    });

                    // 페이징 그리기
                    renderPagination(paging);
                })
                .catch(err => {
                    console.error('Error loading list:', err);
                    document.getElementById('empTableBody').innerHTML = 
                        '<tr><td colspan="8" class="text-center text-danger py-4"><i class="bi bi-exclamation-circle me-1"></i> 데이터를 불러오지 못했습니다.</td></tr>';
                });
        }

        // 1-2. 페이징 네비게이션 그리기
        function renderPagination(paging) {
            const container = document.getElementById('paginationContainer');
            if (!container) return;
            container.innerHTML = '';

            const { totalCount, cpage, pagesize, pagecount } = paging;

            const ul = document.createElement('ul');
            ul.className = 'pagination pagination-sm m-0 gap-1';

            // 처음 페이지 (<<)
            const firstLi = document.createElement('li');
            firstLi.className = `page-item \${cpage === 1 ? 'disabled' : ''}`;
            firstLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(1); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-double-left small"></i></a>`;
            ul.appendChild(firstLi);

            // 이전 페이지 (<)
            const prevLi = document.createElement('li');
            prevLi.className = `page-item \${cpage === 1 ? 'disabled' : ''}`;
            prevLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(\${cpage - 1}); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-left small"></i></a>`;
            ul.appendChild(prevLi);

            // 페이지 번호들 (최대 5개 노출)
            const startPage = Math.max(1, cpage - 2);
            const endPage = Math.min(pagecount, startPage + 4);
            const adjustedStartPage = Math.max(1, endPage - 4);

            for (let i = adjustedStartPage; i <= endPage; i++) {
                const li = document.createElement('li');
                li.className = `page-item \${cpage === i ? 'active' : ''}`;
                li.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center fw-medium" href="#" onclick="changePage(\${i}); return false;" style="width: 32px; height: 32px;">\${i}</a>`;
                ul.appendChild(li);
            }

            // 다음 페이지 (>)
            const nextLi = document.createElement('li');
            nextLi.className = `page-item \${cpage === pagecount ? 'disabled' : ''}`;
            nextLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(\${cpage + 1}); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-right small"></i></a>`;
            ul.appendChild(nextLi);

            // 끝 페이지 (>>)
            const lastLi = document.createElement('li');
            lastLi.className = `page-item \${cpage === pagecount ? 'disabled' : ''}`;
            lastLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(\${pagecount}); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-double-right small"></i></a>`;
            ul.appendChild(lastLi);

            container.appendChild(ul);
        }

        // 1-3. 페이지 번호 클릭 시 이동
        function changePage(pageNum) {
            if (pageNum < 1) return;
            loadEmpList(pageNum, currentPageSize);
        }

        // 1-4. 보기 개수 변경 시 호출
        function changePageSize(size) {
            const parsedSize = parseInt(size);
            if ([5, 10, 20].includes(parsedSize)) {
                loadEmpList(1, parsedSize);
            }
        }

        // 2. 부서 목록 AJAX 로딩 및 Select Box 바인딩
        function loadDeptList(selectElementId, selectedValue = '') {
            return fetch('DeptList.ajax')
                .then(res => res.json())
                .then(depts => {
                    const select = document.getElementById(selectElementId);
                    select.innerHTML = '<option value="">-- 부서 선택 --</option>';
                    depts.forEach(d => {
                        const option = document.createElement('option');
                        option.value = d.deptno;
                        option.textContent = `\${d.deptname} (\${d.loc})`;
                        if (parseInt(selectedValue) === d.deptno) {
                            option.selected = true;
                        }
                        select.appendChild(option);
                    });
                })
                .catch(err => {
                    console.error('Error loading departments:', err);
                });
        }

        // 3. 사원 추가 모달 열기 (대표 전용)
        function openInsertModal() {
            document.getElementById('insertForm').reset();
            loadDeptList('insertDeptSelect').then(() => {
                bootstrapInsertModal.show();
            });
        }

        // 4. 사원 등록 제출
        function submitInsertForm() {
            const form = document.getElementById('insertForm');
            const formData = new FormData(form);
            const params = new URLSearchParams(formData);

            fetch('EmpInsert.ajax', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    bootstrapInsertModal.hide();
                    loadEmpList(1, currentPageSize);
                    showToast('신규 사원이 데이터베이스에 정상적으로 등록되었습니다.');
                } else {
                    alert(data.message || '사원 등록에 실패하였습니다.');
                }
            })
            .catch(err => {
                console.error('Error inserting:', err);
                alert('등록 중 에러가 발생했습니다.');
            });
        }

        // 5. 사원 상세 모달 오픈
        function openDetailModal(empno) {
            fetch(`EmpDetail.ajax?empno=\${empno}`)
                .then(res => res.json())
                .then(res => {
                    if (res.status === 'success') {
                        const emp = res.data;
                        document.getElementById('detailEmpno').value = emp.empno;
                        document.getElementById('detailEname').value = emp.ename;
                        document.getElementById('detailJob').value = emp.job || '';
                        document.getElementById('detailPosition').value = emp.position || '';
                        document.getElementById('detailMgr').value = emp.mgr > 0 ? emp.mgr : '';
                        document.getElementById('detailHiredate').value = emp.hiredate || '';
                        document.getElementById('detailSal').value = emp.sal;
                        
                        // 권한 바인딩
                        const roleSelect = document.getElementById('detailRole');
                        for (let opt of roleSelect.options) {
                            if (opt.value === emp.role) {
                                opt.selected = true;
                                break;
                            }
                        }

                        // 패스워드창 비우기
                        const pwdInput = document.getElementById('detailPwd');
                        if (pwdInput) pwdInput.value = '';

                        // 부서 Select 로드 후 모달 열기
                        loadDeptList('detailDeptSelect', emp.deptno).then(() => {
                            bootstrapDetailModal.show();
                        });
                    } else {
                        alert(res.message);
                    }
                })
                .catch(err => {
                    console.error('Error loading detail:', err);
                    alert('상세 정보를 조회할 수 없습니다.');
                });
        }

        // 6. 사원 수정 요청
        function submitUpdateForm() {
            const form = document.getElementById('detailForm');
            const formData = new FormData(form);
            const params = new URLSearchParams(formData);

            fetch('EmpUpdate.ajax', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    bootstrapDetailModal.hide();
                    loadEmpList(currentPage, currentPageSize);
                    showToast('사원 정보가 비동기로 안전하게 업데이트되었습니다.');
                } else {
                    alert(data.message || '정보 수정에 실패했습니다.');
                }
            })
            .catch(err => {
                console.error('Error updating:', err);
                alert('업데이트 과정에서 에러가 발생했습니다.');
            });
        }

        // 7. 사원 삭제 요청
        function deleteEmp() {
            const empno = document.getElementById('detailEmpno').value;
            if (currentUserEmpno === parseInt(empno)) {
                alert('본인 계정은 삭제할 수 없습니다.');
                return;
            }

            if (!confirm(`사번 #\${empno} 사원을 삭제하시겠습니까?\n삭제 후 복구가 불가능합니다.`)) {
                return;
            }

            const params = new URLSearchParams();
            params.append('empno', empno);

            fetch('EmpDelete.ajax', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    bootstrapDetailModal.hide();
                    loadEmpList(currentPage, currentPageSize);
                    showToast('사원 정보가 성공적으로 영구 삭제되었습니다.');
                } else {
                    alert(data.message || '사원 삭제에 실패하였습니다.');
                }
            })
            .catch(err => {
                console.error('Error deleting:', err);
                alert('삭제 중 오류가 발생했습니다.');
            });
        }

        // 일반 사원인 경우 상세 폼 비활성화
        function disableDetailForm() {
            document.getElementById('detailEname').readOnly = true;
            document.getElementById('detailDeptSelect').disabled = true;
            document.getElementById('detailJob').readOnly = true;
            document.getElementById('detailPosition').readOnly = true;
            document.getElementById('detailMgr').readOnly = true;
            document.getElementById('detailHiredate').readOnly = true;
            document.getElementById('detailSal').readOnly = true;
            document.getElementById('detailRole').disabled = true;
        }

        // 토스트 알림 띄우기
        function showToast(msg) {
            document.getElementById('toastMessage').textContent = msg;
            bootstrapToast.show();
        }

        // HTML 이스케이프
        function escapeHTML(str) {
            if (!str) return '';
            return str.replace(/[&<>'"]/g, 
                tag => ({
                    '&': '&amp;',
                    '<': '&lt;',
                    '>': '&gt;',
                    "'": '&#39;',
                    '"': '&quot;'
                }[tag] || tag)
            );
        }
    </script>
</body>
</html>
