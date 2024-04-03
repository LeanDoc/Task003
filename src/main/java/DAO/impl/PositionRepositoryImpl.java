package DAO.impl;

import connection.ConnectionManagerImpl;
import Entity.Position;
import DAO.PositionRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionRepositoryImpl implements PositionRepository {
    private static final String SAVE_SQL = """
            INSERT INTO positions (position_name)
            VALUES (?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE positions
            SET position_name = ?
            WHERE position_id = ?  ;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM positions
            WHERE position_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT position_id, position_name FROM positions
            WHERE position_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT position_id, position_name FROM positions ;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM positions
                        WHERE position_id = ?
                        LIMIT 1);
            """;
    private static PositionRepository instance;
    private final ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();

    private PositionRepositoryImpl() {
    }

    public static synchronized PositionRepository getInstance() {
        if (instance == null) {
            instance = new PositionRepositoryImpl();
        }
        return instance;
    }

    private static Position createPosition(ResultSet resultSet) throws SQLException {
        Position position;
        position = new Position(resultSet.getLong("position_id"),
                resultSet.getString("position_name"));
        return position;
    }

    @Override
    public Position save(Position position) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, position.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                position = new Position(
                        resultSet.getLong("position_id"),
                        position.getName());
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return position;
    }

    @Override
    public void update(Position position) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {
            preparedStatement.setString(1, position.getName());
            preparedStatement.setLong(2, position.getId());
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
    public Optional<Position> findById(Long id) throws SQLException {
        Position position = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                position = createPosition(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return Optional.ofNullable(position);
    }

    @Override
    public List<Position> findAll() throws SQLException {
        List<Position> positionList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                positionList.add(createPosition(resultSet));
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
