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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        addCors(request, response);
        response.setContentType("text/html; charset=UTF-8");

        String name = request.getParameter("user_name");
        String email = request.getParameter("user_email");
        String message = request.getParameter("user_message");

        // 简单校验
        if (name == null || email == null || message == null || name.isBlank() || email.isBlank() || message.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<h3>All fields are required.</h3>");
            return;
        }

        // 发送邮件
        try {
            sendEmail(name, email, message);
            response.getWriter().write(
                "<html><body>" +
                "<h2>Message Submitted Successfully!</h2>" +
                "<p>Thank you, " + escape(name) + ". Your message has been received.</p>" +
                "<p>We will get back to you shortly.</p>" +
                "</body></html>"
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("<h3>Failed to send email. Please try again later.</h3>");
        }
    }

    // 健康检查（GET）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addCors(request, response);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<h2>Contact API is running</h2>");
    }

    // 邮件发送函数
    private static void sendEmail(String name, String fromEmail, String userMessage) throws Exception {
        String host = System.getenv("SMTP_HOST");
        String port = System.getenv("SMTP_PORT");
        String username = System.getenv("SMTP_USERNAME");
        String password = System.getenv("SMTP_PASSWORD");
        String to = System.getenv("MAIL_TO");
        boolean ssl = Boolean.parseBoolean(System.getenv("SMTP_SSL"));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");    // 465
        } else {
            props.put("mail.smtp.starttls.enable", "true"); // 587
        }

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, "YouthVotesMatter"));
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setReplyTo(new Address[]{ new InternetAddress(fromEmail) });

        msg.setSubject("New Contact Form Submission from " + name, "UTF-8");

        String content =
                "Name: " + name + "\n" +
                "Email: " + fromEmail + "\n\n" +
                "Message:\n" + userMessage + "\n";

        msg.setText(content, "UTF-8");

        Transport.send(msg);
    }

    // 工具函数
    private static void addCors(HttpServletRequest req, HttpServletResponse resp){
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
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(req, resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private static String escape(String s){
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
