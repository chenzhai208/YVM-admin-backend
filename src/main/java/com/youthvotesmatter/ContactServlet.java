package com.youthvotesmatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    // 可选：也可用环境变量 FRONTEND_URL 覆盖
    private static final String DEFAULT_FRONTEND_URL = "https://youthvotesmatter.org/contact.html";

    private static final Pattern EMAIL_RE =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        addCors(request, response);

        String name = trimOrNull(request.getParameter("user_name"));
        String email = trimOrNull(request.getParameter("user_email"));
        String message = trimOrNull(request.getParameter("user_message"));

        // 基础校验
        if (name == null || email == null || message == null ||
            name.isBlank() || email.isBlank() || message.isBlank() ||
            name.length() > 200 || email.length() > 320 || message.length() > 10000 ||
            !EMAIL_RE.matcher(email).matches()) {

            redirectWith(response, false, "1");
            return;
        }

        try {
            sendEmailViaBrevo(name, email, message);
            redirectWith(response, true, null);
        } catch (Exception e) {
            e.printStackTrace();
            redirectWith(response, false, "2");
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

    // ======================
    // Brevo 发送邮件（HTTP API）
    // ======================
    private static void sendEmailViaBrevo(String name, String fromEmail, String userMessage) throws Exception {
        String apiKey    = getenvOrThrow("BREVO_API_KEY");
        String toEmail   = getenvOrThrow("MAIL_TO");     // 收件人（你自己）
        String fromAddr  = getenvOrThrow("FROM_EMAIL");  // 你在 Brevo 验证过的发件邮箱

        String subject = "New Contact Form Submission from " + name;
        String textContent =
                "Name: " + name + "\n" +
                "Email: " + fromEmail + "\n\n" +
                "Message:\n" + userMessage + "\n";

        // 构造 Brevo API JSON
        // 端点：POST https://api.brevo.com/v3/smtp/email
        String json = "{"
                + "\"sender\":{\"email\":\"" + jsonEscape(fromAddr) + "\",\"name\":\"YouthVotesMatter\"},"
                + "\"to\":[{\"email\":\"" + jsonEscape(toEmail) + "\"}],"
                + "\"replyTo\":{\"email\":\"" + jsonEscape(fromEmail) + "\"},"
                + "\"subject\":\"" + jsonEscape(subject) + "\","
                + "\"textContent\":\"" + jsonEscape(textContent) + "\""
                + "}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        int code = resp.statusCode();

        if (code < 200 || code >= 300) {
            throw new IllegalStateException("Brevo API error: " + code + " - " + resp.body());
        }
    }

    // ======================
    // CORS & 跳转 & 工具
    // ======================
    private static void addCors(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        if ("https://youthvotesmatter.org".equals(origin) ||
            "https://www.youthvotesmatter.org".equals(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Vary", "Origin");
        }
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        // 如需携带 Cookie，可启用下行并确保 Allow-Origin 不是 *
        // resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Max-Age", "86400");
    }

    private void redirectWith(HttpServletResponse response, boolean success, String errorCode) throws IOException {
        String front = System.getenv("FRONTEND_URL");
        if (front == null || front.isBlank()) {
            front = DEFAULT_FRONTEND_URL;
        }
        String target = front + (success ? "?success=1" : "?error=" + (errorCode == null ? "2" : errorCode));
        response.sendRedirect(target);
    }

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

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int)c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
