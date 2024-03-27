package servlet.mapper;

import servlet.dto.SubdivisionIncomingDto;
import Entity.Subdivision;
import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;


import java.sql.SQLException;
import java.util.List;

public interface SubdivisionDtoMapper {
    Subdivision map(SubdivisionIncomingDto subdivisionIncomingDto);

    SubdivisionOutGoingDto map(Subdivision subdivision) throws SQLException;

    Subdivision map(SubdivisionUpdateDto subdivisionUpdateDto);

    List<SubdivisionOutGoingDto> map(List<Subdivision> subdivisionList) throws SQLException;

    List<Subdivision> mapUpdateList(List<SubdivisionUpdateDto> subdivisionList);
}
