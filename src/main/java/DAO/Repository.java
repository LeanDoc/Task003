package DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    T save(T t) throws SQLException;

    void update(T t) throws SQLException;

    boolean deleteById(K id) throws SQLException;

    Optional<T> findById(K id) throws SQLException;

    List<T> findAll() throws SQLException;

    boolean exitsById(K id) throws SQLException;
}
