package hotel.db.interfaces;

import java.util.List;

public interface StayHistoryRepository {
    void addEntry(long roomId, String entry);
    List<String> findByRoomId(long roomId, int limit);
    void deleteByRoomId(long roomId);
}
