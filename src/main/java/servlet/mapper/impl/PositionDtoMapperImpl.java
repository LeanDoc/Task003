package servlet.mapper.impl;


import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionOutGoingDto;
import servlet.dto.PositionUpdateDto;
import servlet.mapper.PositionDtoMapper;
import Entity.Position;


import java.util.List;

public class PositionDtoMapperImpl implements PositionDtoMapper {
    private static PositionDtoMapper instance;

    private PositionDtoMapperImpl() {
    }

    public static synchronized PositionDtoMapper getInstance() {
        if (instance == null) {
            instance = new PositionDtoMapperImpl();
        }
        return instance;
    }

    @Override
    public Position map(PositionIncomingDto positionIncomingDto) {
        return new Position(
                null,
                positionIncomingDto.getName()
        );
    }

    @Override
    public Position map(PositionUpdateDto positionUpdateDto) {
        return new Position(
                positionUpdateDto.getId(),
                positionUpdateDto.getName());
    }

    @Override
    public PositionOutGoingDto map(Position position) {
        return new PositionOutGoingDto(
                position.getId(),
                position.getName()
        );
    }

    @Override
    public List<PositionOutGoingDto> map(List<Position> positionList) {
        return positionList.stream().map(this::map).toList();
    }
}
