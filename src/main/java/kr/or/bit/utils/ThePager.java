package kr.or.bit.utils;

public class ThePager {
     
     private int pageSize;
     private int pagerSize;
     private int dataCount;
     private int currentPage;
     private int pageCount;
     private String linkUrl;
     
     public ThePager(int dataCount, int currentPage, int pageSize, int pagerSize, String linkUrl) {
         this.linkUrl = linkUrl;
         this.dataCount = dataCount;
         this.pageSize = pageSize;
         this.pagerSize = pagerSize;
         this.currentPage = currentPage;  
         this.pageCount = (dataCount / pageSize) + ((dataCount % pageSize) > 0 ? 1 : 0); 
     }
     
     @Override
     public String toString() {
         StringBuilder linkString = new StringBuilder();
         
         if (currentPage > 1) {
             linkString.append(String.format("[<a href='%s?cp=1&ps=%d'>처음</a>]", linkUrl, pageSize));
             linkString.append("&nbsp;&nbsp;");
             linkString.append(String.format("[<a href='%s?cp=%d&ps=%d'>이전</a>]", linkUrl, currentPage - 1, pageSize));
             linkString.append("&nbsp;");
         }
         
         int pagerBlock = (currentPage - 1) / pagerSize;
         int start = (pagerBlock * pagerSize) + 1;
         int end = start + pagerSize;
         for (int i = start; i < end; i++) {
             if (i > pageCount) break;
             linkString.append("&nbsp;");
             if (i == currentPage) {
                 linkString.append(String.format("[%d]", i));
             } else { 
                 linkString.append(String.format("<a href='%s?cp=%d&ps=%d'>%d</a>", linkUrl, i, pageSize, i));
             }
             linkString.append("&nbsp;");
         }
         
         if (currentPage < pageCount) {
             linkString.append("&nbsp;");
             linkString.append(String.format("[<a href='%s?cp=%d&ps=%d'>다음</a>]", linkUrl, currentPage + 1, pageSize));
             linkString.append("&nbsp;&nbsp;");
             linkString.append(String.format("[<a href='%s?cp=%d&ps=%d'>마지막</a>]", linkUrl, pageCount, pageSize));
         }
         
         return linkString.toString();
     }
}