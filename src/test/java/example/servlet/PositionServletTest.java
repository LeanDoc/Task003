package example.servlet;

import com.github.dockerjava.api.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.PositionService;
import service.impl.PositionServiceImpl;
import servlet.PositionServlet;
import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.SQLException;

@ExtendWith(
        MockitoExtension.class
)
class PositionServletTest {
    private static PositionService mockPositionService;
    @InjectMocks
    private static PositionServlet positionServlet;
    private static PositionServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(PositionService mock) {
        try {
            Field instance = PositionServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (PositionServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockPositionService = Mockito.mock(PositionService.class);
        setMock(mockPositionService);
        positionServlet = new PositionServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = PositionServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockPositionService);
    }

    @Test
    void doGetAll() throws IOException, SQLException {
        Mockito.doReturn("position/all").when(mockRequest).getPathInfo();
        positionServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockPositionService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException, SQLException {
        Mockito.doReturn("position/2").when(mockRequest).getPathInfo();
        positionServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockPositionService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException, SQLException {
        Mockito.doReturn("position/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockPositionService).findById(100L);
        positionServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doDelete() throws IOException, NotFoundException, SQLException {
        Mockito.doReturn("position/2").when(mockRequest).getPathInfo();
        Mockito.doReturn(true).when(mockPositionService).delete(Mockito.anyLong());
        positionServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockPositionService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("position/a100").when(mockRequest).getPathInfo();
        positionServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException, SQLException {
        String expectedName = "New position Admin";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();
        positionServlet.doPost(mockRequest, mockResponse);
        ArgumentCaptor<PositionIncomingDto> argumentCaptor = ArgumentCaptor.forClass(PositionIncomingDto.class);
        Mockito.verify(mockPositionService).save(argumentCaptor.capture());
        PositionIncomingDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPostBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\":1}",
                null
        ).when(mockBufferedReader).readLine();
        positionServlet.doPost(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut() throws IOException, NotFoundException, SQLException {
        String expectedName = "Update role Admin";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"" +
                expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();
        positionServlet.doPut(mockRequest, mockResponse);
        ArgumentCaptor<PositionUpdateDto> argumentCaptor = ArgumentCaptor.forClass(PositionUpdateDto.class);
        Mockito.verify(mockPositionService).update(argumentCaptor.capture());
        PositionUpdateDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();
        positionServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
