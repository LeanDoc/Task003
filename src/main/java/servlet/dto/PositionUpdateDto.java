package servlet.dto;

public class PositionUpdateDto {
    private Long id;
    private String name;

    public PositionUpdateDto() {
    }

    public PositionUpdateDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
