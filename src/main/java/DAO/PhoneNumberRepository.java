package DAO;

import Entity.PhoneNumber;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PhoneNumberRepository extends Repository<PhoneNumber, Long> {
    List<PhoneNumber> findAllByEmployeeId(Long employeeId) throws SQLException;

    boolean deleteByEmployeeId(Long employeeId) throws SQLException;

    boolean existsByNumber(String number) throws SQLException;

    Optional<PhoneNumber> findByNumber(String number) throws SQLException;
    List<PhoneNumber> findAll() throws SQLException;
}
