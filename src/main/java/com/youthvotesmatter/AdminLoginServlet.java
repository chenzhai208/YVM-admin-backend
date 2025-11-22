import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/admin/login")  // 映射路径为 /admin/login
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取前端表单提交的数据
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 假设的管理员用户名和密码（你可以根据需要改成从数据库中读取）
        String adminUsername = "admin";
        String adminPassword = "password123";

        // 设置响应的Content-Type为文本
        response.setContentType("text/plain");

        // 验证用户名和密码
        if (username != null && password != null) {
            if (username.equals(adminUsername) && password.equals(adminPassword)) {
                // 登录成功，返回成功消息
                response.getWriter().write("Login Successful!");
            } else {
                // 登录失败，返回失败消息
                response.getWriter().write("Invalid username or password.");
            }
        } else {
            // 如果用户名或密码为空，返回错误消息
            response.getWriter().write("Username or password cannot be empty.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 处理 GET 请求时，可能会返回一个简单的欢迎页面或登录页面
        response.setContentType("text/html");
        response.getWriter().write("<html><body><h1>Admin Login</h1><form method='POST' action='/admin/login'><label>Username:</label><input type='text' name='username'><br><label>Password:</label><input type='password' name='password'><br><input type='submit' value='Login'></form></body></html>");
    }
}
