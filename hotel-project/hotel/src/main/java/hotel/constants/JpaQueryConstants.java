package hotel.constants;

public final class JpaQueryConstants {

    // Гости
    public static final String SELECT_ALL_GUESTS_WITH_SERVICES_AND_ROOM =
            "SELECT g FROM Guest g LEFT JOIN FETCH g.services LEFT JOIN FETCH g.room";

    public static final String SELECT_GUESTS_BY_ROOM_ID =
            "SELECT g FROM Guest g WHERE g.room.id = :roomId";

    public static final String COUNT_ALL_GUESTS =
            "SELECT COUNT(g) FROM Guest g";

    // Комнаты
    public static final String SELECT_ALL_ROOMS =
            "SELECT r FROM Room r";

    public static final String SELECT_ROOM_BY_NUMBER =
            "SELECT r FROM Room r WHERE r.number = :number";

    public static final String COUNT_FREE_ROOMS =
            "SELECT COUNT(r) FROM Room r WHERE r.isOccupied = false AND r.underMaintenance = false";

    // Сервисы
    public static final String SELECT_ALL_SERVICES =
            "SELECT s FROM Service s";

    public static final String SELECT_SERVICE_BY_NAME =
            "SELECT s FROM Service s WHERE s.name = :name";

    // История заселения
    public static final String SELECT_HISTORY_ENTRIES_BY_ROOM_ID =
            "SELECT h.entry FROM StayHistory h WHERE h.room.id = :roomId ORDER BY h.entryDate DESC";

    public static final String DELETE_HISTORY_BY_ROOM_ID =
            "DELETE FROM StayHistory h WHERE h.room.id = :roomId";

    public static final String DELETE_OLDEST_HISTORY_ENTRY_BY_ROOM_ID =
            "DELETE FROM StayHistory h WHERE h.id = (SELECT MIN(h2.id) FROM StayHistory h2 WHERE h2.room.id = :roomId)";

    // Параметры
    public static final String PARAM_ROOM_ID = "roomId";
    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_LIMIT = "limit";

    private JpaQueryConstants() {}
}
