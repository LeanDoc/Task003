package example.repository.impl;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import Entity.Subdivision;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import DAO.SubdivisionRepository;
import DAO.impl.SubdivisionRepositoryImpl;
import util.PropertiesUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
class SubdivisionRepositoryImplTest {
    private static final String INIT_SQL = "resources/sql/schema.sql";
    public static SubdivisionRepository subdivisionRepository;
    private static int containerPort = 5432;
    private static int localPort = 5433;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15")
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
//        subdivisionRepository = SubdivisionRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
        subdivisionRepository = SubdivisionRepositoryImpl.getInstance();
    }

    @Test
    void save() throws SQLException {
        String expectedName = "new_Subdivision!";

        Subdivision subdivision = new Subdivision(
                null,
                expectedName,
                null
        );
        subdivision = subdivisionRepository.save(subdivision);

        Optional<Subdivision> resultSubdivision = subdivisionRepository.findById(subdivision.getId());

        Assertions.assertTrue(resultSubdivision.isPresent());
        Assertions.assertEquals(expectedName, resultSubdivision.get().getName());

    }

    @Test
    void update() throws SQLException {
        String expectedName = "Update subdivision name";

        Subdivision subdivision = subdivisionRepository.findById(2L).get();
        String oldName = subdivision.getName();
        int expectedSizeEmployeeList = subdivision.getEmployeeList().size();
        subdivision.setName(expectedName);
        subdivisionRepository.update(subdivision);

        Subdivision resultSubdivision = subdivisionRepository.findById(2L).get();
        int resultSizeEmployeeList = resultSubdivision.getEmployeeList().size();

        Assertions.assertNotEquals(expectedName, oldName);
        Assertions.assertEquals(expectedName, resultSubdivision.getName());
        Assertions.assertEquals(expectedSizeEmployeeList, resultSizeEmployeeList);
    }

    @Test
    void deleteById() throws SQLException {
        Boolean expectedValue = true;
        int expectedSize = subdivisionRepository.findAll().size();

        Subdivision tempSubdivision = new Subdivision(null, "New subdivision", List.of());
        tempSubdivision = subdivisionRepository.save(tempSubdivision);

        int resultSizeBefore = subdivisionRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = subdivisionRepository.deleteById(tempSubdivision.getId());
        int resultSizeAfter = subdivisionRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeAfter);

    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "4, true",
            "1000, false"
    })
    void findById(Long expectedId, Boolean expectedValue) throws SQLException {
        Optional<Subdivision> subdivision = subdivisionRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, subdivision.isPresent());
        if (subdivision.isPresent()) {
            Assertions.assertEquals(expectedId, subdivision.get().getId());
        }
    }

    @Test
    void findAll() throws SQLException {
        int expectedSize = 4;
        int resultSize = subdivisionRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long subdivisionId, Boolean expectedValue) throws SQLException {
        boolean isPositionExist = subdivisionRepository.exitsById(subdivisionId);

        Assertions.assertEquals(expectedValue, isPositionExist);
    }
}
