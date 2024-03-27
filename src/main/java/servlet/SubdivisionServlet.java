package servlet;


import com.github.dockerjava.api.exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import service.SubdivisionService;
import service.impl.SubdivisionServiceImpl;
import servlet.dto.SubdivisionIncomingDto;
import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;


@WebServlet(urlPatterns = {"/subdivision/*"})
public class SubdivisionServlet extends HttpServlet {
    private final transient SubdivisionService subdivisionService = SubdivisionServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public SubdivisionServlet() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            if ("all".equals(pathPart[1])) {
                List<SubdivisionOutGoingDto> subdivisionDtoList = subdivisionService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(subdivisionDtoList);
            } else {
                Long subdivisionId = Long.parseLong(pathPart[1]);
                SubdivisionOutGoingDto subdivisionDto = subdivisionService.findById(subdivisionId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(subdivisionDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long subdivisionId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if (req.getPathInfo().contains("/deleteEmployee/")) {
                if ("deleteEmployee".equals(pathPart[2])) {
                    Long employeeId = Long.parseLong(pathPart[3]);
                    subdivisionService.deleteEmployeeFromSubdivision(subdivisionId, employeeId);
                }
            } else {
                subdivisionService.delete(subdivisionId);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request. ";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = null;
        Optional<SubdivisionIncomingDto> subdivisionResponse;
        try {
            subdivisionResponse = Optional.ofNullable(objectMapper.readValue(json, SubdivisionIncomingDto.class));
            SubdivisionIncomingDto subdivision = subdivisionResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(subdivisionService.save(subdivision));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect subdivision Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<SubdivisionUpdateDto> subdivisionResponse;
        try {
            if (req.getPathInfo().contains("/addEmployee/")) {
                String[] pathPart = req.getPathInfo().split("/");
                if (pathPart.length > 3 && "addEmployee".equals(pathPart[2])) {
                    Long subdivisionId = Long.parseLong(pathPart[1]);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Long employeeId = Long.parseLong(pathPart[3]);
                    subdivisionService.addEmployeeToSubdivision(subdivisionId, employeeId);
                }
            } else {
                subdivisionResponse = Optional.ofNullable(objectMapper.readValue(json, SubdivisionUpdateDto.class));
                SubdivisionUpdateDto subdivisionUpdateDto = subdivisionResponse.orElseThrow(IllegalArgumentException::new);
                subdivisionService.update(subdivisionUpdateDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect subdivision Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}
