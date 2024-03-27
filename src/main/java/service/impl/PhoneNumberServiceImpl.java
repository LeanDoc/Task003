package service.impl;


import servlet.dto.PhoneNumberIncomingDto;
import servlet.dto.PhoneNumberOutGoingDto;
import servlet.dto.PhoneNumberUpdateDto;
import servlet.mapper.PhoneNumberDtoMapper;
import servlet.mapper.impl.PhoneNumberDtoMapperImpl;
import Entity.PhoneNumber;
import DAO.PhoneNumberRepository;
import DAO.impl.PhoneNumberRepositoryImpl;
import service.PhoneNumberService;

import java.sql.SQLException;
import java.util.List;

public class PhoneNumberServiceImpl implements PhoneNumberService {
    private final PhoneNumberDtoMapper phoneNumberDtoMapper = PhoneNumberDtoMapperImpl.getInstance();
    private static PhoneNumberService instance;
    private final PhoneNumberRepository phoneNumberRepository = PhoneNumberRepositoryImpl.getInstance();


    private PhoneNumberServiceImpl() {
    }

    public static synchronized PhoneNumberService getInstance() {
        if (instance == null) {
            instance = new PhoneNumberServiceImpl();
        }
        return instance;
    }

    @Override
    public PhoneNumberOutGoingDto save(PhoneNumberIncomingDto phoneNumberDto) throws SQLException {
        PhoneNumber phoneNumber = phoneNumberDtoMapper.map(phoneNumberDto);
        phoneNumber = phoneNumberRepository.save(phoneNumber);
        return phoneNumberDtoMapper.map(phoneNumber);
    }

    @Override
    public void update(PhoneNumberUpdateDto phoneNumberUpdateDto) throws SQLException {
        if (phoneNumberRepository.exitsById(phoneNumberUpdateDto.getId())) {
            PhoneNumber phoneNumber = phoneNumberDtoMapper.map(phoneNumberUpdateDto);
            phoneNumberRepository.update(phoneNumber);
        } else {
            throw new SQLException("PhoneNumber not found.");
        }
    }

    @Override
    public PhoneNumberOutGoingDto findById(Long phoneNumberId) throws SQLException {
        PhoneNumber phoneNumber = phoneNumberRepository.findById(phoneNumberId).orElseThrow(() ->
                new SQLException("PhoneNumber not found."));
        return phoneNumberDtoMapper.map(phoneNumber);
    }

    @Override
    public List<PhoneNumberOutGoingDto> findAll() throws SQLException {
        List<PhoneNumber> phoneNumberList = phoneNumberRepository.findAll();
        return phoneNumberDtoMapper.map(phoneNumberList);
    }

    @Override
    public boolean delete(Long phoneNumberId) throws SQLException {
        return phoneNumberRepository.deleteById(phoneNumberId);
    }

}
