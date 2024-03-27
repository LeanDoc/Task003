package service;


import servlet.dto.SubdivisionIncomingDto;
import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;

import java.sql.SQLException;
import java.util.List;


public interface SubdivisionService {
    SubdivisionOutGoingDto save(SubdivisionIncomingDto subdivision) throws SQLException;

    void update(SubdivisionUpdateDto subdivision) throws SQLException;

    SubdivisionOutGoingDto findById(Long subdivisionId) throws SQLException;

    List<SubdivisionOutGoingDto> findAll() throws SQLException;

    void delete(Long subdivisionId) throws SQLException;

    void deleteEmployeeFromSubdivision(Long subdivisionId, Long employeeId) throws SQLException;

    void addEmployeeToSubdivision(Long subdivisionId, Long employeeId) throws SQLException;
}
