<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg profile-bar py-3 shadow-sm mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold d-flex align-items-center text-primary" href="${pageContext.request.contextPath}/Main.emp">
            <i class="bi bi-people-fill me-2"></i> EMP Portal
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#appHeaderNavbar"
            aria-controls="appHeaderNavbar" aria-expanded="false" aria-label="메뉴 열기">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse justify-content-end" id="appHeaderNavbar">
            <div class="d-flex align-items-center gap-3">
                <c:choose>
                    <c:when test="${not empty sessionScope.loginUser}">
                        <div class="text-end d-none d-sm-block">
                            <div class="fw-bold text-dark">
                                <c:out value="${sessionScope.loginUser.ename}" />
                                <span class="small text-muted font-monospace">[<c:out value="${sessionScope.loginUser.empno}" />]</span>
                            </div>
                            <div class="small text-secondary">
                                <c:out value="${sessionScope.loginUser.deptname}" /> · <c:out value="${sessionScope.loginUser.position}" />
                            </div>
                        </div>
                        <span class="badge ${sessionScope.loginUser.role eq 'ADMIN' ? 'badge-admin' : 'badge-user'}">
                            <c:choose>
                                <c:when test="${sessionScope.loginUser.role eq 'ADMIN'}"><i class="bi bi-award me-1"></i>관리자</c:when>
                                <c:otherwise><i class="bi bi-person me-1"></i>사원</c:otherwise>
                            </c:choose>
                        </span>
                        <a href="${pageContext.request.contextPath}/Logout.emp" class="btn btn-outline-danger btn-actions">
                            <i class="bi bi-box-arrow-right me-1"></i>로그아웃
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/Login.emp" class="btn btn-primary btn-actions">
                            <i class="bi bi-box-arrow-in-right me-1"></i>로그인
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
