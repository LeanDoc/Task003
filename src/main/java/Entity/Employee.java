package Entity;

import DAO.EmployeeToSubdivisionRepository;
import DAO.PhoneNumberRepository;
import DAO.impl.EmployeeToSubdivisionRepositoryImpl;
import DAO.impl.PhoneNumberRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * Сущность Employee
 * Relation:
 * One To Many: Employee -> PhoneNumber
 * Many To Many: Employee <-> Subdivision
 * Many To One: Employee -> Position
 */
public class Employee {

    private static final PhoneNumberRepository phoneNumberRepository = PhoneNumberRepositoryImpl.getInstance();
    private static final EmployeeToSubdivisionRepository employeeToSubdivisionRepository = EmployeeToSubdivisionRepositoryImpl.getInstance();
    private Long id;
    private String firstName;
    private String lastName;
    private Position position;
    private List<PhoneNumber> phoneNumberList;
    private List<Subdivision> subdivisionList;

    public Employee() {
    }

    public Employee(Long id, String firstName, String lastName, Position position, List<PhoneNumber> phoneNumberList, List<Subdivision> subdivisionList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.phoneNumberList = phoneNumberList;
        this.subdivisionList = subdivisionList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<PhoneNumber> getPhoneNumberList() throws SQLException {
        if (phoneNumberList == null) {
            this.phoneNumberList = phoneNumberRepository.findAllByEmployeeId(this.id);
        }
        return phoneNumberList;
    }

    public void setPhoneNumberList(List<PhoneNumber> phoneNumberList) {
        this.phoneNumberList = phoneNumberList;
    }

    public List<Subdivision> getSubdivisionList() throws SQLException {
        if (subdivisionList == null) {
            subdivisionList = employeeToSubdivisionRepository.findSubdivisionsByEmployeeId(this.id);
        }
        return subdivisionList;
    }


    public void setSubdivisionList(List<Subdivision> subdivisionList) {
        this.subdivisionList = subdivisionList;
    }
}
