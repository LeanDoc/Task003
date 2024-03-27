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
import service.SubdivisionService;
import service.impl.SubdivisionServiceImpl;
import servlet.dto.SubdivisionIncomingDto;
import servlet.SubdivisionServlet;
import servlet.dto.SubdivisionUpdateDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.SQLException;

@ExtendWith(
        MockitoExtension.class
)
class SubdivisionServletTest {
    private static SubdivisionService mockSubdivisionService;
    @InjectMocks
    private static SubdivisionServlet subdivisionServlet;
    private static SubdivisionServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(SubdivisionService mock) {
        try {
            Field instance = SubdivisionServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (SubdivisionServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockSubdivisionService = Mockito.mock(SubdivisionService.class);
        setMock(mockSubdivisionService);
        subdivisionServlet = new SubdivisionServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = SubdivisionServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockSubdivisionService);
    }

    @Test
    void doGetAll() throws IOException, SQLException {
        Mockito.doReturn("subdivision/all").when(mockRequest).getPathInfo();

        subdivisionServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockSubdivisionService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException, SQLException {
        Mockito.doReturn("subdivision/2").when(mockRequest).getPathInfo();

        subdivisionServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockSubdivisionService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException, SQLException {
        Mockito.doReturn("subdivision/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockSubdivisionService).findById(100L);

        subdivisionServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("subdivision/2q").when(mockRequest).getPathInfo();

        subdivisionServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException, SQLException {
        Mockito.doReturn("subdivision/2").when(mockRequest).getPathInfo();

        subdivisionServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockSubdivisionService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("subdivision/a100").when(mockRequest).getPathInfo();

        subdivisionServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException, SQLException {
        String expectedName = "New subdivision";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        subdivisionServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<SubdivisionIncomingDto> argumentCaptor = ArgumentCaptor.forClass(SubdivisionIncomingDto.class);
        Mockito.verify(mockSubdivisionService).save(argumentCaptor.capture());

        SubdivisionIncomingDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPut() throws IOException, NotFoundException, SQLException {
        String expectedName = "Update department";

        Mockito.doReturn("department/").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"" +
                expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        subdivisionServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<SubdivisionUpdateDto> argumentCaptor = ArgumentCaptor.forClass(SubdivisionUpdateDto.class);
        Mockito.verify(mockSubdivisionService).update(argumentCaptor.capture());

        SubdivisionUpdateDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn("subdivision/").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        subdivisionServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}