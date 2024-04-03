package DAO.impl;

import connection.ConnectionManagerImpl;
import Entity.*;
import DAO.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String SAVE_SQL = """
            INSERT INTO employees (employee_firstname, employee_lastname, position_id)
            VALUES (?, ? ,?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE employees
            SET employee_firstname = ?,
                employee_lastname = ?,
                position_id =?
            WHERE employee_id = ?  ;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM employees
            WHERE employee_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT employee_id, employee_firstname, employee_lastname, position_id FROM employees
            WHERE employee_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT employee_id, employee_firstname, employee_lastname, position_id FROM employees;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM employees
                        WHERE employee_id = ?
                        LIMIT 1);
            """;
    private static EmployeeRepository instance;
    private final ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();
    private final EmployeeToSubdivisionRepository employeeToSubdivisionRepository =
            EmployeeToSubdivisionRepositoryImpl.getInstance();
    private final PhoneNumberRepository phoneNumberRepository = PhoneNumberRepositoryImpl.getInstance();
    private final PositionRepository positionRepository = PositionRepositoryImpl.getInstance();
    private final SubdivisionRepository subdivisionRepository = SubdivisionRepositoryImpl.getInstance();

    private EmployeeRepositoryImpl() {
    }

    public static synchronized EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepositoryImpl();
        }
        return instance;
    }

    /**
     * Сохраняем Employee,
     *
     * @param employee
     * @return
     */
    @Override
    public Employee save(Employee employee) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            if (employee.getPosition() == null) {
                preparedStatement.setNull(3, Types.NULL);
            } else {
                preparedStatement.setLong(3, employee.getPosition().getId());
            }
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getLong("employee_id"),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getPosition(),
                        null,
                        null
                );
            }
            savePhoneNumberList(employee);
            saveSubdivisionList(employee);
            employee.getPhoneNumberList();
            employee.getSubdivisionList();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return employee;
    }

    private void saveSubdivisionList(Employee employee) throws SQLException {
        if (employee.getSubdivisionList() != null && !employee.getSubdivisionList().isEmpty()) {
            List<Long> subdivisionIdList = new ArrayList<>(
                    employee.getSubdivisionList()
                            .stream()
                            .map(Subdivision::getId)
                            .toList()
            );
            List<EmployeeToSubdivision> existsSubdivisionList = employeeToSubdivisionRepository.
                    findAllByEmployeeId(employee.getId());
            for (EmployeeToSubdivision employeeToSubdivision : existsSubdivisionList) {
                if (!subdivisionIdList.contains(employeeToSubdivision.getSubdivisionId())) {
                    employeeToSubdivisionRepository.deleteById(employeeToSubdivision.getId());
                }
                subdivisionIdList.remove(employeeToSubdivision.getSubdivisionId());
            }
            for (Long subdivisionId : subdivisionIdList) {
                if (subdivisionRepository.exitsById(subdivisionId)) {
                    EmployeeToSubdivision employeeToSubdivision = new EmployeeToSubdivision(
                            null,
                            employee.getId(),
                            subdivisionId
                    );
                    employeeToSubdivisionRepository.save(employeeToSubdivision);
                }
            }
        } else {
            employeeToSubdivisionRepository.deleteByEmployeeId(employee.getId());
        }
    }

    private void savePhoneNumberList(Employee employee) throws SQLException {
        if (employee.getPhoneNumberList() != null && !employee.getPhoneNumberList().isEmpty()) {
            List<PhoneNumber> phoneNumberList = new ArrayList<>(employee.getPhoneNumberList());
            List<Long> existsPhoneNumberIdList = new ArrayList<>(
                    phoneNumberRepository.findAllByEmployeeId(employee.getId())
                            .stream()
                            .map(PhoneNumber::getId)
                            .toList()
            );
            for (int i = 0; i < phoneNumberList.size(); i++) {
                PhoneNumber phoneNumber = phoneNumberList.get(i);
                phoneNumber.setEmployee(employee);
                if (existsPhoneNumberIdList.contains(phoneNumber.getId())) {
                    phoneNumberRepository.update(phoneNumber);
                } else {
                    saveOrUpdateExitsNumber(phoneNumber);
                }
                phoneNumberList.set(i, null);
                existsPhoneNumberIdList.remove(phoneNumber.getId());
            }
            phoneNumberList
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(phoneNumber -> {
                        phoneNumber.setEmployee(employee);
                        try {
                            phoneNumberRepository.save(phoneNumber);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
            existsPhoneNumberIdList
                    .stream()
                    .forEach(id -> {
                        try {
                            phoneNumberRepository.deleteById(id);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } else {
            phoneNumberRepository.deleteByEmployeeId(employee.getId());
        }
    }

    private void saveOrUpdateExitsNumber(PhoneNumber phoneNumber) throws SQLException {
        if (phoneNumberRepository.existsByNumber(phoneNumber.getNumber())) {
            Optional<PhoneNumber> exitNumber = phoneNumberRepository.findByNumber(phoneNumber.getNumber());
            if (exitNumber.isPresent()
                && exitNumber.get().getEmployee() != null
                && exitNumber.get().getEmployee().getId() > 0) {
                phoneNumber = new PhoneNumber(exitNumber.get().getId(),
                        exitNumber.get().getNumber(),
                        exitNumber.get().getEmployee()
                );
                phoneNumberRepository.update(phoneNumber);
            }
        } else {
            phoneNumberRepository.save(phoneNumber);
        }
    }

    @Override
    public void update(Employee employee ) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            if (employee.getPosition() == null) {
                preparedStatement.setNull(3, Types.NULL);
            } else {
                preparedStatement.setLong(3, employee.getPosition().getId());
            }
            preparedStatement.setLong(4, employee.getId());

            preparedStatement.executeUpdate();
            savePhoneNumberList(employee);
            saveSubdivisionList(employee);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {
            employeeToSubdivisionRepository.deleteByEmployeeId(id);
            phoneNumberRepository.deleteByEmployeeId(id);
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return deleteResult;
    }

    @Override
    public Optional<Employee> findById(Long id) throws SQLException {
        Employee employee = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee = createEmployee(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return Optional.ofNullable(employee);
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeList.add(createEmployee(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return employeeList;
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

    private Employee createEmployee(ResultSet resultSet) throws SQLException {
        Long employeeId = resultSet.getLong("employee_id");
        Position position = positionRepository.findById(resultSet.getLong("position_id")).orElse(null);
Employee employee = new Employee(
                employeeId,
                resultSet.getString("employee_firstname"),
                resultSet.getString("employee_lastname"),
                position,
                null,
                null
        );
    return employee;
    }
}
