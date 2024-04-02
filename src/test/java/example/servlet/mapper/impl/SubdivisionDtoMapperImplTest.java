package example.servlet.mapper.impl;

import Entity.Employee;
import Entity.Subdivision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servlet.dto.SubdivisionIncomingDto;
import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;
import servlet.mapper.SubdivisionDtoMapper;
import servlet.mapper.impl.SubdivisionDtoMapperImpl;

import java.sql.SQLException;
import java.util.List;

class SubdivisionDtoMapperImplTest {
    private SubdivisionDtoMapper subdivisionDtoMapper;

    @BeforeEach
    void setUp() {
        subdivisionDtoMapper = SubdivisionDtoMapperImpl.getInstance();
    }

    @DisplayName("Subdivision map(SubdivisionIncomingDto")
    @Test
    void mapIncoming() {
        SubdivisionIncomingDto dto = new SubdivisionIncomingDto("New Subdivision");
        Subdivision result = subdivisionDtoMapper.map(dto);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
    }

    @DisplayName("SubdivisionOutGoingDto map(Subdivision")
    @Test
    void testMapOutgoing() throws SQLException {
        Subdivision subdivision = new Subdivision(100L, "Subdivision #100", List.of(new Employee(), new Employee()));

        SubdivisionOutGoingDto result = subdivisionDtoMapper.map(subdivision);

        Assertions.assertEquals(subdivision.getId(), result.getId());
        Assertions.assertEquals(subdivision.getName(), result.getName());
        Assertions.assertEquals(subdivision.getEmployeeList().size(), result.getEmployeeList().size());
    }

    @DisplayName("Subdivision map(SubdivisionUpdateDto")
    @Test
    void testMapUpdate() {
        SubdivisionUpdateDto dto = new SubdivisionUpdateDto(10L, "Update name.");

        Subdivision result = subdivisionDtoMapper.map(dto);
        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
    }

    @DisplayName("List<SubdivisionOutGoingDto> map(List<Subdivision>")
    @Test
    void testMap2() throws SQLException {
        List<Subdivision> subdivisionList = List.of(
                new Subdivision(1L, "dep 1", List.of()),
                new Subdivision(2L, "dep 2", List.of()),
                new Subdivision(3L, "dep 3", List.of())
        );

        List<SubdivisionOutGoingDto> result = subdivisionDtoMapper.map(subdivisionList);

        Assertions.assertEquals(3, result.size());
    }

    @DisplayName("List<SubdivisionO> mapUpdateList(List<SubdivisionOUpdateDto>")
    @Test
    void mapUpdateList() {
        List<SubdivisionUpdateDto> subdivisionList = List.of(
                new SubdivisionUpdateDto(),
                new SubdivisionUpdateDto(),
                new SubdivisionUpdateDto()
        );

        List<Subdivision> result = subdivisionDtoMapper.mapUpdateList(subdivisionList);

        Assertions.assertEquals(3, result.size());
    }
}
