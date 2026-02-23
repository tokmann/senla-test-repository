package hotel.mapper;

import hotel.dto.CreateRoomDto;
import hotel.dto.RoomDto;
import hotel.model.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDto toDto(Room room);
    Room toEntity(CreateRoomDto dto);
}
