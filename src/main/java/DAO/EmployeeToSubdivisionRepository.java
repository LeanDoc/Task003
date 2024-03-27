package DAO;

import Entity.Employee;
import Entity.EmployeeToSubdivision;
import Entity.Subdivision;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public interface EmployeeToSubdivisionRepository extends Repository<EmployeeToSubdivision, Long> {
    boolean deleteByEmployeeId(Long employeeId) throws SQLException;

    boolean deleteBySubdivisionId(Long subdivisionId) throws SQLException;

    List<EmployeeToSubdivision> findAllByEmployeeId(Long employeeId) throws SQLException;

    List<Subdivision> findSubdivisionsByEmployeeId(Long employeeId) throws SQLException;

    List<EmployeeToSubdivision> findAllBySubdivisionId(Long subdivisionId) throws SQLException;

    List<Employee> findEmployeesBySubdivisionId(Long subdivisionId) throws SQLException;

    Optional<EmployeeToSubdivision> findByEmployeeIdAndSubdivisionId(Long employeeId, Long subdivisionId) throws SQLException;
}
