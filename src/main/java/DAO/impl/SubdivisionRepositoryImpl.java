package DAO.impl;

import connection.ConnectionManagerImpl;
import Entity.Subdivision;
import DAO.SubdivisionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubdivisionRepositoryImpl implements SubdivisionRepository {
    private static final String SAVE_SQL = """
            INSERT INTO subdivisions (subdivision_name)
            VALUES (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE subdivisions
            SET subdivision_name = ?
            WHERE subdivision_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM subdivisions
            WHERE subdivision_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT subdivision_id, subdivision_name FROM subdivisions
            WHERE subdivision_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT subdivision_id, subdivision_name FROM subdivisions;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM subdivisions
                        WHERE subdivision_id = ?
                        LIMIT 1);
            """;
    private static SubdivisionRepository instance;
    private final ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();

    private SubdivisionRepositoryImpl() {
    }

    public static synchronized SubdivisionRepository getInstance() {
        if (instance == null) {
            instance = new SubdivisionRepositoryImpl();
        }
        return instance;
    }

    private static Subdivision createSubdivision(ResultSet resultSet) throws SQLException {
        Subdivision subdivision;
        subdivision = new Subdivision(
                resultSet.getLong("subdivision_id"),
                resultSet.getString("subdivision_name"),
                null);
        return subdivision;
    }

    @Override
    public Subdivision save(Subdivision subdivision) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, subdivision.getName());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                subdivision = new Subdivision(
                        resultSet.getLong("subdivision_id"),
                        subdivision.getName(),
                        null
                );
                subdivision.getEmployeeList();
            }
        } catch (SQLException e) {
            throw new SQLException (e);
        }

        return subdivision;
    }

    @Override
    public void update(Subdivision subdivision) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, subdivision.getName());
            preparedStatement.setLong(2, subdivision.getId());

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
    public Optional<Subdivision> findById(Long id) throws SQLException {
        Subdivision subdivision = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                subdivision = createSubdivision(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return Optional.ofNullable(subdivision);
    }

    @Override
    public List<Subdivision> findAll() throws SQLException {
        List<Subdivision> positionList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                positionList.add(createSubdivision(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return positionList;
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
}
