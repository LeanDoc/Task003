package servlet.dto;

public class PositionIncomingDto {
    private String name;

    public PositionIncomingDto() {
    }

    public PositionIncomingDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
