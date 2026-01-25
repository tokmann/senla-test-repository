package hotel.util;

import di.Component;

@Component
public class RoomConfigurationService {

    private final boolean statusChangeEnabled;
    private final int historySize;

    public RoomConfigurationService() {
        this.statusChangeEnabled = ConfigurationLoader.getBooleanProperty("room.status.change.enabled", true);
        this.historySize = ConfigurationLoader.getIntProperty("room.history.size", 10);
    }

    public boolean isStatusChangeEnabled() {
        return statusChangeEnabled;
    }

    public int getHistorySize() {
        return historySize;
    }
}
