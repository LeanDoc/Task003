package example.repository.impl;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import Entity.PhoneNumber;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import DAO.PhoneNumberRepository;
import DAO.impl.PhoneNumberRepositoryImpl;
import util.PropertiesUtil;

import java.sql.SQLException;
import java.util.Optional;

class PhoneNumberRepositoryImplTest {
    private static final String INIT_SQL = "resources/sql/schema.sql";
    public static PhoneNumberRepository phoneNumberRepository;
    private static int containerPort = 5432;
    private static int localPort = 5433;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("employees_db")
            .withUsername(PropertiesUtil.getProperties("db.username"))
            .withPassword(PropertiesUtil.getProperties("db.password"))
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))
            ))
            .withInitScript(INIT_SQL);
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        phoneNumberRepository = PhoneNumberRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
    }
    @DisplayName("Save")
    @Test
    void save() throws SQLException {
        String expectedNumber = "+3 (123) 123 4321";
        PhoneNumber phoneNumber = new PhoneNumber(
                null,
                expectedNumber,
                null
        );
        phoneNumber = phoneNumberRepository.save(phoneNumber);
        Optional<PhoneNumber> resultPhone = phoneNumberRepository.findById(phoneNumber.getId());

        Assertions.assertTrue(resultPhone.isPresent());
        Assertions.assertEquals(expectedNumber, resultPhone.get().getNumber());
    }
    @DisplayName("Update")
    @Test
    void update() throws SQLException {
        String expectedNumber = "+3 (321) 321 4321";

        PhoneNumber phoneNumberUpdate = phoneNumberRepository.findById(3L).get();
        String oldPhoneNumber = phoneNumberUpdate.getNumber();

        phoneNumberUpdate.setNumber(expectedNumber);
        phoneNumberRepository.update(phoneNumberUpdate);

        PhoneNumber number = phoneNumberRepository.findById(3L).get();

        Assertions.assertNotEquals(expectedNumber, oldPhoneNumber);
        Assertions.assertEquals(expectedNumber, number.getNumber());
    }

    @DisplayName("Delete by ID")
    @Test
    void deleteById() throws SQLException {
        Boolean expectedValue = true;
        int expectedSize = phoneNumberRepository.findAll().size();
        PhoneNumber tempNumber = new PhoneNumber(null, "+(temp) number", null);
        tempNumber = phoneNumberRepository.save(tempNumber);

        int resultSizeBefore = phoneNumberRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = phoneNumberRepository.deleteById(tempNumber.getId());
        int resultSizeListAfter = phoneNumberRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeListAfter);
    }

    @DisplayName("Delete by ID")
    @Test
    void deleteByEmployeeId() throws SQLException {
        Boolean expectedValue = true;
        int expectedSize = phoneNumberRepository.findAll().size() - phoneNumberRepository.findAllByEmployeeId(1L).size();

        boolean resultDelete = phoneNumberRepository.deleteByEmployeeId(1L);
        System.out.println(resultDelete);
        int resultSize = phoneNumberRepository.findAll().size();
        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Check exist by Phone number.")
    @ParameterizedTest
    @CsvSource(value = {
            "'+1(123)123 5555',true",
            "'not exits number', false"
    })
    void existsByNumber(String number, Boolean expectedValue) throws SQLException {
        boolean isExist = phoneNumberRepository.existsByNumber(number);

        Assertions.assertEquals(expectedValue, isExist);
    }

    @DisplayName("Find by Phone number.")
    @ParameterizedTest
    @CsvSource(value = {
            "'+1(123)123 5555',true",
            "'not exits number', false"
    })
    void findByNumber(String findNumber, Boolean expectedValue) throws SQLException {
        Optional<PhoneNumber> phoneNumber = phoneNumberRepository.findByNumber(findNumber);

        Assertions.assertEquals(expectedValue, phoneNumber.isPresent());
        if (phoneNumber.isPresent()) {
            Assertions.assertEquals(findNumber, phoneNumber.get().getNumber());
        }
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "4, true",
            "1000, false"
    })
    void findById(Long expectedId, Boolean expectedValue) throws SQLException {
        Optional<PhoneNumber> phoneNumber = phoneNumberRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, phoneNumber.isPresent());
        if (phoneNumber.isPresent()) {
            Assertions.assertEquals(expectedId, phoneNumber.get().getId());
        }
    }

    @Test
    void findAll() throws SQLException {
        int expectedSize = 9; //9
        int resultSize = phoneNumberRepository.findAll().size();
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 1111"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 2222"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 3333"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 4444"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 5555"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 6666"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 7777"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 8888"));
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 9995"));
        System.out.println(phoneNumberRepository.findAll());
//        System.out.println(phoneNumberRepository.findByNumber("+1(123)123 9995").get().getId());
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "4, true",
            "1000, false"
    })
    void exitsById(Long expectedId, Boolean expectedValue) throws SQLException {
        Boolean resultValue = phoneNumberRepository.exitsById(expectedId);

        Assertions.assertEquals(expectedValue, resultValue);
    }

    @DisplayName("Find by EmployeeId")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 2",
            "2, 2",
            "3, 1",
            "1000, 0"
    })
    void findAllByEmployeeId(Long employeeId, int expectedSize) throws SQLException {
        int resultSize = phoneNumberRepository.findAllByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }
}