package hotel.controller;

import hotel.dto.CreateServiceDto;
import hotel.dto.ServiceDto;
import hotel.mapper.ServiceMapper;
import hotel.model.Service;
import hotel.service.interfaces.IServiceManager;
import hotel.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления услугами отеля.
 * Делегирует бизнес-операции в {@link IServiceManager}.
 */
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    private final IServiceManager serviceManager;
    private final ServiceMapper serviceMapper;

    public ServiceController(IServiceManager serviceManager, ServiceMapper serviceMapper) {
        this.serviceManager = serviceManager;
        this.serviceMapper = serviceMapper;
    }

    /**
     * Добавляет новую услугу.
     * @param dto - dto для создания услуги
     * @return добавленная услуга
     */
    @PostMapping
    public ResponseEntity<ServiceDto> addService(@RequestBody CreateServiceDto dto) {
        log.info("Начало обработки команды: addService, dto={}", dto);
        Service service = serviceManager.addService(serviceMapper.toEntity(dto));
        return new ResponseEntity<>(serviceMapper.toDto(service), HttpStatus.CREATED);
    }

    /**
     * Возвращает список услуг с сортировкой.
     * @param option параметр сортировки
     * @return список услуг
     */
    @GetMapping
    public ResponseEntity<List<ServiceDto>> getServices(@RequestParam ServiceSortOption option) {
        log.info("Начало обработки команды: getServices, option={}", option);
        List<Service> services = serviceManager.getSortedServices(option);
        List<ServiceDto> dtos = services.stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
