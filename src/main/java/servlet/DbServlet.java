package servlet;

import connection.ConnectionManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.InitSqlScheme;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;


@WebServlet(urlPatterns = {"/db"})
public class DbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder("Create data base:\n\n");
        sb.append("New version\n");

        ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();
        sb.append("Create schema\n");
        try {
            InitSqlScheme.initSqlScheme(connectionManager);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        sb.append("Create data\n");
        try {
            InitSqlScheme.initSqlData(connectionManager);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = resp.getWriter();
        printWriter.write(sb.toString());
        printWriter.flush();
    }
}
