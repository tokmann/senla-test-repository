package hotel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RoomConfigurationService {

    @Value("${room.status.change.enabled:true}")
    private boolean statusChangeEnabled;

    @Value("${room.history.size:10}")
    private int historySize;

    public boolean isStatusChangeEnabled() {
        return statusChangeEnabled;
    }

    public int getHistorySize() {
        return historySize;
    }
}
