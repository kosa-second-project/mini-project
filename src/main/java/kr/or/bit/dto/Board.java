package kr.or.bit.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private int idx;
    private int empno;
    private String ename;
    private String deptname;
    private String subject;
    private String content;
    private Date writedate;
    private int readnum;
    private int refer;
    private int depth;
    private int step;
    private boolean deleted;
    private int replyCount;
}
