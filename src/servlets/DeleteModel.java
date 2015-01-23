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

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import bean.Model;
import bean.User;
import dao.Dao;
import dao.impl.DaoImpl;

/**
 * Servlet implementation class DeleteModel
 */
@WebServlet("/deletemodel")
public class DeleteModel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext sc;
	private String realModelPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteModel() {
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
		Integer mid = Integer.parseInt(request.getParameter("mid"));
		Dao<Model> modelDao = new DaoImpl<Model>();
		try {
			Model model = modelDao.find(Model.class, mid);
			if (model == null) {
				result = 0;
				message = "删除失败！系统中未找到该模型文件的记录。";
			} else {
				modelDao.delete(model);
				File dir = new File(realModelPath + "m" + mid);
				FileUtils.deleteDirectory(dir);
				result = 1;
				message = "删除成功！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			message = "删除失败！服务器出现异常。";
		}
		json.put("result", result);
		json.put("message", message);
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(json.toString());
	}

}
