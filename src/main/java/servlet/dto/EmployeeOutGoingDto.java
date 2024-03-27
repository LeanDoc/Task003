package servlet.dto;

import java.util.List;

public class EmployeeOutGoingDto {
    private Long id;
    private String firstName;
    private String lastName;

    private PositionOutGoingDto position;
    private List<PhoneNumberOutGoingDto> phoneNumberList;
    private List<SubdivisionOutGoingDto> subdivisionList;

    public EmployeeOutGoingDto() {
    }

    public EmployeeOutGoingDto(Long id, String firstName, String lastName, PositionOutGoingDto position,
                               List<PhoneNumberOutGoingDto> phoneNumberList, List<SubdivisionOutGoingDto> subdivisionList) {
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

    public PositionOutGoingDto getPosition() {
        return position;
    }


    public List<PhoneNumberOutGoingDto> getPhoneNumberList() {
        return phoneNumberList;
    }

    public List<SubdivisionOutGoingDto> getSubdivisionList() {
        return subdivisionList;
    }

}
