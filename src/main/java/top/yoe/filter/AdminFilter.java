package top.yoe.filter;



import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AdminFilter",urlPatterns = "/admin/*")
public class AdminFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String origin = request.getHeader("Origin");
        if(origin == null){
            origin = request.getHeader("Referer");
        }

        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
        HttpSession session = request.getSession();
        String url = request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/"));
        if("/login".equals(url)){
            chain.doFilter(req,resp);
            return;
        }
        //判断是否登录
        if(session.getAttribute("adminID") == null){
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print("管理员尚未登录！");
            return;
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
