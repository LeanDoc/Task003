package example.service.impl;

import servlet.dto.PhoneNumberIncomingDto;
import servlet.dto.PhoneNumberOutGoingDto;
import servlet.dto.PhoneNumberUpdateDto;
import Entity.PhoneNumber;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import DAO.PhoneNumberRepository;
import DAO.impl.PhoneNumberRepositoryImpl;
import service.PhoneNumberService;
import service.impl.PhoneNumberServiceImpl;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Optional;

class PhoneNumberServiceImplTest {
    private static PhoneNumberService phoneNumberService;
    private static PhoneNumberRepository mockPhoneNumberRepository;
    private static PhoneNumberRepositoryImpl oldInstance;

    private static void setMock(PhoneNumberRepository mock) {
        try {
            Field instance = PhoneNumberRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (PhoneNumberRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockPhoneNumberRepository = Mockito.mock(PhoneNumberRepository.class);
        setMock(mockPhoneNumberRepository);
        phoneNumberService = PhoneNumberServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = PhoneNumberRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockPhoneNumberRepository);
    }

    @Test
    void save() throws SQLException {
        Long expectedId = 1L;

        PhoneNumberIncomingDto dto = new PhoneNumberIncomingDto("+123 123 1111");
        PhoneNumber phoneNumber = new PhoneNumber(expectedId, "+123 123 1111", null);

        Mockito.doReturn(phoneNumber).when(mockPhoneNumberRepository).save(Mockito.any(PhoneNumber.class));

        PhoneNumberOutGoingDto result = phoneNumberService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws SQLException {
        Long expectedId = 1L;

        PhoneNumberUpdateDto dto = new PhoneNumberUpdateDto(expectedId, "+123 123 1111", null);

        Mockito.doReturn(true).when(mockPhoneNumberRepository).exitsById(Mockito.any());

        phoneNumberService.update(dto);

        ArgumentCaptor<PhoneNumber> argumentCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
        Mockito.verify(mockPhoneNumberRepository).update(argumentCaptor.capture());

        PhoneNumber result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

//    @Test
//    void updateNotFound() throws SQLException {
//        PhoneNumberUpdateDto dto = new PhoneNumberUpdateDto(1L, "+123 123 1111", null);
//
//        Mockito.doReturn(false).when(mockPhoneNumberRepository).exitsById(Mockito.any());
//
//        NotFoundException exception = Assertions.assertThrows(
//                NotFoundException.class,
//                () -> {
//                    phoneNumberService.update(dto);
//                }, "Not found."
//        );
//        Assertions.assertEquals("PhoneNumber not found.", exception.getMessage());
//    }

    @Test
    void findById() throws SQLException {
        Long expectedId = 1L;

        Optional<PhoneNumber> role = Optional.of(new PhoneNumber(expectedId, "+123 123 1111", null));

        Mockito.doReturn(true).when(mockPhoneNumberRepository).exitsById(Mockito.any());
        Mockito.doReturn(role).when(mockPhoneNumberRepository).findById(Mockito.anyLong());

        PhoneNumberOutGoingDto dto = phoneNumberService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

//    @Test
//    void findByIdNotFound() throws SQLException {
//        Optional<Position> position = Optional.empty();
//
//        Mockito.doReturn(false).when(mockPhoneNumberRepository).exitsById(Mockito.any());
//
//        NotFoundException exception = Assertions.assertThrows(
//                NotFoundException.class,
//                () -> {
//                    phoneNumberService.findById(1L);
//                }, "Not found."
//        );
//        Assertions.assertEquals("PhoneNumber not found.", exception.getMessage());
//    }

    @Test
    void findAll() throws SQLException {
        phoneNumberService.findAll();
        Mockito.verify(mockPhoneNumberRepository).findAll();
    }

    @Test
    void delete() throws SQLException {
        Long expectedId = 100L;

        phoneNumberService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockPhoneNumberRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}