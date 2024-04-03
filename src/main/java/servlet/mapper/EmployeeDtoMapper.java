package servlet.mapper;


import Entity.Employee;
import servlet.dto.EmployeeIncomingDto;
import servlet.dto.EmployeeOutGoingDto;
import servlet.dto.EmployeeUpdateDto;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDtoMapper {
    Employee map(EmployeeIncomingDto employeeIncomingDto);

    Employee map(EmployeeUpdateDto employeeIncomingDto);

    EmployeeOutGoingDto map(Employee employee) throws SQLException;

    List<EmployeeOutGoingDto> map(List<Employee> employee) throws SQLException;
}
