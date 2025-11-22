package com.youthvotesmatter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/admin/login")  // 映射路径为 /admin/login
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 打印请求方法和请求路径
        System.out.println("Received POST request to /admin/login");

        // 获取前端表单提交的数据
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 打印出提交的用户名和密码，帮助调试
        System.out.println("Received username: " + username);
        System.out.println("Received password: " + password);

        // 假设的管理员用户名和密码
        String adminUsername = "admin";
        String adminPassword = "password123";

        // 验证用户名和密码
        if (username != null && password != null) {
            // 打印用户名和密码验证结果
            System.out.println("Validating username and password...");

            if (username.equals(adminUsername) && password.equals(adminPassword)) {
                // 登录成功，返回成功消息
                System.out.println("Login successful!");

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Login Successful!");
            } else {
                // 登录失败，返回失败消息
                System.out.println("Invalid username or password.");

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid username or password.");
            }
        } else {
            // 如果用户名或密码为空，返回错误消息
            System.out.println("Username or password is empty!");

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Username or password cannot be empty.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 处理 GET 请求，如果有需要，也可以返回管理员登录页面或者其他内容
        System.out.println("Received GET request for /admin/login");
        response.getWriter().write("GET request for Admin Login");
    }
}
