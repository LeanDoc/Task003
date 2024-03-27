package example.servlet;

import com.github.dockerjava.api.exception.NotFoundException;
import servlet.EmployeeServlet;
import servlet.dto.EmployeeIncomingDto;
import servlet.dto.EmployeeUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.EmployeeService;
import service.impl.EmployeeServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.SQLException;

@ExtendWith(
        MockitoExtension.class
)
class EmployeeServletTest {
    private static EmployeeService mockEmployeeService;
    @InjectMocks
    private static EmployeeServlet employeeServlet;
    private static EmployeeServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(EmployeeService mock) {
        try {
            Field instance = EmployeeServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (EmployeeServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockEmployeeService = Mockito.mock(EmployeeService.class);
        setMock(mockEmployeeService);
        employeeServlet = new EmployeeServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = EmployeeServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockEmployeeService);
    }

    @Test
    void doGetAll() throws IOException, SQLException, exception.NotFoundException {
        Mockito.doReturn("employee/all").when(mockRequest).getPathInfo();

        employeeServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockEmployeeService).findAll();
    }

    @Test
    void doGetById() throws IOException, SQLException, exception.NotFoundException {
        Mockito.doReturn("employee/2").when(mockRequest).getPathInfo();

        employeeServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockEmployeeService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, SQLException, exception.NotFoundException {
        Mockito.doReturn("employee/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockEmployeeService).findById(100L);

        employeeServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("employee/2q").when(mockRequest).getPathInfo();

        employeeServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, SQLException, exception.NotFoundException {
        Mockito.doReturn("employee/2").when(mockRequest).getPathInfo();

        employeeServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockEmployeeService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws IOException, SQLException, exception.NotFoundException {
        Mockito.doReturn("employee/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockEmployeeService).delete(100L);

        employeeServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockEmployeeService).delete(100L);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("employee/a100").when(mockRequest).getPathInfo();

        employeeServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException, SQLException, exception.NotFoundException {
        String expectedFirstname = "New first";
        String expectedLastname = "New last";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"firstName\":\"" + expectedFirstname + "\"" +
                         ",\"lastName\":\"" + expectedLastname + "\"" +
                         ", \"position\":{\"id\":4,\"name\":\"Администратор\"} " +
                         "}",
                null
        ).when(mockBufferedReader).readLine();

        employeeServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<EmployeeIncomingDto> argumentCaptor = ArgumentCaptor.forClass(EmployeeIncomingDto.class);
        Mockito.verify(mockEmployeeService).save(argumentCaptor.capture());

        EmployeeIncomingDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedFirstname, result.getFirstName());
        Assertions.assertEquals(expectedLastname, result.getLastName());
    }

    @Test
    void doPut() throws IOException, SQLException, exception.NotFoundException {
        String expectedFirstname = "New first";
        String expectedLastname = "New last";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\": 1," +
                         "\"firstName\":\"" + expectedFirstname + "\"" +
                         ",\"lastName\":\"" + expectedLastname + "\"" +
                         ", \"position\":{\"id\":4}, " +
                         "\"phoneNumberList\": [{ \"id\": 1,\"number\": \"+1(123)123 1111\"}]," +
                         "\"subdivisionList\": [{\"id\": 2}]" +
                         "}",
                null
        ).when(mockBufferedReader).readLine();

        employeeServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<EmployeeUpdateDto> argumentCaptor = ArgumentCaptor.forClass(EmployeeUpdateDto.class);
        Mockito.verify(mockEmployeeService).update(argumentCaptor.capture());

        EmployeeUpdateDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedFirstname, result.getFirstName());
        Assertions.assertEquals(expectedLastname, result.getLastName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        employeeServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}