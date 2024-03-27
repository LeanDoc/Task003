package DAO.impl;

import connection.ConnectionManagerImpl;

import Entity.Employee;
import Entity.EmployeeToSubdivision;
import Entity.Subdivision;
import DAO.EmployeeRepository;
import DAO.EmployeeToSubdivisionRepository;
import DAO.SubdivisionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeToSubdivisionRepositoryImpl implements EmployeeToSubdivisionRepository {
    private static final ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();
    private static final SubdivisionRepository subdivisionRepository = SubdivisionRepositoryImpl.getInstance();
    private static final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private static final String SAVE_SQL = """
            INSERT INTO employees_subdivisions (employee_id, subdivision_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE employees_subdivisions
            SET employee_id = ?,
                subdivision_id = ?
            WHERE employees_subdivisions_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM employees_subdivisions
            WHERE employees_subdivisions_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT employees_subdivisions_id, employee_id, subdivision_id FROM employees_subdivisions
            WHERE employees_subdivisions_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT employees_subdivisions_id, employee_id, subdivision_id FROM employees_subdivisions;
            """;
    private static final String FIND_ALL_BY_EMPLOYEEID_SQL = """
            SELECT employees_subdivisions_id, employee_id, subdivision_id FROM employees_subdivisions
            WHERE employee_id = ?;
            """;
    private static final String FIND_ALL_BY_SUBDIVISION_ID_SQL = """
            SELECT employees_subdivisions_id, employee_id, subdivision_id FROM employees_subdivisions
            WHERE subdivision_id = ?;
            """;
    private static final String FIND_BY_EMPLOYEEID_AND_SUBDIVISION_ID_SQL = """
            SELECT employees_subdivisions_id, employee_id, subdivision_id FROM employees_subdivisions
            WHERE employee_id = ? AND subdivision_id = ?
            LIMIT 1;
            """;
    private static final String DELETE_BY_EMPLOYEEID_SQL = """
            DELETE FROM employees_subdivisions
            WHERE employee_id = ?;
            """;
    private static final String DELETE_BY_SUBDIVISION_ID_SQL = """
            DELETE FROM employees_subdivisions
            WHERE subdivision_id = ?;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM employees_subdivisions
                        WHERE employees_subdivisions_id = ?
                        LIMIT 1);
            """;
    private static EmployeeToSubdivisionRepository instance;

    private EmployeeToSubdivisionRepositoryImpl() {
    }

    public static synchronized EmployeeToSubdivisionRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeToSubdivisionRepositoryImpl();
        }
        return instance;
    }

    private static EmployeeToSubdivision createEmployeeToSubdivision(ResultSet resultSet) throws SQLException {
        EmployeeToSubdivision employeeToSubdivision;
        employeeToSubdivision = new EmployeeToSubdivision(
                resultSet.getLong("employees_subdivisions_id"),
                resultSet.getLong("employee_id"),
                resultSet.getLong("subdivision_id")
        );
        return employeeToSubdivision;
    }

    @Override
    public EmployeeToSubdivision save(EmployeeToSubdivision employeeToSubdivision) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, employeeToSubdivision.getEmployeeId());
            preparedStatement.setLong(2, employeeToSubdivision.getSubdivisionId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                employeeToSubdivision = new EmployeeToSubdivision(
                        resultSet.getLong("employees_subdivisions_id"),
                        employeeToSubdivision.getEmployeeId(),
                        employeeToSubdivision.getSubdivisionId()
                );
            }
        } catch (SQLException e) {
            throw new SQLException (e);
        }

        return employeeToSubdivision;
    }

    @Override
    public void update(EmployeeToSubdivision employeeToSubdivision) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setLong(1, employeeToSubdivision.getEmployeeId());
            preparedStatement.setLong(2, employeeToSubdivision.getSubdivisionId());
            preparedStatement.setLong(3, employeeToSubdivision.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            preparedStatement.setLong(1, id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteByEmployeeId(Long employeeId) throws SQLException {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_EMPLOYEEID_SQL);) {

            preparedStatement.setLong(1, employeeId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteBySubdivisionId(Long subdivisionId) throws SQLException {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_SUBDIVISION_ID_SQL);) {

            preparedStatement.setLong(1, subdivisionId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return deleteResult;
    }

    @Override
    public Optional<EmployeeToSubdivision> findById(Long id) throws SQLException {
        EmployeeToSubdivision employeeToSubdivision = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeToSubdivision = createEmployeeToSubdivision(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return Optional.ofNullable(employeeToSubdivision);
    }

    @Override
    public List<EmployeeToSubdivision> findAll() throws SQLException {
        List<EmployeeToSubdivision> employeeToSubdivisionList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeToSubdivisionList.add(createEmployeeToSubdivision(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return employeeToSubdivisionList;
    }

    @Override
    public boolean exitsById(Long id) throws SQLException {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return isExists;
    }

    public List<EmployeeToSubdivision> findAllByEmployeeId(Long employeeId) throws SQLException {
        List<EmployeeToSubdivision> EmployeeToSubdivisionList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEEID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                EmployeeToSubdivisionList.add(createEmployeeToSubdivision(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return EmployeeToSubdivisionList;
    }

    public List<Subdivision> findSubdivisionsByEmployeeId(Long employeeId) throws SQLException {
        List<Subdivision> subdivisionList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEEID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long subdivisionId = resultSet.getLong("subdivision_id");
                Optional<Subdivision> optionalSubdivision = subdivisionRepository.findById(subdivisionId);
                if (optionalSubdivision.isPresent()) {
                    subdivisionList.add(optionalSubdivision.get());
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return subdivisionList;
    }

    public List<EmployeeToSubdivision> findAllBySubdivisionId(Long subdivisionId) throws SQLException {
        List<EmployeeToSubdivision> EmployeeToSubdivisionList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_SUBDIVISION_ID_SQL)) {

            preparedStatement.setLong(1, subdivisionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                EmployeeToSubdivisionList.add(createEmployeeToSubdivision(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return EmployeeToSubdivisionList;
    }

    public List<Employee> findEmployeesBySubdivisionId(Long subdivisionId) throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_SUBDIVISION_ID_SQL)) {

            preparedStatement.setLong(1, subdivisionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long employeeId = resultSet.getLong("employee_id");
                Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
                if (optionalEmployee.isPresent()) {
                    employeeList.add(optionalEmployee.get());
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return employeeList;
    }

    public Optional<EmployeeToSubdivision> findByEmployeeIdAndSubdivisionId(Long employeeId, Long subdivisionId) throws SQLException {
        Optional<EmployeeToSubdivision> employeeToSubdivision = Optional.empty();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMPLOYEEID_AND_SUBDIVISION_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            preparedStatement.setLong(2, subdivisionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeToSubdivision = Optional.of(createEmployeeToSubdivision(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return employeeToSubdivision;
    }

}
