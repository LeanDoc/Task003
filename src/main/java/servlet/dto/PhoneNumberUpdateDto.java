package servlet.dto;


public class PhoneNumberUpdateDto {
    private Long id;
    private String number;
    private Long employeeId;

    public PhoneNumberUpdateDto() {
    }

    public PhoneNumberUpdateDto(Long id, String number, Long employeeId) {
        this.id = id;
        this.number = number;
        this.employeeId = employeeId;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Long getEmployeeId() {
        return employeeId;
    }
}
