package example.servlet.mapper.impl;

import Entity.Position;
import org.junit.jupiter.api.*;
import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionOutGoingDto;
import servlet.dto.PositionUpdateDto;
import servlet.mapper.PositionDtoMapper;
import servlet.mapper.impl.PositionDtoMapperImpl;

import java.util.List;

class PositionDtoMapperImplTest {
    private static Position position;
    private static PositionIncomingDto positionIncomingDto;
    private static PositionUpdateDto positionUpdateDto;
    private static PositionOutGoingDto positionOutGoingDto;
    private PositionDtoMapper positionDtoMapper;

    @BeforeAll
    static void beforeAll() {
        position = new Position(
                10L,
                "Position for Test"
        );

        positionIncomingDto = new PositionIncomingDto(
                "Incoming servlet.dto"
        );

        positionUpdateDto = new PositionUpdateDto(
                100L,
                "Update servlet.dto"
        );
    }

    @BeforeEach
    void setUp() {
        positionDtoMapper = PositionDtoMapperImpl.getInstance();
    }

    @DisplayName("Position map(PositionIncomingDto")
    @Test
    void mapIncoming() {
        Position resultPosition = positionDtoMapper.map(positionIncomingDto);
        Assertions.assertNull(resultPosition.getId());
        Assertions.assertEquals(positionIncomingDto.getName(), resultPosition.getName());
    }

    @DisplayName("Position map(PositionUpdateDto")
    @Test
    void testMapUpdate() {
        Position resultPosition = positionDtoMapper.map(positionUpdateDto);
        Assertions.assertEquals(positionUpdateDto.getId(), resultPosition.getId());
        Assertions.assertEquals(positionUpdateDto.getName(), resultPosition.getName());
    }

    @DisplayName("PositionOutGoingDto map(Position")
    @Test
    void testMapOutgoing() {
        PositionOutGoingDto resultPosition = positionDtoMapper.map(position);
        Assertions.assertEquals(position.getId(), resultPosition.getId());
        Assertions.assertEquals(position.getName(), resultPosition.getName());
    }


    @DisplayName("List<PositionOutGoingDto> map(List<Position> positionList")
    @Test
    void testMapList() {
        List<PositionOutGoingDto> resultList = positionDtoMapper.map(
                List.of(
                        position,
                        position,
                        position
                )
        );

        Assertions.assertEquals(3, resultList.size());
    }
}
