package hotel.mapper;

import hotel.dto.CreateServiceDto;
import hotel.dto.ServiceDto;
import hotel.model.Service;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    ServiceDto toDto(Service service);
    Service toEntity(CreateServiceDto dto);
}
