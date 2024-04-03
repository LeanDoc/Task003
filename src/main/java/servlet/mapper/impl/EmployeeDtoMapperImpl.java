package servlet.mapper.impl;


import servlet.dto.EmployeeIncomingDto;
import servlet.dto.EmployeeOutGoingDto;
import servlet.dto.EmployeeUpdateDto;
import servlet.mapper.EmployeeDtoMapper;
import servlet.mapper.PhoneNumberDtoMapper;
import servlet.mapper.PositionDtoMapper;
import servlet.mapper.SubdivisionDtoMapper;
import Entity.Employee;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDtoMapperImpl implements EmployeeDtoMapper {
    private static final PositionDtoMapper positionDtoMapper = PositionDtoMapperImpl.getInstance();
    private static final PhoneNumberDtoMapper phoneNumberDtoMapper = PhoneNumberDtoMapperImpl.getInstance();
    private static final SubdivisionDtoMapper subdivisionDtoMapper = SubdivisionDtoMapperImpl.getInstance();

    private static EmployeeDtoMapper instance;

    private EmployeeDtoMapperImpl() {
    }

    public static synchronized EmployeeDtoMapper getInstance() {
        if (instance == null) {
            instance = new EmployeeDtoMapperImpl();
        }
        return instance;
    }

    @Override
    public Employee map(EmployeeIncomingDto employeeDto) {
        return new Employee(
                null,
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getPosition(),
                null,
                null
        );
    }

    @Override
    public Employee map(EmployeeUpdateDto employeeDto) {
        return new Employee(
                employeeDto.getId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                positionDtoMapper.map(employeeDto.getPosition()),
                phoneNumberDtoMapper.mapUpdateList(employeeDto.getPhoneNumberList()),
                subdivisionDtoMapper.mapUpdateList(employeeDto.getSubdivisionList())
        );
    }

    @Override
    public EmployeeOutGoingDto map(Employee employee) throws SQLException {
        return new EmployeeOutGoingDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                positionDtoMapper.map(employee.getPosition()),
                phoneNumberDtoMapper.map(employee.getPhoneNumberList()),
                subdivisionDtoMapper.map(employee.getSubdivisionList())
        );
    }

    @Override
    public List<EmployeeOutGoingDto> map(List<Employee> employees) throws SQLException {
        List<EmployeeOutGoingDto> list = new ArrayList<>();
        for (Employee employee : employees) {
            EmployeeOutGoingDto map = map(employee);
            list.add(map);
        }
        return list;
    }
}
