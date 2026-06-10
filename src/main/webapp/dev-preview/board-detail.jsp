<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="kr.or.bit.dto.Board" %>
<%
    Board previewBoard = Board.builder()
        .idx(101)
        .empno(1000)
        .subject("FolioOne 디자인 확인용 게시글")
        .content("로그인이나 DB 연결 없이 게시판 상세 화면의 전역 테마, 본문, 버튼, 댓글 영역 배치를 확인하기 위한 임시 데이터입니다.\n\n이 페이지는 dev-preview 전용입니다.")
        .writedate(new Date())
        .readnum(12)
        .deleted(false)
        .build();
    request.setAttribute("idx", previewBoard.getIdx());
    request.setAttribute("board", previewBoard);
    request.setAttribute("replyList", new ArrayList<>());
    request.getRequestDispatcher("/WEB-INF/views/board/board_detail.jsp").forward(request, response);
%>
