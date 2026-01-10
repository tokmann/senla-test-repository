package task_11.serialize;

import config.AnnotationConfigurationLoader;
import di.Component;
import di.Inject;
import task_11.model.Guest;
import task_11.model.Room;
import task_11.model.Service;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.service.interfaces.IServiceManager;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StateManager {

    private static final String STATE_FILE = "hotel.state";

    @Inject
    private IGuestManager guestManager;

    @Inject
    private IRoomManager roomManager;

    @Inject
    private IServiceManager serviceManager;

    public void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE))) {
            HotelState state = new HotelState(
                    guestManager.getAllGuests(),
                    roomManager.getAllRooms(),
                    serviceManager.getAllServices()
            );
            oos.writeObject(state);
            System.out.println("Состояние сохранено в " + STATE_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения состояния: " + e.getMessage());
        }
    }

    public void loadState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            System.out.println("Файл состояния не найден, инициализация по умолчанию");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STATE_FILE))) {
            HotelState state = (HotelState) ois.readObject();
            restoreFromState(state);
            System.out.println("Состояние загружено из " + STATE_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки состояния: " + e.getMessage());
        }
    }

    private void restoreFromState(HotelState state) {
        state.rooms.forEach(roomManager::addRoom);
        state.services.forEach(serviceManager::addService);
        state.guests.forEach(guestManager::addGuest);

        Map<Long, Guest> guestMap = guestManager.getAllGuests().stream()
                .collect(Collectors.toMap(Guest::getId, g -> g));
        Map<Long, Room> roomMap = roomManager.getAllRooms().stream()
                .collect(Collectors.toMap(Room::getId, r -> r));
        Map<Long, Service> serviceMap = serviceManager.getAllServices().stream()
                .collect(Collectors.toMap(Service::getId, s -> s));

        for (Map.Entry<Long, Long> entry : state.guestToRoom.entrySet()) {
            Guest guest = guestMap.get(entry.getKey());
            Room room = roomMap.get(entry.getValue());
            if (guest != null && room != null) {
                guest.setGuestRoom(room);
            }
        }

        for (Map.Entry<Long, List<Long>> entry : state.guestToServices.entrySet()) {
            Guest guest = guestMap.get(entry.getKey());
            if (guest != null) {
                List<Service> services = entry.getValue().stream()
                        .map(serviceMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                guest.setGuestServices(services);
            }
        }

        for (Room room : roomManager.getAllRooms()) {
            AnnotationConfigurationLoader.configure(room);
            List<Long> guestIds = state.roomToGuests.getOrDefault(room.getId(), Collections.emptyList());
            List<Guest> guests = guestIds.stream()
                    .map(guestMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            room.setGuests(guests);
            room.setIsOccupied(!guests.isEmpty());
        }

        roomManager.syncIdGen();
        guestManager.syncIdGen();
        serviceManager.syncIdGen();
    }

    /**
     * Вспомогательный класс для хранения состояния.
     */
    private static class HotelState implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<Guest> guests;
        private final List<Room> rooms;
        private final List<Service> services;

        private final Map<Long, Long> guestToRoom = new HashMap<>();
        private final Map<Long, List<Long>> guestToServices = new HashMap<>();
        private final Map<Long, List<Long>> roomToGuests = new HashMap<>();

        public HotelState(List<Guest> guests, List<Room> rooms, List<Service> services) {
            this.guests = new ArrayList<>(guests);
            this.rooms = new ArrayList<>(rooms);
            this.services = new ArrayList<>(services);

            for (Guest guest : guests) {
                if (guest.getGuestRoom() != null) {
                    guestToRoom.put(guest.getId(), guest.getGuestRoom().getId());
                }
                List<Long> serviceIds = guest.getGuestServices().stream()
                        .map(Service::getId)
                        .collect(Collectors.toList());
                if (!serviceIds.isEmpty()) {
                    guestToServices.put(guest.getId(), serviceIds);
                }
            }

            for (Room room : rooms) {
                List<Long> guestIds = room.getGuests().stream()
                        .map(Guest::getId)
                        .collect(Collectors.toList());
                if (!guestIds.isEmpty()) {
                    roomToGuests.put(room.getId(), guestIds);
                }
            }
        }
    }
}
