import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class AdminServlet extends HttpServlet {
    // 假设的管理员用户名和密码
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 验证管理员用户名和密码
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            // 登录成功
            response.getWriter().write("登录成功");
        } else {
            // 登录失败
            response.getWriter().write("登录失败，用户名或密码错误");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 返回登录表单
        response.setContentType("text/html");
        response.getWriter().write("<html><body>");
        response.getWriter().write("<h2>管理员登录</h2>");
        response.getWriter().write("<form method='POST'>");
        response.getWriter().write("用户名: <input type='text' name='username'><br>");
        response.getWriter().write("密码: <input type='password' name='password'><br>");
        response.getWriter().write("<input type='submit' value='登录'>");
        response.getWriter().write("</form>");
        response.getWriter().write("</body></html>");
    }
}
