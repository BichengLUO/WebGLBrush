package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class FileNameById
 */
@WebServlet("/filename")
public class FileNameById extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext sc;
	private String realModelPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileNameById() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void init(ServletConfig config) {
		sc = config.getServletContext();
		realModelPath = sc.getInitParameter("real_model_path");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int result = 0;
		String message = "";
		JSONObject json = new JSONObject();
		String mid = request.getParameter("mid");
		File dir = new File(realModelPath + "m" + mid);
		if (!dir.exists()) {
			result = 0;
			message = "系统未找到模型记录，查询模型地址失败！";
		} else {
			String filename = LoginServlet.modelInFolder(dir);
			if (filename == null || filename.equals("")) {
				result = 0;
				message = "服务器异常，查询模型地址失败！";
			} else {
				result = 1;
				json.put("filename", filename);
			}
		}
		json.put("result", result);
		json.put("message", message);
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(json.toString());
	}

}
