package com.youthvotesmatter;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/AdminServlet")  // 使用 @WebServlet 注解来映射 Servlet 的 URL 路径
public class AdminServlet extends HttpServlet {

    // 处理 POST 请求，用于表单提交的用户名和密码验证
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取表单提交的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 假设的正确用户名和密码
        String correctUsername = "admin";
        String correctPassword = "password123";

        // 验证用户名和密码
        if (correctUsername.equals(username) && correctPassword.equals(password)) {
            // 验证成功，跳转到后台管理页面
            response.sendRedirect("admin-dashboard.html");  // 登录成功，重定向到后台页面
        } else {
            // 验证失败，返回错误信息
            response.setContentType("text/html");
            response.getWriter().println("<h1>Invalid username or password</h1>");
            response.getWriter().println("<a href='/admin.html'>Go back</a>");
        }
    }

    // 可选：处理 GET 请求，用于显示登录表单页面（如果需要）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 你可以在这里处理 GET 请求，或者返回一个 HTML 页面
        response.setContentType("text/html");
        response.getWriter().println("<h1>Please log in</h1>");
        // 如果需要返回表单页面，可以直接输出 HTML 或重定向到 `admin.html`
    }
}
