package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.*;

import dao.Dao;
import dao.impl.DaoImpl;

import bean.User;

/**
 * Servlet implementation class RegisterServlet
 * 
 * @author Windsor
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext sc;
	private String realAvatarPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
		super();
	}

	public void init(ServletConfig config) {
		sc = config.getServletContext();
		realAvatarPath = sc.getInitParameter("real_avatar_path");
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
		int result = 0;
		String message = "";
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items = null;
		String username = null;
		String password = null;
		String nickname = null;
		try {
			items = upload.parseRequest(request);
			for (FileItem item : items) {
				if (item.isFormField()) {
					if (item.getFieldName().equals("username")) {
						username = item.getString("UTF-8");
					} else if (item.getFieldName().equals("password")) {
						password = item.getString("UTF-8");
					} else if (item.getFieldName().equals("nickname")) {
						nickname = item.getString("UTF-8");
					}
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
			result = 0;
			message = "注册失败！服务器出现异常。";
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setNickname(nickname);
		Dao<User> userDao = new DaoImpl<User>();
		try {
			List<User> userList = userDao.list("from User u where u.username='"
					+ username + "'");
			if (userList != null && userList.size() != 0) {
				result = 0;
				message = "注册失败！此用户名已被注册过了。";
			} else {
				userDao.create(user);
				Iterator<FileItem> itr = items.iterator();
				while (itr.hasNext()) {
					FileItem item = itr.next();
					if (item.isFormField()) {
						System.out.println("Post：" + item.getFieldName()
								+ ", Value：" + item.getString("UTF-8"));
					} else if (item.getName() != null
							&& !item.getName().equals("")) {
						System.out.println("File Size：" + item.getSize());
						System.out
								.println("File Type：" + item.getContentType());
						System.out.println("File Name：" + item.getName());
						File file = new File(realAvatarPath, "u" + user.getId());
						item.write(file);
					}
				}
				result = 1;
				message = "注册成功！";
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			result = 0;
			message = "上传文件失败！";
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			message = "注册失败！服务器出现异常。";
		} finally {
			JSONObject json = new JSONObject();
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
}
