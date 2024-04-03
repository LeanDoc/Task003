package service.impl;


import servlet.dto.SubdivisionIncomingDto;
import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;
import servlet.mapper.SubdivisionDtoMapper;
import servlet.mapper.impl.SubdivisionDtoMapperImpl;
import Entity.EmployeeToSubdivision;
import Entity.Subdivision;
import DAO.EmployeeRepository;
import DAO.EmployeeToSubdivisionRepository;
import DAO.SubdivisionRepository;
import DAO.impl.EmployeeRepositoryImpl;
import DAO.impl.EmployeeToSubdivisionRepositoryImpl;
import DAO.impl.SubdivisionRepositoryImpl;
import service.SubdivisionService;

import java.sql.SQLException;
import java.util.List;

public class SubdivisionServiceImpl implements SubdivisionService {
    private final SubdivisionRepository subdivisionRepository = SubdivisionRepositoryImpl.getInstance();
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private final EmployeeToSubdivisionRepository employeeToSubdivisionRepository = EmployeeToSubdivisionRepositoryImpl.getInstance();
    private static final SubdivisionDtoMapper employeeDtoMapper = SubdivisionDtoMapperImpl.getInstance();
    private static SubdivisionService instance;

    private SubdivisionServiceImpl() {
    }

    public static synchronized SubdivisionService getInstance() {
        if (instance == null) {
            instance = new SubdivisionServiceImpl();
        }
        return instance;
    }

    private void checkExistSubdivision(Long subdivisionId) throws SQLException {
        if (!subdivisionRepository.exitsById(subdivisionId)) {
            throw new SQLException("Subdivision not found.");
        }
    }

    @Override
    public SubdivisionOutGoingDto save(SubdivisionIncomingDto subdivisionDto) throws SQLException {
        Subdivision subdivision = employeeDtoMapper.map(subdivisionDto);
        subdivision = subdivisionRepository.save(subdivision);
        return employeeDtoMapper.map(subdivision);
    }

    @Override
    public void update(SubdivisionUpdateDto subdivisionUpdateDto) throws SQLException {
        checkExistSubdivision(subdivisionUpdateDto.getId());
        Subdivision subdivision = employeeDtoMapper.map(subdivisionUpdateDto);
        subdivisionRepository.update(subdivision);
    }

    @Override
    public SubdivisionOutGoingDto findById(Long subdivisionId) throws SQLException {
        Subdivision subdivision = subdivisionRepository.    findById(subdivisionId).orElseThrow(() ->
                new SQLException("Subdivision not found."));
        return employeeDtoMapper.map(subdivision);
    }

    @Override
    public List<SubdivisionOutGoingDto> findAll() throws SQLException {
        List<Subdivision> subdivisionList = subdivisionRepository.findAll();
        return employeeDtoMapper.map(subdivisionList);
    }

    @Override
    public void delete(Long subdivisionId) throws SQLException {
        checkExistSubdivision(subdivisionId);
        subdivisionRepository.deleteById(subdivisionId);
    }

    @Override
    public void deleteEmployeeFromSubdivision(Long subdivisionId, Long employeeId) throws SQLException {
        checkExistSubdivision(subdivisionId);
        if (employeeRepository.exitsById(employeeId)) {
            EmployeeToSubdivision linkEmployeeSubdivision = employeeToSubdivisionRepository.findByEmployeeIdAndSubdivisionId(employeeId, subdivisionId)
                    .orElseThrow(() -> new SQLException("Link many to many Not found."));

            employeeToSubdivisionRepository.deleteById(linkEmployeeSubdivision.getId());
        } else {
            throw new SQLException("Employee not found.");
        }

    }

    @Override
    public void addEmployeeToSubdivision(Long subdivisionId, Long employeeId) throws SQLException {
        checkExistSubdivision(subdivisionId);
        if (employeeRepository.exitsById(employeeId)) {
            EmployeeToSubdivision linkEmployeeSubdivision = new EmployeeToSubdivision(
                    null,
                    employeeId,
                    subdivisionId
            );
            employeeToSubdivisionRepository.save(linkEmployeeSubdivision);
        } else {
            throw new SQLException("Employee not found.");
        }

    }

}
