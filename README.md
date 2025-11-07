# YouthVotesMatter Contact Backend (Servlet)

Java Servlet 后端，用于接收 https://youthvotesmatter.org 的联系表单。
- 路由：`POST /contact`
- 技术：Jakarta Servlet 6 + Tomcat 10 + Java 17
- 跨域：仅允许 `https://youthvotesmatter.org` 与 `https://www.youthvotesmatter.org`

## 一键本地构建
```bash
mvn -q -DskipTests clean package
```

生成 `target/contact.war`。

## 本地运行（Docker + Tomcat）
```bash
docker build -t yvm-contact:latest .
docker run -d --name yvm -p 8080:8080 yvm-contact:latest
# 测试：
curl -X POST -H "Origin: https://youthvotesmatter.org"       -d "user_name=Alice&user_email=a@b.com&user_message=Hello"       http://localhost:8080/contact
```

## Render 部署步骤
1. 将本仓库推到 GitHub（比如 `yvm-contact-backend`）。
2. 登录 https://render.com → **New → Web Service** → 选择该仓库。
3. Environment 选择 **Docker**（自动识别 Dockerfile）。
4. 创建后等待构建完成，会得到 `https://xxxx.onrender.com` 域名。

### 绑定自定义域 `api.youthvotesmatter.org`
1. 在 Render 服务页的 **Custom Domains** 添加 `api.youthvotesmatter.org`。
2. 到域名 DNS 添加 **CNAME**：
   - 主机名：`api`
   - 值：Render 提示的目标（通常为 `your-service.onrender.com`）。
3. 等待 Render 验证并签发 HTTPS 证书。成功后你的后端地址为：
   ```
   https://api.youthvotesmatter.org/contact
   ```

## 前端表单配置
在你的静态站点 HTML 中，将表单 action 指向后端：
```html
<form id="contact-form" action="https://api.youthvotesmatter.org/contact" method="POST" novalidate>
```

## 日志和排错
- Render 仪表盘 → Logs 可查看后端输出（含表单内容）。
- 确认跨域：白名单仅允许 `https://youthvotesmatter.org` 与 `https://www.youthvotesmatter.org`。
- 若 404：确保 Servlet 映射 `@WebServlet("/contact")`，且 Docker 构建的 WAR 放置为 `ROOT.war`。
