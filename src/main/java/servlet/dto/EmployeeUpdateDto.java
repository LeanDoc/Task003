package servlet.dto;

import java.util.List;

public class EmployeeUpdateDto {
    private Long id;
    private String firstName;
    private String lastName;
    private PositionUpdateDto position;
    private List<PhoneNumberUpdateDto> phoneNumberList;
    private List<SubdivisionUpdateDto> subdivisionList;

    public EmployeeUpdateDto() {
    }

    public EmployeeUpdateDto(Long id, String firstName, String lastName, PositionUpdateDto position,
                             List<PhoneNumberUpdateDto> phoneNumberList, List<SubdivisionUpdateDto> subdivisionList) {
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public PositionUpdateDto getPosition() {
        return position;
    }

    public List<PhoneNumberUpdateDto> getPhoneNumberList() {
        return phoneNumberList;
    }

    public List<SubdivisionUpdateDto> getSubdivisionList() {
        return subdivisionList;
    }

}

