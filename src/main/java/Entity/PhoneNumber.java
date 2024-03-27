package Entity;

import DAO.EmployeeRepository;
import DAO.impl.EmployeeRepositoryImpl;

import java.sql.SQLException;

public class PhoneNumber {
        private static final EmployeeRepository employeeRepository  = EmployeeRepositoryImpl.getInstance();
    private Long id;
    private String number;
    private Employee employee;

    public PhoneNumber() {
    }

    public PhoneNumber(Long id, String number, Employee employee) {
        this.id = id;
        this.number = number;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Employee getEmployee() throws SQLException {
        if (employee != null && employee.getId() > 0 && employee.getFirstName() == null) {
            this.employee = employeeRepository.findById(employee.getId()).orElse(employee);
        } else if (employee != null && employee.getId() == 0) {
            this.employee = null;
        }

        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
