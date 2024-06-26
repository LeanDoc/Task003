package DAO.impl;

import connection.ConnectionManagerImpl;
import Entity.Employee;
import Entity.PhoneNumber;
import DAO.PhoneNumberRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhoneNumberRepositoryImpl implements PhoneNumberRepository {
    private static final ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();
    private static final String SAVE_SQL = """
            INSERT INTO phone_numbers (phonenumber_number, employee_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE phone_numbers
            SET phonenumber_number = ?,
                employee_id = ?
            WHERE phonenumber_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM phone_numbers
            WHERE phonenumber_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT phonenumber_id, phonenumber_number, employee_id FROM phone_numbers
            WHERE phonenumber_id = ?
            LIMIT 1;
            """;
    private static final String FIND_BY_NUMBER_SQL = """
            SELECT phonenumber_id, phonenumber_number, employee_id FROM phone_numbers
            WHERE phonenumber_number = ?
            LIMIT 1;
            """;
    private static final String EXIST_BY_NUMBER_SQL = """
            SELECT exists (
                SELECT 1
                    FROM phone_numbers
                        WHERE phonenumber_number = LOWER(?)
                        LIMIT 1
            );
            """;
    private static final String FIND_ALL_BY_EMPLOYEEID_SQL = """
            SELECT phonenumber_id, phonenumber_number, employee_id FROM phone_numbers
            WHERE employee_id = ?;
            """;
    private static final String DELETE_ALL_BY_EMPLOYEEID_SQL = """
            DELETE FROM phone_numbers
            WHERE employee_id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT phonenumber_id, phonenumber_number, employee_id FROM phone_numbers;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM phone_numbers
                        WHERE phonenumber_id = ?
                        LIMIT 1);
            """;
    private static PhoneNumberRepository instance;
    private PhoneNumberRepositoryImpl() {
    }

    public static synchronized PhoneNumberRepository getInstance() {
        if (instance == null) {
            instance = new PhoneNumberRepositoryImpl();
        }
        return instance;
    }

    private static PhoneNumber createPhoneNumber(ResultSet resultSet) throws SQLException {
        PhoneNumber phoneNumber;
        Employee employee = new Employee(
                resultSet.getLong("employee_id"),
                null,
                null,
                null,
                List.of(),
                List.of()
        );
        phoneNumber = new PhoneNumber(
                resultSet.getLong("phonenumber_id"),
                resultSet.getString("phonenumber_number"),
                employee);
        return phoneNumber;
    }

    @Override
    public PhoneNumber save(PhoneNumber phoneNumber) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, phoneNumber.getNumber());
            if (phoneNumber.getEmployee() == null) {
                preparedStatement.setNull(2, Types.NULL);
            } else {
                preparedStatement.setLong(2, phoneNumber.getEmployee().getId());
            }
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                phoneNumber = new PhoneNumber(
                        resultSet.getLong("phonenumber_id"),
                        phoneNumber.getNumber(),
                        phoneNumber.getEmployee()
                );
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return phoneNumber;
    }

    @Override
    public void update(PhoneNumber phoneNumber) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {
            preparedStatement.setString(1, phoneNumber.getNumber());
            if (phoneNumber.getEmployee() == null) {
                preparedStatement.setNull(2, Types.NULL);
            } else {
                preparedStatement.setLong(2, phoneNumber.getEmployee().getId());
            }
            preparedStatement.setLong(3, phoneNumber.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        boolean deleteResult = true;
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
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_BY_EMPLOYEEID_SQL);) {
            preparedStatement.setLong(1, employeeId);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return deleteResult;
    }

    @Override
    public boolean existsByNumber(String number) throws SQLException {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_NUMBER_SQL)) {
            preparedStatement.setString(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return isExists;
    }

    @Override
    public Optional<PhoneNumber> findByNumber(String number) throws SQLException {
        PhoneNumber phoneNumber = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NUMBER_SQL)) {
            preparedStatement.setString(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                phoneNumber = createPhoneNumber(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return Optional.ofNullable(phoneNumber);
    }

    @Override
    public Optional<PhoneNumber> findById(Long id) throws SQLException {
        PhoneNumber phoneNumber = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                phoneNumber = createPhoneNumber(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return Optional.ofNullable(phoneNumber);

    }

    @Override
    public List<PhoneNumber> findAll() throws SQLException {
        List<PhoneNumber> phoneNumberList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                phoneNumberList.add(createPhoneNumber(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return phoneNumberList;
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

    @Override
    public List<PhoneNumber> findAllByEmployeeId(Long employeeId) throws SQLException {
        List<PhoneNumber> phoneNumberList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEEID_SQL)) {
            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                phoneNumberList.add(createPhoneNumber(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return phoneNumberList;
    }
}
