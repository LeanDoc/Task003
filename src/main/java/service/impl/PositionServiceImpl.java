package service.impl;


import servlet.dto.PositionIncomingDto;
import servlet.dto.PositionOutGoingDto;
import servlet.dto.PositionUpdateDto;
import servlet.mapper.PositionDtoMapper;
import servlet.mapper.impl.PositionDtoMapperImpl;
import Entity.Position;
import DAO.PositionRepository;
import DAO.impl.PositionRepositoryImpl;
import service.PositionService;

import java.sql.SQLException;
import java.util.List;

public class PositionServiceImpl implements PositionService {
    private PositionRepository positionRepository = PositionRepositoryImpl.getInstance();
    private static PositionService instance;
    private final PositionDtoMapper positionDtoMapper = PositionDtoMapperImpl.getInstance();


    private PositionServiceImpl() {
    }

    public static synchronized PositionService getInstance() {
        if (instance == null) {
            instance = new PositionServiceImpl();
        }
        return instance;
    }

    @Override
    public PositionOutGoingDto save(PositionIncomingDto positionDto) throws SQLException {
        Position position = positionDtoMapper.map(positionDto);
        position = positionRepository.save(position);
        return positionDtoMapper.map(position);
    }

    @Override
    public void update(PositionUpdateDto positionUpdateDto) throws SQLException {
        checkPositionExist(positionUpdateDto.getId());
        Position position = positionDtoMapper.map(positionUpdateDto);
        positionRepository.update(position);
    }

    @Override
    public PositionOutGoingDto findById(Long positionId) throws SQLException {
        Position position = positionRepository.findById(positionId).orElseThrow(() ->
                new SQLException("Position not found."));
        return positionDtoMapper.map(position);
    }

    @Override
    public List<PositionOutGoingDto> findAll() throws SQLException {
        List<Position> positionList = positionRepository.findAll();
        return positionDtoMapper.map(positionList);
    }

    @Override
    public boolean delete(Long positionId) throws SQLException {
        checkPositionExist(positionId);
        return positionRepository.deleteById(positionId);
    }

    private void checkPositionExist(Long positionId) throws SQLException {
        if (!positionRepository.exitsById(positionId)) {
            throw new SQLException("Position not found.");
        }
    }
}
