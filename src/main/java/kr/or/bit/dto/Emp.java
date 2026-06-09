package kr.or.bit.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emp {
    private int seq;           // 시퀀스 자동 증가 PK
    private int empno;         // 사원 번호 (사번)
    private String ename;      // 사원 이름
    private String job;        // 직무 (예: 개발, 기획, 인사 등)
    private String position;   // 직급 (예: 사원, 대리, 과장 등)
    private int mgr;           // 직속 상사의 사번
    private String hiredate;   // 입사일
    private int sal;           // 급여
    private int deptno;        // 부서 번호 (FK)
    private String deptname;   // 부서 이름 (JOIN 조회용)
    private String pwd;        // 암호화된 비밀번호
    private String role;       // 권한 (ADMIN, USER)
}
