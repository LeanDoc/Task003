package service;



import exception.NotFoundException;
import servlet.dto.EmployeeIncomingDto;
import servlet.dto.EmployeeOutGoingDto;
import servlet.dto.EmployeeUpdateDto;


import java.sql.SQLException;
import java.util.List;

public interface EmployeeService {
    EmployeeOutGoingDto save(EmployeeIncomingDto employeeDto) throws NotFoundException, SQLException;

    void update(EmployeeUpdateDto employeeDto) throws NotFoundException, SQLException;

    EmployeeOutGoingDto findById(Long employeeId) throws NotFoundException, SQLException;

    List<EmployeeOutGoingDto> findAll() throws NotFoundException, SQLException;

    void delete(Long employeeId) throws NotFoundException, SQLException;
}
