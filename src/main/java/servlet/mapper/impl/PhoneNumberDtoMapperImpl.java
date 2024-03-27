package servlet.mapper.impl;

import servlet.dto.EmployeeSmallOutGoingDto;
import servlet.dto.PhoneNumberIncomingDto;
import servlet.dto.PhoneNumberOutGoingDto;
import servlet.dto.PhoneNumberUpdateDto;
import servlet.mapper.PhoneNumberDtoMapper;
import Entity.Employee;
import Entity.PhoneNumber;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhoneNumberDtoMapperImpl implements PhoneNumberDtoMapper {
    private static PhoneNumberDtoMapper instance;

    private PhoneNumberDtoMapperImpl() {
    }

    public static synchronized PhoneNumberDtoMapper getInstance() {
        if (instance == null) {
            instance = new PhoneNumberDtoMapperImpl();
        }
        return instance;
    }

    @Override
    public PhoneNumber map(PhoneNumberIncomingDto phoneDto) {
        return new PhoneNumber(
                null,
                phoneDto.getNumber(),
                null
        );
    }

    @Override
    public PhoneNumberOutGoingDto map(PhoneNumber phoneNumber) throws SQLException {
        return new PhoneNumberOutGoingDto(
                phoneNumber.getId(),
                phoneNumber.getNumber(),
                phoneNumber.getEmployee() == null ?
                        null :
                        new EmployeeSmallOutGoingDto(
                                phoneNumber.getEmployee().getId(),
                                phoneNumber.getEmployee().getFirstName(),
                                phoneNumber.getEmployee().getLastName()
                        )
        );
    }

    @Override
    public List<PhoneNumberOutGoingDto> map(List<PhoneNumber> phoneNumberList) throws SQLException {
        List<PhoneNumberOutGoingDto> list = new ArrayList<>();
        for (PhoneNumber phoneNumber : phoneNumberList) {
            PhoneNumberOutGoingDto map = map(phoneNumber);
            list.add(map);
        }
        return list;
    }

    @Override
    public List<PhoneNumber> mapUpdateList(List<PhoneNumberUpdateDto> phoneNumberUpdateList) {
        return phoneNumberUpdateList.stream().map(this::map).toList();
    }

    @Override
    public PhoneNumber map(PhoneNumberUpdateDto phoneDto) {
        return new PhoneNumber(
                phoneDto.getId(),
                phoneDto.getNumber(),
                new Employee(
                        phoneDto.getEmployeeId(),
                        null,
                        null,
                        null,
                        List.of(),
                        List.of()
                )
        );
    }

}
