package service;


import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionOutGoingDto;
import servlet.dto.PositionUpdateDto;

import java.sql.SQLException;
import java.util.List;


public interface PositionService {
    PositionOutGoingDto save(PositionIncomingDto position) throws SQLException;

    void update(PositionUpdateDto position) throws SQLException;

    PositionOutGoingDto findById(Long positionId) throws SQLException;

    List<PositionOutGoingDto> findAll() throws SQLException;

    boolean delete(Long positionId) throws SQLException;
}
