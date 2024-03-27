package Entity;

/**
 * ManyToMany
 * Employee <-> Subdivision
 */
public class EmployeeToSubdivision {
    private Long id;
    private Long employeeId;
    private Long subdivisionId;

    public EmployeeToSubdivision() {
    }

    public EmployeeToSubdivision(Long id, Long employeeId, Long subdivisionId) {
        this.id = id;
        this.employeeId = employeeId;
        this.subdivisionId = subdivisionId;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getSubdivisionId() {
        return subdivisionId;
    }

    public void setSubdivisionId(Long subdivisionId) {
        this.subdivisionId = subdivisionId;
    }
}
