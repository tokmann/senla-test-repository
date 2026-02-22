package hotel.controller;

import hotel.dto.CreateRoomDto;
import hotel.dto.RoomDto;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.mapper.RoomMapper;
import hotel.model.Room;
import hotel.service.interfaces.IRoomManager;
import hotel.enums.RoomSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки команд, связанных с номерами отеля.
 * Делегирует бизнес-логику в {@link IRoomManager}.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    private final IRoomManager roomManager;
    private final RoomMapper roomMapper;

    public RoomController(IRoomManager roomManager, RoomMapper roomMapper) {
        this.roomManager = roomManager;
        this.roomMapper = roomMapper;
    }

    /**
     * Возвращает список всех номеров с сортировкой.
     * @param option параметр сортировки
     * @return список номеров
     */
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms(@RequestParam RoomSortOption option) {
        log.info("Начало обработки команды: getAllRooms, option={}", option);
        List<Room> rooms = roomManager.getSortedRooms(option);
        List<RoomDto> dtos = rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Возвращает список свободных номеров.
     * @param option параметр сортировки
     * @return список свободных номеров
     */
    @GetMapping("/free")
    public ResponseEntity<List<RoomDto>> getFreeRooms(@RequestParam RoomSortOption option) {
        log.info("Начало обработки команды: getFreeRooms, option={}", option);
        List<Room> rooms = roomManager.getFreeRooms(option);
        List<RoomDto> dtos = rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Находит номера, которые будут свободны на указанную дату.
     * @param date дата
     * @return список номеров
     */
    @GetMapping("/available/{date}")
    public ResponseEntity<List<RoomDto>> findRoomsThatWillBeFree(@PathVariable LocalDate date) {
        log.info("Начало обработки команды: findRoomsThatWillBeFree, date={}", date);
        List<Room> rooms = roomManager.findRoomsThatWillBeFree(date);
        List<RoomDto> dtos = rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Рассчитывает полную стоимость проживания в номере.
     * @param roomNumber номер комнаты
     * @return стоимость проживания, если номер найден
     */
    @GetMapping("/{roomNumber}/price")
    public ResponseEntity<Double> getFullRoomPrice(@PathVariable int roomNumber) {
        log.info("Начало обработки команды: getFullRoomPrice, roomNumber={}", roomNumber);
        Room room = roomManager.findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }
        Double price = roomManager.fullRoomPrice(room);
        return ResponseEntity.ok(price);
    }

    /**
     * Возвращает историю изменений состояния номера.
     * @param roomNumber номер комнаты
     * @return список записей истории
     */
    @GetMapping("/{roomNumber}/history")
    public ResponseEntity<List<String>> getRoomHistory(@PathVariable int roomNumber) {
        log.info("Начало обработки команды: getRoomHistory, roomNumber={}", roomNumber);
        List<String> history = roomManager.getRoomHistory(roomNumber);
        return ResponseEntity.ok(history);
    }

    /**
     * Возвращает полную информацию о номере.
     * @param roomNumber номер комнаты
     * @return информация о номере
     */
    @GetMapping("/{roomNumber}")
    public ResponseEntity<RoomDto> getFullRoomInfo(@PathVariable int roomNumber) {
        log.info("Начало обработки команды: getFullRoomInfo, roomNumber={}", roomNumber);
        Room room = roomManager.findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    /**
     * Возвращает количество свободных номеров.
     * @return количество свободных номеров
     */
    @GetMapping("/count/free")
    public ResponseEntity<Integer> countFreeRooms() {
        log.info("Начало обработки команды: countFreeRooms");
        return ResponseEntity.ok(roomManager.countFreeRooms());
    }

    /**
     * Добавляет новый номер в систему.
     * @param dto - dto для создания комнаты
     * @return true, если номер был добавлен
     */
    @PostMapping
    public ResponseEntity<RoomDto> addRoom(@RequestBody CreateRoomDto dto) {
        log.info("Начало обработки команды: addRoom, dto={}", dto);
        boolean added = roomManager.addRoom(roomMapper.toEntity(dto));
        if (added) {
            Room room = roomManager.findRoomByNumber(dto.getNumber());
            return new ResponseEntity<>(roomMapper.toDto(room), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Устанавливает или снимает режим обслуживания для номера.
     * @param roomNumber  номер комнаты
     * @param maintenance признак обслуживания
     */
    @PutMapping("/{roomNumber}/maintenance")
    public ResponseEntity<Boolean> setRoomMaintenance(@PathVariable int roomNumber, @RequestParam boolean maintenance) {
        log.info("Начало обработки команды: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);
        boolean result = roomManager.setRoomMaintenance(roomNumber, maintenance);
        return ResponseEntity.ok(result);
    }
}
