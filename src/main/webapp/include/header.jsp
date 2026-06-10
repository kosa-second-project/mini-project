<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${empty param.pageTitle ? 'EMP Portal' : param.pageTitle}" /></title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Poppins:wght@400;500;600;700&family=Raleway:wght@500;600;700;800&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/folioone-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/emp.css">
    <c:if test="${not empty param.pageCss}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}${param.pageCss}">
    </c:if>
    <c:if test="${not empty param.pageCss2}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}${param.pageCss2}">
    </c:if>
</head>
