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
