<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EMP Portal Preview</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Poppins:wght@400;500;600;700&family=Raleway:wght@500;600;700;800&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/folioone-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/emp.css">
</head>
<body class="app-shell">
    <nav class="navbar navbar-expand-lg profile-bar py-3 shadow-sm mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold d-flex align-items-center text-primary" href="#">
                <i class="bi bi-people-fill me-2"></i> EMP Portal
            </a>
            <div class="d-flex align-items-center gap-3 ms-auto">
                <div class="text-end d-none d-sm-block">
                    <div class="fw-bold text-dark">테스트 관리자 <span class="small text-muted font-monospace">[1000]</span></div>
                    <div class="small text-secondary">플랫폼개발팀 · 팀장</div>
                </div>
                <span class="badge badge-admin"><i class="bi bi-award me-1"></i>대표</span>
                <a href="#" class="btn btn-outline-danger btn-actions"><i class="bi bi-box-arrow-right me-1"></i>로그아웃</a>
            </div>
        </div>
    </nav>

    <div class="container pb-5">
        <div class="row align-items-center mb-4">
            <div class="col">
                <h4 class="fw-bold text-dark m-0">
                    <i class="bi bi-list-check me-2 text-primary"></i>전체 사원 관리 명부
                </h4>
                <p class="text-muted small m-0 mt-1">사원 이름을 누르면 상세 정보 및 관련 작업을 진행할 수 있습니다.</p>
            </div>
            <div class="col-auto">
                <button class="btn btn-primary d-flex align-items-center">
                    <i class="bi bi-person-plus me-2"></i> 사원 추가
                </button>
            </div>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="text-muted small">총 <span class="fw-bold text-primary">3</span>명</div>
            <div class="d-flex align-items-center gap-2">
                <label class="form-label small fw-bold m-0 text-secondary">보기 개수:</label>
                <select class="form-select form-select-sm" style="width: auto;">
                    <option selected>5개씩 보기</option>
                </select>
            </div>
        </div>

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
                    <tbody>
                        <tr>
                            <td class="fw-bold text-primary">#1000</td>
                            <td>테스트 관리자</td>
                            <td>플랫폼개발팀</td>
                            <td><span class="badge bg-light text-secondary border">백엔드개발</span></td>
                            <td>팀장</td>
                            <td>6,200</td>
                            <td><span class="badge badge-admin">대표</span></td>
                            <td class="font-monospace small">2025-01-02</td>
                        </tr>
                        <tr>
                            <td class="fw-bold text-primary">#1101</td>
                            <td>김코사</td>
                            <td>서비스기획팀</td>
                            <td><span class="badge bg-light text-secondary border">기획</span></td>
                            <td>대리</td>
                            <td>4,500</td>
                            <td><span class="badge badge-user">사원</span></td>
                            <td class="font-monospace small">2025-03-14</td>
                        </tr>
                        <tr>
                            <td class="fw-bold text-primary">#1204</td>
                            <td>박디자인</td>
                            <td>UX팀</td>
                            <td><span class="badge bg-light text-secondary border">디자인</span></td>
                            <td>사원</td>
                            <td>3,900</td>
                            <td><span class="badge badge-user">사원</span></td>
                            <td class="font-monospace small">2025-08-21</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="d-flex justify-content-center py-3 border-top">
                <nav aria-label="preview pagination">
                    <ul class="pagination mb-0">
                        <li class="page-item active"><span class="page-link">1</span></li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</body>
</html>
