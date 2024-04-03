package example.service.impl;

import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionOutGoingDto;
import servlet.dto.PositionUpdateDto;
import Entity.Position;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import DAO.PositionRepository;
import DAO.impl.PositionRepositoryImpl;
import service.PositionService;
import service.impl.PositionServiceImpl;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Optional;

class PositionServiceImplTest {
    private static PositionService positionService;
    private static PositionRepository mockPositionRepository;
    private static PositionRepositoryImpl oldInstance;

    private static void setMock(PositionRepository mock) {
        try {
            Field instance = PositionRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (PositionRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockPositionRepository = Mockito.mock(PositionRepository.class);
        setMock(mockPositionRepository);
        positionService = PositionServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = PositionRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockPositionRepository);
    }

    @Test
    void save() throws SQLException {
        Long expectedId = 1L;
        PositionIncomingDto dto = new PositionIncomingDto("position #2");
        Position role = new Position(expectedId, "position #10");
        Mockito.doReturn(role).when(mockPositionRepository).save(Mockito.any(Position.class));
        PositionOutGoingDto result = positionService.save(dto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws SQLException {
        Long expectedId = 1L;
        PositionUpdateDto dto = new PositionUpdateDto(expectedId, "position update #1");
        Mockito.doReturn(true).when(mockPositionRepository).exitsById(Mockito.any());
        positionService.update(dto);
        ArgumentCaptor<Position> argumentCaptor = ArgumentCaptor.forClass(Position.class);
        Mockito.verify(mockPositionRepository).update(argumentCaptor.capture());
        Position result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findById() throws SQLException {
        Long expectedId = 1L;
        Optional<Position> role = Optional.of(new Position(expectedId, "position found #1"));
        Mockito.doReturn(true).when(mockPositionRepository).exitsById(Mockito.any());
        Mockito.doReturn(role).when(mockPositionRepository).findById(Mockito.anyLong());
        PositionOutGoingDto dto = positionService.findById(expectedId);
        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findAll() throws SQLException {
        positionService.findAll();
        Mockito.verify(mockPositionRepository).findAll();
    }

    @Test
    void delete() throws SQLException {
        Long expectedId = 100L;
        Mockito.doReturn(true).when(mockPositionRepository).exitsById(100L);
        positionService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockPositionRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}
