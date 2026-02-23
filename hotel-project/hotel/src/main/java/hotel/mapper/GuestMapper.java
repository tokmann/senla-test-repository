package hotel.mapper;

import hotel.dto.CreateGuestDto;
import hotel.dto.GuestDto;
import hotel.model.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    @Mapping(source = "room.number", target = "roomNumber")
    @Mapping(target = "servicesCount", expression = "java(guest.getServices() != null ? guest.getServices().size() : 0)")
    GuestDto toDto(Guest guest);

    Guest toEntity(CreateGuestDto dto);
}
