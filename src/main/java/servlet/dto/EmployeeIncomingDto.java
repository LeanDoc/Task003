package servlet.dto;


import Entity.Position;

public class EmployeeIncomingDto {
    private String firstName;
    private String lastName;

    private Position position;

    public EmployeeIncomingDto() {
    }

    public EmployeeIncomingDto(String firstName, String lastName, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Position getPosition() {
        return position;
    }
}
