package servlet.mapper;

import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionOutGoingDto;
import servlet.dto.PositionUpdateDto;
import Entity.Position;


import java.util.List;

public interface PositionDtoMapper {
    Position map(PositionIncomingDto positionIncomingDto);

    Position map(PositionUpdateDto positionUpdateDto);

    PositionOutGoingDto map(Position position);

    List<PositionOutGoingDto> map(List<Position> positionList);
}
