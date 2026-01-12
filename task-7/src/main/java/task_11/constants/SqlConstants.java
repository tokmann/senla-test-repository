package task_11.constants;

public class SqlConstants {
    // room
    public static final String INSERT_ROOM = "INSERT INTO room (number, capacity, price, stars, history_size) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_ROOM = "UPDATE room SET number = ?, capacity = ?, price = ?, stars = ?, is_occupied = ?, under_maintenance = ?, status_change_enabled = ?, history_size = ?, check_in_date = ?, check_out_date = ? WHERE id = ?";
    public static final String DELETE_ROOM = "DELETE FROM room WHERE id = ?";
    public static final String SELECT_ROOM_BY_ID = "SELECT * FROM room WHERE id = ?";
    public static final String SELECT_ROOM_BY_NUMBER = "SELECT * FROM room WHERE number = ?";
    public static final String SELECT_ALL_ROOMS = "SELECT * FROM room";
    public static final String COUNT_FREE_ROOMS = "SELECT COUNT(*) FROM room WHERE is_occupied = FALSE AND under_maintenance = FALSE";

    // guest
    public static final String INSERT_GUEST = "INSERT INTO guest (age, first_name, second_name, room_id) VALUES (?, ?, ?, ?) RETURNING id";
    public static final String UPDATE_GUEST = "UPDATE guest SET age = ?, first_name = ?, second_name = ?, room_id = ? WHERE id = ?";
    public static final String DELETE_GUEST = "DELETE FROM guest WHERE id = ?";
    public static final String SELECT_GUEST_BY_ID = "SELECT * FROM guest WHERE id = ?";
    public static final String SELECT_ALL_GUESTS = "SELECT * FROM guest";
    public static final String SELECT_GUESTS_BY_ROOM_ID = "SELECT * FROM guest WHERE room_id = ?";
    public static final String COUNT_GUESTS = "SELECT COUNT(*) FROM guest";

    // service
    public static final String INSERT_SERVICE = "INSERT INTO service (name, description, price, date) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_SERVICE = "UPDATE service SET name = ?, description = ?, price = ?, date = ? WHERE id = ?";
    public static final String DELETE_SERVICE = "DELETE FROM service WHERE id = ?";
    public static final String SELECT_SERVICE_BY_ID = "SELECT * FROM service WHERE id = ?";
    public static final String SELECT_SERVICE_BY_NAME = "SELECT * FROM service WHERE LOWER(name) = LOWER(?)";
    public static final String SELECT_ALL_SERVICES = "SELECT * FROM service";

    // guest_Service
    public static final String INSERT_GUEST_SERVICE = "INSERT INTO guest_service (guest_id, service_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
    public static final String DELETE_GUEST_SERVICE = "DELETE FROM guest_service WHERE guest_id = ? AND service_id = ?";
    public static final String SELECT_SERVICES_BY_GUEST_ID = "SELECT s.* FROM service s JOIN guest_service gs ON s.id = gs.service_id WHERE gs.guest_id = ?";
    public static final String SELECT_GUESTS_BY_SERVICE_ID = "SELECT g.* FROM guest g JOIN guest_service gs ON g.id = gs.guest_id WHERE gs.service_id = ?";

    // stay_History
    public static final String INSERT_STAY_HISTORY = "INSERT INTO stay_history (room_id, history_entry) VALUES (?, ?)";
    public static final String SELECT_HISTORY_BY_ROOM_ID = "SELECT history_entry FROM stay_history WHERE room_id = ? ORDER BY id DESC LIMIT ?";
    public static final String DELETE_HISTORY_BY_ROOM_ID = "DELETE FROM stay_history WHERE room_id = ?";

    private SqlConstants() {}

}
