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

    fetch(`EmpListAjax?cp=${cpage}&ps=${pagesize}`)
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
                    <td class="fw-bold text-primary">#${emp.empno}</td>
                    <td><span class="text-dark fw-medium">${escapeHTML(emp.ename)}</span></td>
                    <td>${escapeHTML(emp.deptname || '미배정')}</td>
                    <td><span class="badge bg-light text-secondary border">${escapeHTML(emp.job || '-')}</span></td>
                    <td>${escapeHTML(emp.position || '-')}</td>
                    <td>${emp.sal.toLocaleString()} 만원</td>
                    <td><span class="badge ${badgeClass}">${badgeText}</span></td>
                    <td class="font-monospace small">${emp.hiredate}</td>
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
    firstLi.className = `page-item ${cpage === 1 ? 'disabled' : ''}`;
    firstLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(1); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-double-left small"></i></a>`;
    ul.appendChild(firstLi);

    // 이전 페이지 (<)
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${cpage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(${cpage - 1}); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-left small"></i></a>`;
    ul.appendChild(prevLi);

    // 페이지 번호들 (최대 5개 노출)
    const startPage = Math.max(1, cpage - 2);
    const endPage = Math.min(pagecount, startPage + 4);
    const adjustedStartPage = Math.max(1, endPage - 4);

    for (let i = adjustedStartPage; i <= endPage; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${cpage === i ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center fw-medium" href="#" onclick="changePage(${i}); return false;" style="width: 32px; height: 32px;">${i}</a>`;
        ul.appendChild(li);
    }

    // 다음 페이지 (>)
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${cpage === pagecount ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(${cpage + 1}); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-right small"></i></a>`;
    ul.appendChild(nextLi);

    // 끝 페이지 (>>)
    const lastLi = document.createElement('li');
    lastLi.className = `page-item ${cpage === pagecount ? 'disabled' : ''}`;
    lastLi.innerHTML = `<a class="page-link border-0 rounded-circle d-flex align-items-center justify-content-center" href="#" onclick="changePage(${pagecount}); return false;" style="width: 32px; height: 32px;"><i class="bi bi-chevron-double-right small"></i></a>`;
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
    return fetch('DeptListAjax')
        .then(res => res.json())
        .then(depts => {
            const select = document.getElementById(selectElementId);
            select.innerHTML = '<option value="">-- 부서 선택 --</option>';
            depts.forEach(d => {
                const option = document.createElement('option');
                option.value = d.deptno;
                option.textContent = `${d.deptname} (${d.loc})`;
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

    fetch('EmpInsertAjax', {
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
    fetch(`EmpDetailAjax?empno=${empno}`)
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
    
    // HTML5 required 유효성 검사 수행
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        alert('부서 등 필수 항목을 올바르게 선택해주세요.');
        return;
    }
    
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    fetch('EmpUpdateAjax', {
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

    if (!confirm(`사번 #${empno} 사원을 삭제하시겠습니까?\n삭제 후 복구가 불가능합니다.`)) {
        return;
    }

    const params = new URLSearchParams();
    params.append('empno', empno);

    fetch('EmpDeleteAjax', {
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
