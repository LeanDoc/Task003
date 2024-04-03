package servlet.dto;

public class SubdivisionUpdateDto {
    private Long id;
    private String name;

    public SubdivisionUpdateDto() {
    }

    public SubdivisionUpdateDto(Long id, String name) {
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
