package hotel.controller;

import hotel.dto.CheckInDto;
import hotel.dto.CreateGuestDto;
import hotel.dto.GuestDto;
import hotel.dto.ServiceDto;
import hotel.exceptions.guests.GuestNotFoundException;
import hotel.mapper.GuestMapper;
import hotel.mapper.ServiceMapper;
import hotel.model.Guest;
import hotel.model.Service;
import hotel.service.interfaces.IGuestManager;
import hotel.enums.GuestSortOption;
import hotel.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки команд, связанных с гостями отеля.
 * Отвечает за приём пользовательских команд и делегирует
 * бизнес-логику в слой {@link IGuestManager}.
 */
@RestController
@RequestMapping("api/guests")
public class GuestController {

    private static final Logger log = LoggerFactory.getLogger(GuestController.class);

    private final IGuestManager guestManager;
    private final GuestMapper guestMapper;
    private final ServiceMapper serviceMapper;

    public GuestController(IGuestManager guestManager, GuestMapper guestMapper, ServiceMapper serviceMapper) {
        this.guestManager = guestManager;
        this.guestMapper = guestMapper;
        this.serviceMapper = serviceMapper;
    }

    /**
     * Регистрирует нового гостя в системе.
     * @param dto - DTO для регистрации гостя
     * @return зарегистрированный гость
     */
    @PostMapping
    public ResponseEntity<GuestDto> registerGuest(@RequestBody CreateGuestDto dto) {
        log.info("Начало обработки команды: registerGuest, dto={}", dto);
        Guest guest = guestManager.addGuest(guestMapper.toEntity(dto));
        return new ResponseEntity<>(guestMapper.toDto(guest), HttpStatus.CREATED);
    }

    /**
     * Заселяет гостя в указанный номер на заданный период.
     * @param dto - DTO для заселения гостя
     * @return true, если заселение прошло успешно
     */
    @PostMapping("/check-in")
    public ResponseEntity<Boolean> checkInGuest(@RequestBody CheckInDto dto) {
        log.info("Начало обработки команды: checkInGuest, dto={}", dto);
        boolean result = guestManager.checkInGuest(
                dto.getGuestId(),
                dto.getRoomNumber(),
                dto.getCheckIn(),
                dto.getCheckOut()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Выписывает гостя из номера.
     * @param guestId идентификатор гостя
     */
    @PostMapping("/{guestId}/check-out")
    public ResponseEntity<Boolean> checkOutGuest(@PathVariable long guestId) {
        log.info("Начало обработки команды: checkOutGuest, guestId={}", guestId);
        boolean result = guestManager.checkOutGuest(guestId);
        return ResponseEntity.ok(result);
    }

    /**
     * Возвращает список гостей, отсортированных по заданному критерию.
     * @param option параметр сортировки
     * @return список гостей
     */
    @GetMapping("/sorted")
    public ResponseEntity<List<GuestDto>> getSortedGuests(@RequestParam GuestSortOption option) {
        log.info("Начало обработки команды: getSortedGuests, option={}", option);
        List<Guest> guests = guestManager.getSortedGuests(option);
        List<GuestDto> dtos = guests.stream()
                .map(guestMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Находит гостя по полному имени.
     * @param fullName полное имя гостя
     * @return найденный гость
     */
    @GetMapping("/search")
    public ResponseEntity<GuestDto> findGuestByFullName(@RequestParam String fullName) {
        log.info("Начало обработки команды: findGuestByFullName, fullName={}", fullName);
        Guest guest = guestManager.findGuestByFullName(fullName);
        if (guest == null) {
            throw new GuestNotFoundException("Гость не найден: " + fullName);
        }
        return ResponseEntity.ok(guestMapper.toDto(guest));
    }

    /**
     * Возвращает список услуг, оказанных гостю, с сортировкой.
     * @param guestId id гостя
     * @param option параметр сортировки услуг
     * @return список услуг
     */
    @GetMapping("/{guestId}/services")
    public ResponseEntity<List<ServiceDto>> getGuestServices(@PathVariable long guestId, @RequestParam ServiceSortOption option) {
        log.info("Начало обработки команды: getGuestServices, guestId={}, option={}", guestId, option);
        Guest guest = guestManager.getGuestById(guestId);
        if (guest == null) {
            throw new GuestNotFoundException(guestId);
        }
        List<Service> services = guestManager.getSortedGuestServices(guest, option);
        List<ServiceDto> dtos = services.stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Возвращает общее количество зарегистрированных гостей.
     * @return количество гостей
     */
    @GetMapping("/count")
    public int countGuests() {
        log.info("Начало обработки команды: countGuests");
        return guestManager.countGuests();
    }

    /**
     * Добавляет услугу гостю по имени гостя и названию услуги.
     * @param guestFullName полное имя гостя
     * @param serviceName   название услуги
     */
    @PostMapping("/{guestFullName}/services/{serviceName}")
    public ResponseEntity<Boolean> addServiceToGuestByName(@PathVariable String guestFullName, @PathVariable String serviceName) {
        log.info("Начало обработки команды: addServiceToGuestByName, fullName={}, service={}",
                guestFullName, serviceName);
        boolean result = guestManager.addServiceToGuestByName(guestFullName, serviceName);
        return ResponseEntity.ok(result);
    }
}
