package example.repository.impl;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import Entity.Employee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import DAO.EmployeeRepository;
import DAO.impl.EmployeeRepositoryImpl;
import util.PropertiesUtil;
import java.sql.SQLException;
import java.util.Optional;
@Testcontainers
@Tag("DockerRequired")
class EmployeeRepositoryImplTest {
    private static final String INIT_SQL = "resources/sql/schema.sql";
    private static final int containerPort = 5432;
    private static final int localPort = 5433;
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
    public static EmployeeRepository employeeRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        employeeRepository = EmployeeRepositoryImpl.getInstance();
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
        String expectedFirstname = "new Firstname";
        String expectedLastname = "new Lastname";

        Employee employee = new Employee(
                null,
                expectedFirstname,
                expectedLastname,
                null,
                null,
                null);
        employee = employeeRepository.save(employee);
        Optional<Employee> resultEmployee = employeeRepository.findById(employee.getId());

        Assertions.assertTrue(resultEmployee.isPresent());
        Assertions.assertEquals(expectedFirstname, resultEmployee.get().getFirstName());
        Assertions.assertEquals(expectedLastname, resultEmployee.get().getLastName());
    }
    @DisplayName("update")
    @Test
    void update() throws SQLException {
        String expectedName = "UPDATE Name";

        Employee employeeForUpdate = employeeRepository.findById(5L).get();
        employeeForUpdate.setFirstName(expectedName);

        employeeRepository.update(employeeForUpdate);
        Assertions.assertEquals(expectedName, employeeForUpdate.getFirstName());
    }
//    @Test
//    void update() throws SQLException {
//        String expectedFirstname = "UPDATE_Firstname";
//        String expectedLastname = "UPDATE_Lastname";
//        Long expectedPositionId = 1L;
//
//        Employee employeeForUpdate = employeeRepository.findById(3L).get();
//
//        List<Subdivision> subdivisionList = employeeForUpdate.getSubdivisionList();
//        int phoneListSize = employeeForUpdate.getPhoneNumberList().size();
//        int subdivisionListSize = employeeForUpdate.getSubdivisionList().size();
//        Position oldPosition = employeeForUpdate.getPosition();
//
//        Assertions.assertNotEquals(expectedPositionId, employeeForUpdate.getPosition().getId());
//        Assertions.assertNotEquals(expectedFirstname, employeeForUpdate.getFirstName());
//        Assertions.assertNotEquals(expectedLastname, employeeForUpdate.getLastName());
//
//        employeeForUpdate.setFirstName(expectedFirstname);
//        employeeForUpdate.setLastName(expectedLastname);
//        employeeRepository.update(employeeForUpdate);
//
//        Employee resultEmployee = employeeRepository.findById(3L).get();
//
//        Assertions.assertEquals(expectedFirstname, resultEmployee.getFirstName());
//        Assertions.assertEquals(expectedLastname, resultEmployee.getLastName());
//
//        Assertions.assertEquals(phoneListSize, resultEmployee.getPhoneNumberList().size());
//        Assertions.assertEquals( subdivisionListSize, resultEmployee.getSubdivisionList().size());
//        Assertions.assertEquals(oldPosition.getId(), resultEmployee.getPosition().getId());
//
//        employeeForUpdate.setPhoneNumberList(List.of());
//        employeeForUpdate.setSubdivisionList(List.of());
//        employeeForUpdate.setPosition(new Position(expectedPositionId, null));
//        employeeRepository.update(employeeForUpdate);
//        resultEmployee = employeeRepository.findById(3L).get();
//
//        Assertions.assertEquals(0, resultEmployee.getPhoneNumberList().size());
//        Assertions.assertEquals(0, resultEmployee.getSubdivisionList().size());
//        Assertions.assertEquals(expectedPositionId, resultEmployee.getPosition().getId());
//
//        subdivisionList.add(new Subdivision(3L, null, null));
//        subdivisionList.add(new Subdivision(4L, null, null));
//        employeeForUpdate.setSubdivisionList(subdivisionList);
//        employeeRepository.update(employeeForUpdate);
//        resultEmployee = employeeRepository.findById(3L).get();
//
//        Assertions.assertEquals(3, resultEmployee.getSubdivisionList().size());
//
//        subdivisionList.remove(2);
//        employeeForUpdate.setSubdivisionList(subdivisionList);
//        employeeRepository.update(employeeForUpdate);
//        resultEmployee = employeeRepository.findById(3L).get();
//
//        Assertions.assertEquals(2, resultEmployee.getSubdivisionList().size());
//
//        employeeForUpdate.setPhoneNumberList(List.of(
//                new PhoneNumber(null, "+4 new phone", null),
//                new PhoneNumber(null, "+1(123)123 2222", null)));
//        employeeForUpdate.setSubdivisionList(List.of(new Subdivision(1L, null, null)));
//
//        employeeRepository.update(employeeForUpdate);
//        resultEmployee = employeeRepository.findById(3L).get();
//
//        Assertions.assertEquals(1, resultEmployee.getPhoneNumberList().size());
//        Assertions.assertEquals(1, resultEmployee.getSubdivisionList().size());
//    }
    @DisplayName("delete by Id")
    @Test
    void deleteById() throws SQLException {
        Boolean expectedValue = true;
        int expectedSize = employeeRepository.findAll().size();

        Employee tempEmployee = new Employee(
                null,
                "Employee for delete Firstname.",
                "Employee for delete Lastname.",
                null,
                null,
                null
        );
        tempEmployee = employeeRepository.save(tempEmployee);

        boolean resultDelete = employeeRepository.deleteById(tempEmployee.getId());
        int positionListAfterSize = employeeRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, positionListAfterSize);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) throws SQLException {
        Optional<Employee> employee = employeeRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, employee.isPresent());
        employee.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() throws SQLException {
        int expectedSize = 7;//7
        int resultSize = employeeRepository.findAll().size();
        System.out.println(employeeRepository.findAll().size());
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long positionId, Boolean expectedValue) throws SQLException {
        boolean isEmployeeExist = employeeRepository.exitsById(positionId);

        Assertions.assertEquals(expectedValue, isEmployeeExist);
    }
}