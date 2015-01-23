package servlets;

import java.io.File;
import java.io.FileOutputStream;
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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class SaveModelServlet
 */
@WebServlet("/save")
public class SaveModelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext sc;
	private String realModelPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveModelServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		sc = config.getServletContext();
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
		int result = 0;
		String message = "";
		JSONObject json = new JSONObject();
		String mid = request.getParameter("mid");
		String data = request.getParameter("data");
		File dir = new File(realModelPath + "m" + mid);
		if (!dir.exists()) {
			result = 0;
			message = "系统未找到模型记录，保存模型失败！";
		} else {
			String filename = LoginServlet.modelInFolder(dir);
			if (filename == null || filename.equals("")) {
				result = 0;
				message = "服务器异常，保存模型失败！";
			} else {
				File file = new File(realModelPath + "m" + mid, filename);
				if (file.delete()) {
					file = new File(realModelPath + "m" + mid, "model.obj");
					PrintWriter pw = new PrintWriter(new FileOutputStream(file));
					pw.write(data);
					pw.close();
					result = 1;
					message = "保存模型成功！";
				} else {
					result = 0;
					message = "服务器异常，无法更新原有模型！";
				}
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
