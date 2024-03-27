package example.service.impl;

import servlet.dto.SubdivisionIncomingDto;
import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;
import Entity.EmployeeToSubdivision;
import Entity.Subdivision;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import DAO.EmployeeRepository;
import DAO.EmployeeToSubdivisionRepository;
import DAO.SubdivisionRepository;
import DAO.impl.EmployeeRepositoryImpl;
import DAO.impl.EmployeeToSubdivisionRepositoryImpl;
import DAO.impl.SubdivisionRepositoryImpl;
import service.SubdivisionService;
import service.impl.SubdivisionServiceImpl;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

class SubdivisionServiceImplTest {
    private static SubdivisionService subdivisionService;
    private static SubdivisionRepository mockSubdivisionRepository;
    private static EmployeeRepository mockEmployeeRepository;
    private static EmployeeToSubdivisionRepository mockEmployeeToSubdivisionRepository;
    private static SubdivisionRepositoryImpl oldSubdivisionInstance;
    private static EmployeeRepositoryImpl oldEmployeeInstance;
    private static EmployeeToSubdivisionRepositoryImpl oldLinkInstance;

    private static void setMock(SubdivisionRepository mock) {
        try {
            Field instance = SubdivisionRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldSubdivisionInstance = (SubdivisionRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(EmployeeRepository mock) {
        try {
            Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldEmployeeInstance = (EmployeeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(EmployeeToSubdivisionRepository mock) {
        try {
            Field instance = EmployeeToSubdivisionRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldLinkInstance = (EmployeeToSubdivisionRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockSubdivisionRepository = Mockito.mock(SubdivisionRepository.class);
        setMock(mockSubdivisionRepository);
        mockEmployeeRepository = Mockito.mock(EmployeeRepository.class);
        setMock(mockEmployeeRepository);
        mockEmployeeToSubdivisionRepository = Mockito.mock(EmployeeToSubdivisionRepository.class);
        setMock(mockEmployeeToSubdivisionRepository);

        subdivisionService = SubdivisionServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = SubdivisionRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldSubdivisionInstance);

        instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldEmployeeInstance);

        instance = EmployeeToSubdivisionRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldLinkInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockSubdivisionRepository);
    }

    @Test
    void save() throws SQLException {
        Long expectedId = 1L;

        SubdivisionIncomingDto dto = new SubdivisionIncomingDto("subdivision #2");
        Subdivision subdivision = new Subdivision(expectedId, "subdivision #10", List.of());

        Mockito.doReturn(subdivision).when(mockSubdivisionRepository).save(Mockito.any(Subdivision.class));

        SubdivisionOutGoingDto result = subdivisionService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws SQLException {
        Long expectedId = 1L;

        SubdivisionUpdateDto dto = new SubdivisionUpdateDto(expectedId, "subdivision update #1");

        Mockito.doReturn(true).when(mockSubdivisionRepository).exitsById(Mockito.any());

        subdivisionService.update(dto);

        ArgumentCaptor<Subdivision> argumentCaptor = ArgumentCaptor.forClass(Subdivision.class);
        Mockito.verify(mockSubdivisionRepository).update(argumentCaptor.capture());

        Subdivision result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

//    @Test
//    void updateNotFound() throws SQLException {
//        SubdivisionUpdateDto dto = new SubdivisionUpdateDto(1L, "subdivision update #1");
//
//        Mockito.doReturn(false).when(mockSubdivisionRepository).exitsById(Mockito.any());
//
//        NotFoundException exception = Assertions.assertThrows(
//                NotFoundException.class,
//                () -> {
//                    subdivisionService.update(dto);
//                }, "Not found."
//        );
//        Assertions.assertEquals("Subdivision not found.", exception.getMessage());
//    }

    @Test
    void findById() throws SQLException {
        Long expectedId = 1L;

        Optional<Subdivision> subdivision = Optional.of(new Subdivision(expectedId, "subdivision found #1", List.of()));

        Mockito.doReturn(true).when(mockSubdivisionRepository).exitsById(Mockito.any());
        Mockito.doReturn(subdivision).when(mockSubdivisionRepository).findById(Mockito.anyLong());

        SubdivisionOutGoingDto dto = subdivisionService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

//    @Test
//    void findByIdNotFound() throws SQLException {
//        Optional<Subdivision> subdivision = Optional.empty();
//
//        Mockito.doReturn(false).when(mockSubdivisionRepository).exitsById(Mockito.any());
//
//        NotFoundException exception = Assertions.assertThrows(
//                NotFoundException.class,
//                () -> {
//                    subdivisionService.findById(1L);
//                }, "Not found."
//        );
//        Assertions.assertEquals("Subdivision not found.", exception.getMessage());
//    }

    @Test
    void findAll() throws SQLException {
        subdivisionService.findAll();
        Mockito.verify(mockSubdivisionRepository).findAll();
    }

    @Test
    void delete() throws SQLException {
        Long expectedId = 100L;

        Mockito.doReturn(true).when(mockSubdivisionRepository).exitsById(Mockito.any());
        subdivisionService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockSubdivisionRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void deleteUserFromDepartment() throws SQLException {
        Long expectedId = 100L;
        Optional<EmployeeToSubdivision> link = Optional.of(new EmployeeToSubdivision(expectedId, 1L, 2L));

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());
        Mockito.doReturn(true).when(mockSubdivisionRepository).exitsById(Mockito.any());
        Mockito.doReturn(link).when(mockEmployeeToSubdivisionRepository).findByEmployeeIdAndSubdivisionId(Mockito.anyLong(), Mockito.anyLong());

        subdivisionService.deleteEmployeeFromSubdivision(1L, 1l);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockEmployeeToSubdivisionRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void addUserToDepartment() throws SQLException {
        Long expectedEmployeeId = 100L;
        Long expectedSubdivisionId = 500L;

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());
        Mockito.doReturn(true).when(mockSubdivisionRepository).exitsById(Mockito.any());

        subdivisionService.addEmployeeToSubdivision(expectedSubdivisionId, expectedEmployeeId);

        ArgumentCaptor<EmployeeToSubdivision> argumentCaptor = ArgumentCaptor.forClass(EmployeeToSubdivision.class);
        Mockito.verify(mockEmployeeToSubdivisionRepository).save(argumentCaptor.capture());
        EmployeeToSubdivision result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedEmployeeId, result.getEmployeeId());
        Assertions.assertEquals(expectedSubdivisionId, result.getSubdivisionId());
    }
}