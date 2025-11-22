@WebServlet("/admin/login")  // 映射到 /admin/login
public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 返回登录页面 HTML 内容
        response.setContentType("text/html");
        response.getWriter().println("<h1>Admin Login</h1>");
        response.getWriter().println("<form action='/admin/login' method='POST'>");
        response.getWriter().println("Username: <input type='text' name='username' /><br>");
        response.getWriter().println("Password: <input type='password' name='password' /><br>");
        response.getWriter().println("<input type='submit' value='Login' />");
        response.getWriter().println("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 简单的用户名和密码验证（可扩展为数据库查询）
        if ("admin".equals(username) && "password123".equals(password)) {
            // 验证通过，重定向到管理面板
            response.sendRedirect("/admin/dashboard");
        } else {
            // 验证失败，显示错误信息
            response.setContentType("text/html");
            response.getWriter().println("<h1>Invalid username or password</h1>");
            response.getWriter().println("<a href='/admin/login'>Go back</a>");
        }
    }
}
