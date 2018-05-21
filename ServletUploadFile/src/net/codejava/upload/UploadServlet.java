package net.codejava.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY = "upload";
    private static final int THRESHOLD_SIZE = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 3;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 3;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");

        if (!ServletFileUpload.isMultipartContent(request)) {
            writer.println("Request does not contain upload file data");
            writer.flush();
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(THRESHOLD_SIZE);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        String uploadpath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadpath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            List<FileItem> formItems = upload.parseRequest(request);
            for (FileItem fileItem : formItems) {
                if (!fileItem.isFormField()) {
                    String fileName = new File(fileItem.getName()).getName();
                    String filePath = uploadpath + File.separator + fileName;
                    File storeFile = new File(filePath);
                    fileItem.write(storeFile);
                }
            }
            request.setAttribute("message", "Upload has been done successfully!");
        } catch (FileUploadException e) {
            request.setAttribute("message", "There was an error: " + e.getMessage());
            System.out.println("FileUploadException in net.codejava.upload.doPost() :: " + e);
        } catch (Exception e) {
            request.setAttribute("message", "There was an error: " + e.getMessage());
            System.out.println("Exception in net.codejava.upload.doPost() :: " + e);
        }
        getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
    }
}
