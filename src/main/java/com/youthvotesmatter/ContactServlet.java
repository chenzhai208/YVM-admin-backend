package com.youthvotesmatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.*;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    private static final String FRONTEND_URL = "https://youthvotesmatter.org/contact.html";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        addCors(request, response);

        String name = request.getParameter("user_name");
        String email = request.getParameter("user_email");
        String message = request.getParameter("user_message");

        // 简单校验
        if (name == null || email == null || message == null ||
                name.isBlank() || email.isBlank() || message.isBlank()) {
            response.sendRedirect(FRONTEND_URL + "?error=1");
            return;
        }

        try {
            // ✅ 发送邮件到你的邮箱
            sendEmail(name, email, message);

            // ✅ 提交成功，跳回 GitHub Pages 的 contact.html
            response.sendRedirect(FRONTEND_URL + "?success=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(FRONTEND_URL + "?error=2");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        addCors(request, response);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write("Contact API is running");
    }

    // ✅ Gmail SMTP 邮件发送函数
    private static void sendEmail(String name, String fromEmail, String userMessage) throws Exception {
        String host = System.getenv("SMTP_HOST");
        String port = System.getenv("SMTP_PORT");
        String username = System.getenv("SMTP_USERNAME"); // Gmail
        String password = System.getenv("SMTP_PASSWORD"); // 16位应用密码
        String to = System.getenv("MAIL_TO");             // 收件邮箱

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  // Gmail 必备
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, "YouthVotesMatter"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setReplyTo(new Address[]{ new InternetAddress(fromEmail) });

        msg.setSubject("New Contact Form Submission from " + name, "UTF-8");

        String content =
                "Name: " + name + "\n" +
                "Email: " + fromEmail + "\n\n" +
                "Message:\n" + userMessage + "\n";

        msg.setText(content, "UTF-8");

        Transport.send(msg);
    }

    // ✅ 正确 CORS 设置
    private static void addCors(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");

        if ("https://youthvotesmatter.org".equals(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Vary", "Origin");
        }

        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        addCors(req, resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
