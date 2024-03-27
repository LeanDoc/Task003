package service;




import servlet.dto.PhoneNumberIncomingDto;
import servlet.dto.PhoneNumberOutGoingDto;
import servlet.dto.PhoneNumberUpdateDto;

import java.sql.SQLException;
import java.util.List;


public interface PhoneNumberService {
    PhoneNumberOutGoingDto save(PhoneNumberIncomingDto phoneNumber) throws SQLException;

    void update(PhoneNumberUpdateDto phoneNumber) throws SQLException;

    PhoneNumberOutGoingDto findById(Long phoneNumberId) throws SQLException;

    List<PhoneNumberOutGoingDto> findAll() throws SQLException;

    boolean delete(Long phoneNumberId) throws SQLException;
}
