package task_4.management;

import task_4.model.Guest;
import task_4.model.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GuestManager {

    private final List<Guest> guests;

    private static final Map<String, Comparator<Service>> SORTING_RULES_SERVICE = Map.of(
            "Цена", Comparator.comparing(Service::getPrice),
            "Дата", Comparator.comparing(Service::getDate)
    );

    private static final Map<String, Comparator<Guest>> SORTING_RULES_GUESTS = Map.of(
            "Алфавит", Comparator.comparing(Guest::getFullName),
            "Дата освобождения номера", Comparator.comparing(guest -> guest.getGuestRoom().getCheckOutDate())
    );

    public GuestManager() {
        this.guests = new ArrayList<>();
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void addGuest(Guest guest) {
        guests.add(guest);
        System.out.println(guest + " добавлен");
    }

    public int allGuestsCount() {
        return guests.size();
    }

    public List<Service> getSortedGuestServices(Guest guest, String by) {
        Comparator<Service> comparator = SORTING_RULES_SERVICE.get(by);
        return guest
                .getGuestServices()
                .stream()
                .sorted(comparator)
                .toList();
    }

    public void removeGuest(Guest guest) {
        guests.remove(guest);
        System.out.println("Гость " + guest + " удален из системы");
    }

    public void removeGuests(List<Guest> guestsToRemove) {
        guests.removeAll(guestsToRemove);
        System.out.println("Удалено гостей: " + guestsToRemove.size());
    }

    public void showAllSortedGuests(String by) {
        System.out.println("ВСЕ ГОСТИ (ПО " + by + ")");
        Comparator<Guest> comparator = SORTING_RULES_GUESTS.get(by);
        if (comparator == null) {
            throw new IllegalArgumentException("Неизвестный критерий сортировки: " + by);
        }
        if (getGuests().isEmpty()) {
            System.out.println("Гости отсутствуют");
        } else {
            System.out.println(getGuests()
                    .stream()
                    .sorted(comparator)
                    .toList());
        }
    }
}
