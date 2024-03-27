package example.repository.impl;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import Entity.EmployeeToSubdivision;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import DAO.EmployeeToSubdivisionRepository;
import DAO.impl.EmployeeToSubdivisionRepositoryImpl;
import util.PropertiesUtil;

import java.sql.SQLException;
import java.util.Optional;

class EmployeeToSubdivisionRepositoryImplTest {
    private static final String INIT_SQL = "resources/sql/schema.sql";
    public static EmployeeToSubdivisionRepository employeeToSubdivisionRepository;
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
        employeeToSubdivisionRepository = EmployeeToSubdivisionRepositoryImpl.getInstance();
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

    @Test
    void save() throws SQLException {
        Long expectedEmployeeId = 1L;
        Long expectedSubdivisionId = 4L;
        EmployeeToSubdivision link = new EmployeeToSubdivision(
                null,
                expectedEmployeeId,
                expectedSubdivisionId
        );
        link = employeeToSubdivisionRepository.save(link);
        Optional<EmployeeToSubdivision> resultLink = employeeToSubdivisionRepository.findById(link.getId());

        Assertions.assertTrue(resultLink.isPresent());
        Assertions.assertEquals(expectedEmployeeId, resultLink.get().getEmployeeId());
        Assertions.assertEquals(expectedSubdivisionId, resultLink.get().getSubdivisionId());
    }

    @Test
    void update() throws SQLException {
        Long expectedEmployeeId = 1L;
        Long expectedSubdivisionId = 4L;

        EmployeeToSubdivision link = employeeToSubdivisionRepository.findById(2L).get();

        Long oldSubdivisionId = link.getSubdivisionId();
        Long oldEmployeeId = link.getEmployeeId();

        Assertions.assertNotEquals(expectedEmployeeId, oldEmployeeId);
        Assertions.assertNotEquals(expectedSubdivisionId, oldSubdivisionId);

        link.setEmployeeId(expectedEmployeeId);
        link.setSubdivisionId(expectedSubdivisionId);

        employeeToSubdivisionRepository.update(link);

        EmployeeToSubdivision resultLink = employeeToSubdivisionRepository.findById(2L).get();

        Assertions.assertEquals(link.getId(), resultLink.getId());
        Assertions.assertEquals(expectedEmployeeId, resultLink.getEmployeeId());
        Assertions.assertEquals(expectedSubdivisionId, resultLink.getSubdivisionId());
    }

    @Test
    void deleteById() throws SQLException {
        Boolean expectedValue = true;
        int expectedSize = employeeToSubdivisionRepository.findAll().size();

        EmployeeToSubdivision link = new EmployeeToSubdivision(null, 1L, 3L);
        link = employeeToSubdivisionRepository.save(link);

        int resultSizeBefore = employeeToSubdivisionRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = employeeToSubdivisionRepository.deleteById(link.getId());

        int resultSizeAfter = employeeToSubdivisionRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeAfter);
    }

    @DisplayName("Delete by EmployeeId.")
    @ParameterizedTest
    @CsvSource(value = {
            "2, true",
            "1000, false"
    })
    void deleteByEmployeeId(Long expectedEmployeeId, Boolean expectedValue) throws SQLException {
        int beforeSize = employeeToSubdivisionRepository.findAllByEmployeeId(expectedEmployeeId).size();
        Boolean resultDelete = employeeToSubdivisionRepository.deleteByEmployeeId(expectedEmployeeId);

        int afterDelete = employeeToSubdivisionRepository.findAllByEmployeeId(expectedEmployeeId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @DisplayName("Delete by Subdivision Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "2, true",
            "1000, false"
    })
    void deleteBySubdivisionId(Long expectedSubdivisionId, Boolean expectedValue) throws SQLException {
        int beforeSize = employeeToSubdivisionRepository.findAllBySubdivisionId(expectedSubdivisionId).size();
        Boolean resultDelete = employeeToSubdivisionRepository.deleteBySubdivisionId(expectedSubdivisionId);

        int afterDelete = employeeToSubdivisionRepository.findAllBySubdivisionId(expectedSubdivisionId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @DisplayName("Delete by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true, 1, 1",
            "3, true, 3, 2",
            "1000, false, 0, 0"
    })
    void findById(Long expectedId, Boolean expectedValue, Long expectedEmployeeId, Long expectedSubdivisionId) throws SQLException {
        Optional<EmployeeToSubdivision> link = employeeToSubdivisionRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, link.isPresent());
        if (link.isPresent()) {
            Assertions.assertEquals(expectedId, link.get().getId());
            Assertions.assertEquals(expectedEmployeeId, link.get().getEmployeeId());
            Assertions.assertEquals(expectedSubdivisionId, link.get().getSubdivisionId());
        }
    }

    @Test
    void findAll() throws SQLException {
        int expectedSize = 8;
        int resultSize = employeeToSubdivisionRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "2, true",
            "3, true",
            "1000, false"
    })
    void exitsById(Long expectedId, Boolean expectedValue) throws SQLException {
        Boolean resultValue = employeeToSubdivisionRepository.exitsById(expectedId);
        Assertions.assertEquals(expectedValue, resultValue);
    }

    @DisplayName("Find by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1",
            "6, 2",
            "1000, 0"
    })
    void findAllByEmployeeId(Long employeeId, int expectedSize) throws SQLException {
        int resultSize = employeeToSubdivisionRepository.findAllByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "3, 1",
            "6, 2",
            "1000, 0"
    })
    void findSubdivisionsByEmployeeId(Long employeeId, int expectedSize) throws SQLException {
        int resultSize = employeeToSubdivisionRepository.findSubdivisionsByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find Subdivision by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 3",
            "2, 3",
            "3, 1",
            "1000, 0"
    })
    void findAllBySubdivisionId(Long subdivisionId, int expectedSize) throws SQLException {
        int resultSize = employeeToSubdivisionRepository.findAllBySubdivisionId(subdivisionId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find Employees by Subdivision Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 3",
            "2, 3",
            "3, 1",
            "1000, 0"
    })
    void findEmployeesBySubdivisionId(Long subdivisionId, int expectedSize) throws SQLException {
        int resultSize = employeeToSubdivisionRepository.findEmployeesBySubdivisionId(subdivisionId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find Employees by Subdivision Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1, true",
            "1, 4, false"
    })
    void findByEmployeeIdAndSubdivisionId(Long employeeId, Long subdivisionId, Boolean expectedValue) throws SQLException {
        Optional<EmployeeToSubdivision> link = employeeToSubdivisionRepository.findByEmployeeIdAndSubdivisionId(employeeId, subdivisionId);

        Assertions.assertEquals(expectedValue, link.isPresent());
    }
}