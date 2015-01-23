package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.Model;
import bean.User;

import dao.Dao;
import dao.impl.DaoImpl;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String avatarPath;
	private ServletContext sc;
	private String modelPath;
	private String realModelPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
	}

	public void init(ServletConfig config) {
		sc = config.getServletContext();
		avatarPath = sc.getInitParameter("avatar_path");
		modelPath = sc.getInitParameter("model_path");
		realModelPath = sc.getInitParameter("real_model_path");
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
		String password = request.getParameter("password");
		String uid = request.getParameter("uid");
		Dao<User> dao = new DaoImpl<User>();
		List<User> userList;
		int result = 0;
		String message = "";
		JSONObject json = new JSONObject();
		boolean loginFromUid = false;
		try {
			if (uid == null || uid.equals("")) {
				userList = dao.list("from User u where u.username='" + username
						+ "'", 0, 1);
			} else {
				userList = dao.list("from User u where u.id='" + uid + "'", 0,
						1);
				loginFromUid = true;
			}
			if (userList == null || userList.size() == 0) {
				result = 0;
				message = "用户不存在！";
			} else {
				User user = userList.get(0);
				if (loginFromUid || user.getPassword().equals(password)) {
					result = 1;
					json.put("result", result);
					json.put("uid", user.getId());
					json.put("avatar_url", avatarPath + "u" + user.getId());
					json.put("username", user.getUsername());
					json.put("nickname", user.getNickname());
					json.put("used_storage", 0);
					json.put("models", new JSONArray());
					Dao<Model> modelDao = new DaoImpl<Model>();
					List<Model> modelList = modelDao
							.list("from Model m where m.userID=" + user.getId());
					if (modelList != null) {
						double usedStorage = 0;
						for (Model model : modelList) {
							JSONObject modelJson = new JSONObject();
							File dir = new File(realModelPath + "m"
									+ model.getId());
							modelJson.put("model_id", model.getId());
							modelJson.put("model_name", model.getModelName());
							modelJson.put("thumbnail_url", modelPath + "m"
									+ model.getId() + "/thumb.png");
							modelJson.put("model_url",
									modelPath + "m" + model.getId() + "/"
											+ modelInFolder(dir));
							json.append("models", modelJson);
							usedStorage += folderSize(dir) / 1024.0 / 1024.0;
						}
						json.put("used_storage",
								String.format("%.3f", usedStorage));
					}

				} else {
					result = 0;
					message = "密码错误！";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			message = "获取用户信息失败！";
		} finally {
			if (result == 0) {
				try {
					json.put("result", result);
					json.put("message", message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(json.toString());
		}
	}

	public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}

	public static String modelInFolder(File directory) {
		String filename = null;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				String name = file.getName().toLowerCase();
				if (name.endsWith(".obj") || name.endsWith(".ply")
						|| name.endsWith(".stl")) {
					filename = name;
				}
			}
		}
		return filename;
	}
}
