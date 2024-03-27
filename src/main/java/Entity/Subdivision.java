package Entity;

import DAO.EmployeeToSubdivisionRepository;
import DAO.impl.EmployeeToSubdivisionRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class Subdivision {
   // надо еще добавить getInstance от repo
   private static final EmployeeToSubdivisionRepository employeeToSubdivisionRepository  = EmployeeToSubdivisionRepositoryImpl.getInstance();
    private Long id;
    private String name;
    private List<Employee> employeeList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployeeList() throws SQLException {
        if (employeeList == null) {
            employeeList = employeeToSubdivisionRepository.findEmployeesBySubdivisionId(this.id);
        }
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public Subdivision(Long id, String name, List<Employee> employeeList) {
        this.id = id;
        this.name = name;
        this.employeeList = employeeList;
    }

    public Subdivision() {
    }
}
