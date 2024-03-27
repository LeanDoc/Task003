package service.impl;
import com.github.dockerjava.api.exception.NotFoundException;
import servlet.dto.EmployeeIncomingDto;
import servlet.dto.EmployeeOutGoingDto;
import servlet.dto.EmployeeUpdateDto;
import servlet.mapper.EmployeeDtoMapper;
import servlet.mapper.impl.EmployeeDtoMapperImpl;
import Entity.Employee;
import DAO.EmployeeRepository;
import DAO.impl.EmployeeRepositoryImpl;
import service.EmployeeService;
import java.sql.SQLException;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private static final EmployeeDtoMapper employeeDtoMapper = EmployeeDtoMapperImpl.getInstance();
    private static EmployeeService instance;


    private EmployeeServiceImpl() {
    }

    public static synchronized EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeServiceImpl();
        }
        return instance;
    }

    private void checkExistEmployee(Long employeeId) throws SQLException {
        if (!employeeRepository.exitsById(employeeId)) {
            throw new NotFoundException("Employee not found.");
        }
    }

    @Override
    public EmployeeOutGoingDto save(EmployeeIncomingDto employeeDto) throws SQLException {
        Employee employee = employeeRepository.save(employeeDtoMapper.map(employeeDto));
        return employeeDtoMapper.map(employeeRepository.findById(employee.getId()).orElse(employee));
    }

    @Override
    public void update(EmployeeUpdateDto employeeDto) throws SQLException {
        if (employeeDto == null || employeeDto.getId() == null) {
            throw new IllegalArgumentException();
        }
        checkExistEmployee(employeeDto.getId());
        employeeRepository.update(employeeDtoMapper.map(employeeDto));
    }

    @Override
    public EmployeeOutGoingDto findById(Long employeeId) throws SQLException {
        checkExistEmployee(employeeId);
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return employeeDtoMapper.map(employee);
    }

    @Override
    public List<EmployeeOutGoingDto> findAll() throws SQLException {
        List<Employee> all = employeeRepository.findAll();
        return employeeDtoMapper.map(all);
    }

    @Override
    public void delete(Long employeeId) throws SQLException {
        checkExistEmployee(employeeId);
        employeeRepository.deleteById(employeeId);
    }
}
