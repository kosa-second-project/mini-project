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
    private String writer;
    private String userid;
    private String pwd;
    private String content;
    private Date writedate;
    private int idx_fk;
}
