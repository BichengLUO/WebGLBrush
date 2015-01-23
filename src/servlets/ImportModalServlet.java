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
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import dao.Dao;
import dao.impl.DaoImpl;

import bean.Model;

/**
 * Servlet implementation class ImportModalServlet
 */
@WebServlet("/import")
public class ImportModalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String realModelPath;
	private ServletContext sc;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImportModalServlet() {
		super();
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
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
		String modelName = null;
		Integer userId = 0;
		boolean fileFound = false;
		try {
			items = upload.parseRequest(request);
			for (FileItem item : items) {
				if (item.isFormField()) {
					if (item.getFieldName().equals("model_name")) {
						modelName = item.getString("UTF-8");
					} else if (item.getFieldName().equals("uid")) {
						userId = Integer.parseInt(item.getString("UTF-8"));
					}
				} else {
					fileFound = true;
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
			result = 0;
			message = "服务器异常，模型保存失败！";
		}
		Model model = new Model();
		model.setModelName(modelName);
		model.setUserID(userId);
		Dao<Model> modelDao = new DaoImpl<Model>();
		try {
			modelDao.create(model);
			Iterator<FileItem> itr = items.iterator();
			int mid = model.getId();
			if (fileFound) {
				while (itr.hasNext()) {
					FileItem item = itr.next();
					if (item.isFormField()) {
						System.out.println("Post：" + item.getFieldName()
								+ ", Value：" + item.getString("UTF-8"));
					} else if (item.getName() != null && !item.getName().equals("")) {
						System.out.println("File Size：" + item.getSize());
						System.out.println("File Type：" + item.getContentType());
						System.out.println("File Name：" + item.getName());
						File file = new File(realModelPath + "m" + mid + "/",
								item.getName());
						File dir = new File(realModelPath + "m" + mid);
						if (!dir.exists()) {
							dir.mkdir();
						}
						item.write(file);
					}
				}
			} else {
				File srcfile = new File(sc.getRealPath("/") + "ressources/sphere.obj");
				File destfile = new File(realModelPath + "m" + mid + "/sphere.obj");
				File dir = new File(realModelPath + "m" + mid);
				if (!dir.exists()) {
					dir.mkdir();
				}
				FileUtils.copyFile(srcfile, destfile);
			}
			result = 1;
			message = "模型保存成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			message = "服务器异常，模型保存失败！";
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
