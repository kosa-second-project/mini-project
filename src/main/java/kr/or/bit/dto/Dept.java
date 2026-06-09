package kr.or.bit.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dept {
    private int deptno;       // 부서 번호 (PK)
    private String deptname;  // 부서 이름
    private String loc;       // 부서 위치
}
