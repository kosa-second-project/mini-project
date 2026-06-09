package kr.or.bit.dto;

import java.util.Date;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private int idx;
    private int empno;
    private String subject;
    private String content;
    private Date writedate;
    private int readnum;
    private int refer;
    private int depth;
    private int step;
    private boolean deleted;
    private float lat; // 위도
    private float lng; // 경도
}