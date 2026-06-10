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
public class Reply {
    private int no;
    private int empno;
    private String ename;
    private String deptname;
    private String content;
    private Date writedate;
    private int idx_fk;
    private int refer;
    private int depth;
    private int step;
    private boolean deleted;
}
