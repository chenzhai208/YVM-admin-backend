package com.youthvotesmatter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class AdminServlet extends HttpServlet {

    // 处理 POST 请求
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取表单提交的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 假设正确的用户名和密码
        String correctUsername = "admin";
        String correctPassword = "password123";

        // 验证用户名和密码
        if (correctUsername.equals(username) && correctPassword.equals(password)) {
            // 登录成功，跳转到后台页面或其他成功页面
            response.sendRedirect("admin-dashboard.html");  // 例如：跳转到后台页面
        } else {
            // 登录失败，返回错误信息
            response.setContentType("text/html");
            response.getWriter().println("<h1>Invalid username or password</h1>");
            response.getWriter().println("<a href='/admin.html'>Go back</a>");
        }
    }
}
