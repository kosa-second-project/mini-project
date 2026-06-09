package kr.or.bit.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;

//들어오는 모든 요청 걸러내기 
@WebFilter(
	    description = "어노테이션 활용 필터 적용하기",
	    urlPatterns = {"/*"},
	    initParams = {@WebInitParam(name="encoding" , value="UTF-8")}
	  )
public class Encoding extends HttpFilter implements Filter {
	
	
	//개발자가 만들어둔 코드 
	private String encoding;
       

    public Encoding() {
        super();
    }
    
    
	public void init(FilterConfig fConfig) throws ServletException {
		this.encoding = fConfig.getInitParameter("encoding");
	}

	public void destroy() {
		
	}


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		//AOP 구현 
		if(request.getCharacterEncoding()==null) {
			request.setCharacterEncoding(this.encoding);
		}
		
		 chain.doFilter(request, response);
	}



}
