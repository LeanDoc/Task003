package example.servlet.mapper.impl;

import Entity.Employee;
import Entity.PhoneNumber;
import Entity.Position;
import Entity.Subdivision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servlet.dto.*;
import servlet.mapper.EmployeeDtoMapper;
import servlet.mapper.impl.EmployeeDtoMapperImpl;

import java.sql.SQLException;
import java.util.List;

class EmployeeDtoMapperImplTest {
    private EmployeeDtoMapper employeeDtoMapper;

    @BeforeEach
    void setUp() {
        employeeDtoMapper = EmployeeDtoMapperImpl.getInstance();
    }

    @DisplayName("Employee map(EmployeeIncomingDto")
    @Test
    void mapIncoming() {
        EmployeeIncomingDto dto = new EmployeeIncomingDto(
                "f1",
                "l2",
                new Position(1L, "position1")
        );
        Employee result = employeeDtoMapper.map(dto);
        Assertions.assertNull(result.getId());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getPosition().getId(), result.getPosition().getId());
    }

    @DisplayName("Employee map(EmployeeUpdateDto")
    @Test
    void testMapUpdate() throws SQLException {
        EmployeeUpdateDto dto = new EmployeeUpdateDto(
                100L,
                "f1",
                "l2",
                new PositionUpdateDto(1L, "Position update"),
                List.of(new PhoneNumberUpdateDto()),
                List.of(new SubdivisionUpdateDto())
        );
        Employee result = employeeDtoMapper.map(dto);
        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getPosition().getId(), result.getPosition().getId());
        Assertions.assertEquals(dto.getPhoneNumberList().size(), result.getPhoneNumberList().size());
        Assertions.assertEquals(dto.getSubdivisionList().size(), result.getSubdivisionList().size());
    }

    @DisplayName("EmployeeOutGoingDto map(Employee")
    @Test
    void testMapOutgoing() throws SQLException {
        Employee employee = new Employee(
                100L,
                "f1",
                "l2",
                new Position (1L, "Position #1"),
                List.of(new PhoneNumber(1L, "1324", null)),
                List.of(new Subdivision(1L, "d1", List.of()))
        );
        EmployeeOutGoingDto result = employeeDtoMapper.map(employee);
        Assertions.assertEquals(employee.getId(), result.getId());
        Assertions.assertEquals(employee.getFirstName(), result.getFirstName());
        Assertions.assertEquals(employee.getLastName(), result.getLastName());
        Assertions.assertEquals(employee.getPosition().getId(), result.getPosition().getId());
        Assertions.assertEquals(employee.getPhoneNumberList().size(), result.getPhoneNumberList().size());
        Assertions.assertEquals(employee.getSubdivisionList().size(), result.getSubdivisionList().size());
    }

    @DisplayName("List<EmployeeOutGoingDto> map(List<Employee>")
    @Test
    void testMapList() throws SQLException {
        List<Employee> employeeList = List.of(
                new Employee(
                        100L,
                        "f1",
                        "l2",
                        new Position(1L, "Position #1"),
                        List.of(new PhoneNumber(1L, "1324", null)),
                        List.of(new Subdivision(1L, "d1", List.of()))
                ),
                new Employee(
                        101L,
                        "f3",
                        "l4",
                        new Position(1L, "Position #1"),
                        List.of(new PhoneNumber(2L, "24242", null)),
                        List.of(new Subdivision(2L, "d2", List.of()))
                )
        );
        int result = employeeDtoMapper.map(employeeList).size();
        Assertions.assertEquals(employeeList.size(), result);
    }
}
