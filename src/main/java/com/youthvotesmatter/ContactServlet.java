package com.youthvotesmatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import jakarta.mail.*;
import jakarta.mail.internet.*;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    // 你的前端页面（提交后跳转回这个页面并带上 ?success=1 / ?error=...）
    private static final String FRONTEND_URL = "https://youthvotesmatter.org/contact.html";

    // 简单的邮件格式校验（可按需放宽/收紧）
    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        addCors(request, response);

        String name = trimOrNull(request.getParameter("user_name"));
        String email = trimOrNull(request.getParameter("user_email"));
        String message = trimOrNull(request.getParameter("user_message"));

        // 基础校验（避免空值/超长）
        if (name == null || email == null || message == null ||
            name.isBlank() || email.isBlank() || message.isBlank() ||
            name.length() > 200 || email.length() > 320 || message.length() > 10000 ||
            !EMAIL_RE.matcher(email).matches()) {
            response.sendRedirect(FRONTEND_URL + "?error=1");
            return;
        }

        try {
            sendEmail(name, email, message);
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

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        addCors(req, resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // ✅ Gmail/通用 SMTP 发送（支持 587-STARTTLS / 465-SMTPS）
    private static void sendEmail(String name, String fromEmail, String userMessage) throws Exception {
        String host = getenvOrThrow("SMTP_HOST");          // e.g. smtp.gmail.com
        String port = getenvOrThrow("SMTP_PORT");          // "587" 或 "465"
        String username = getenvOrThrow("SMTP_USERNAME");  // 完整邮箱，如 xxx@gmail.com
        String password = getenvOrThrow("SMTP_PASSWORD");  // Gmail 16位应用密码
        String to = getenvOrThrow("MAIL_TO");              // 接收通知的邮箱
        boolean debug = "true".equalsIgnoreCase(System.getenv().getOrDefault("MAIL_DEBUG", "false"));

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");

        // 超时（毫秒）：避免长时间卡住
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // 强制现代 TLS 协议，避免老环境/中间代理导致握手失败
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        // 根据端口自动选择 TLS 策略
        if ("465".equals(port)) {
            // SMTPS（隐式 TLS）
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "false");
        } else {
            // 587/25 等 → STARTTLS
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.enable", "false");
        }

        // 若环境中意外走了 SOCKS 代理，可强制清除（确需代理时注释掉下面两行，改为使用 mail.smtp.socks.* 配置）
        System.clearProperty("socksProxyHost");
        System.clearProperty("socksProxyPort");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(debug); // 通过环境变量 MAIL_DEBUG 控制

        MimeMessage msg = new MimeMessage(session);

        // 发件人（显示名可自定义；第三个参数是字符集）
        msg.setFrom(new InternetAddress(username, "YouthVotesMatter", "UTF-8"));

        // 收件人：你的通知邮箱
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // 设置 Reply-To，便于在邮箱中直接回复给访客
        msg.setReplyTo(new Address[]{ new InternetAddress(fromEmail) });

        // 主题与正文
        msg.setSubject("New Contact Form Submission from " + name, "UTF-8");

        String content =
            "Name: " + name + "\n" +
            "Email: " + fromEmail + "\n\n" +
            "Message:\n" + userMessage + "\n";

        msg.setText(content, "UTF-8");

        // 发送
        Transport.send(msg);
    }

    // ✅ CORS：只允许你的正式站点；按需扩展允许的请求头
    private static void addCors(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");

        if ("https://youthvotesmatter.org".equals(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Vary", "Origin");
        }

        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        // 如需携带 Cookie，再打开下面一行，并确保 Allow-Origin 不是通配符
        // resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Max-Age", "86400");
    }

    // 工具方法：环境变量缺失时立刻抛错，避免“连接失败”类的二次迷惑
    private static String getenvOrThrow(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing env: " + key);
        }
        return v.trim();
    }

    private static String trimOrNull(String s) {
        return (s == null) ? null : s.trim();
    }
}
