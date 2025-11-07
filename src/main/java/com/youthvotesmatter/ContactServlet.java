package com.youthvotesmatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
        "https://youthvotesmatter.org",
        "https://www.youthvotesmatter.org"
    );

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(req, resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    addCors(request, response);
    response.setContentType("text/html; charset=UTF-8");
    response.getWriter().write(
        "<!doctype html><html><head><meta charset='utf-8'><title>Contact API</title></head><body>" +
        "<h2>Contact API 运行正常</h2>" +
        "<p>请使用 <code>POST /contact</code> 提交表单（字段：user_name, user_email, user_message）。</p>" +
        "</body></html>"
    );
}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        addCors(request, response);
        response.setContentType("text/html; charset=UTF-8");

        String name = request.getParameter("user_name");
        String email = request.getParameter("user_email");
        String message = request.getParameter("user_message");

        if (isBlank(name) || isBlank(email) || isBlank(message)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<h3>提交失败：所有字段必填。</h3>");
            return;
        }

        // TODO: 这里可扩展为：写入数据库 / 发送邮件 / 写日志
        System.out.println("Form -> name=" + name + " | email=" + email + " | message=" + message);

        response.getWriter().write("<h2>提交成功！</h2><p>谢谢你，"
                + escape(name) + "，我们已经收到你的消息。</p>");
    }

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }

    private static String escape(String s){
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void addCors(HttpServletRequest req, HttpServletResponse resp){
        String origin = req.getHeader("Origin");
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Vary", "Origin");
        }
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
    }
}
