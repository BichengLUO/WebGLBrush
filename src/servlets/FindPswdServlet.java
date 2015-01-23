package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import bean.User;
import dao.Dao;
import dao.impl.DaoImpl;

/**
 * Servlet implementation class FindPswdServlet
 */
@WebServlet("/findpw")
public class FindPswdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String fromAddress;
	private String password;
	private String smtpHost;
	private ServletContext sc;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FindPswdServlet() {
		super();
	}

	public void init(ServletConfig config) {
		sc=config.getServletContext();
		fromAddress = sc.getInitParameter("email_address");
		password = sc.getInitParameter("email_password");
		smtpHost = sc.getInitParameter("smtp_host");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		JSONObject json = new JSONObject();
		int result = 0;
		String message = "";
		try {
			Dao<User> userDao = new DaoImpl<User>();
			List<User> userList = userDao.list("from User u where u.username='"
					+ username + "'", 0, 1);
			if (userList == null || userList.size() == 0) {
				result = 0;
				message = "用户名未被注册！";
			} else {
				User user = userList.get(0);
				sendEmail(user);
				result = 1;
				message = "已经向您的邮箱发送了密码！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			message = "找回密码失败，服务器出现异常！";
		} finally {
			try {
				json.put("result", result);
				json.put("message", message);
				response.setHeader("Content-type", "text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(json.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendEmail(User user) throws MessagingException {
		String toAddress = user.getUsername();
		String pw = user.getPassword();
		String subject = "WebGLBrush找回密码";
		String text = "您的密码为：\n" + pw;
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", smtpHost);
		properties.setProperty("mail.smtp.auth", "true");
		Authenticator auth = new MyAuthenticator(fromAddress, password);
		Session session = Session.getDefaultInstance(properties, auth);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromAddress));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(
				toAddress));
		message.setSubject(subject);
		message.setText(text);
		Transport.send(message);
	}

	class MyAuthenticator extends Authenticator {
		private String user;
		private String pw;

		public MyAuthenticator(String user, String pw) {
			this.user = user;
			this.pw = pw;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pw);
		}
	}

}
