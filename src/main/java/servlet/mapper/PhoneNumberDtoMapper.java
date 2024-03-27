package servlet.mapper;


import Entity.PhoneNumber;
import servlet.dto.PhoneNumberIncomingDto;
import servlet.dto.PhoneNumberOutGoingDto;
import servlet.dto.PhoneNumberUpdateDto;


import java.sql.SQLException;
import java.util.List;

public interface PhoneNumberDtoMapper {
    PhoneNumber map(PhoneNumberIncomingDto phoneNumberIncomingDto);

    PhoneNumberOutGoingDto map(PhoneNumber phoneNumber) throws SQLException;

    List<PhoneNumberOutGoingDto> map(List<PhoneNumber> phoneNumberList) throws SQLException;

    List<PhoneNumber> mapUpdateList(List<PhoneNumberUpdateDto> phoneNumberUpdateList);

    PhoneNumber map(PhoneNumberUpdateDto phoneNumberIncomingDto);
}
