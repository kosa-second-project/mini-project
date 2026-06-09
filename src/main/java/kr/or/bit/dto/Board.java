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
    private String writer;
    private String pwd;
    private String subject;
    private String content;
    private Date writedate;
    private int readnum;
    private String filename;
    private int filesize;
    private String homepage;
    private String email;
    private int refer;
    private int depth;
    private int step;
}
