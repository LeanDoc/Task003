package servlet.dto;

public class PositionOutGoingDto {
    private Long id;
    private String name;

    public PositionOutGoingDto() {
    }

    public PositionOutGoingDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
