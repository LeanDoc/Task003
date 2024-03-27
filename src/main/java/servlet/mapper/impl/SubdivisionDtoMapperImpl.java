package servlet.mapper.impl;

import servlet.dto.EmployeeSmallOutGoingDto;
import servlet.dto.SubdivisionIncomingDto;

import servlet.dto.SubdivisionOutGoingDto;
import servlet.dto.SubdivisionUpdateDto;
import servlet.mapper.SubdivisionDtoMapper;
import Entity.Subdivision;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubdivisionDtoMapperImpl implements SubdivisionDtoMapper {
    private static SubdivisionDtoMapper instance;

    private SubdivisionDtoMapperImpl() {
    }


    public static synchronized SubdivisionDtoMapper getInstance() {
        if (instance == null) {
            instance = new SubdivisionDtoMapperImpl();
        }
        return instance;
    }

    @Override
    public Subdivision map(SubdivisionIncomingDto subdivisionIncomingDto) {
        return new Subdivision(
                null,
                subdivisionIncomingDto.getName(),
                null
        );
    }

    @Override
    public SubdivisionOutGoingDto map(Subdivision subdivision) throws SQLException {
        List<EmployeeSmallOutGoingDto> userList = subdivision.getEmployeeList()
                .stream().map(employee -> new EmployeeSmallOutGoingDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName()
                )).toList();

        return new SubdivisionOutGoingDto(
                subdivision.getId(),
                subdivision.getName(),
                userList
        );
    }

    @Override
    public Subdivision map(SubdivisionUpdateDto subdivisionUpdateDto) {
        return new Subdivision(
                subdivisionUpdateDto.getId(),
                subdivisionUpdateDto.getName(),
                null
        );
    }

    @Override
    public List<SubdivisionOutGoingDto> map(List<Subdivision> subdivisionList) throws SQLException {
        List<SubdivisionOutGoingDto> list = new ArrayList<>();
        for (Subdivision subdivision : subdivisionList) {
            SubdivisionOutGoingDto map = map(subdivision);
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Subdivision> mapUpdateList(List<SubdivisionUpdateDto> subdivisionList) {
        return subdivisionList.stream().map(this::map).toList();
    }
}
