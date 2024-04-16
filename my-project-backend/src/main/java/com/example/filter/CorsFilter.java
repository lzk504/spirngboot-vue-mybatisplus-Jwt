package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * 跨域配置过滤器，仅处理跨域，添加跨域响应头
 */
@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        this.addCorsFilter(request,response);
        chain.doFilter(request, response);
    }
    /**
     * 添加所有跨域相关响应头
     * @param request 请求
     * @param response 响应
     */
    private void addCorsFilter(HttpServletRequest request,
                               HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Methods", request.getMethod());
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    }
}
