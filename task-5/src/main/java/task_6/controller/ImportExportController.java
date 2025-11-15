package task_6.controller;

import task_6.io.exporter.GuestCsvExporter;
import task_6.io.exporter.RoomCsvExporter;
import task_6.io.exporter.ServiceCsvExporter;
import task_6.io.importer.GuestCsvImporter;
import task_6.io.importer.RoomCsvImporter;
import task_6.io.importer.ServiceCsvImporter;

/**
 * Контроллер для управления операциями импорта и экспорта данных.
 * Координирует работу всех CSV-импортеров и экспортеров системы.
 */
public class ImportExportController {

    private final GuestCsvImporter guestImporter;
    private final GuestCsvExporter guestExporter;

    private final RoomCsvImporter roomImporter;
    private final RoomCsvExporter roomExporter;

    private final ServiceCsvImporter serviceImporter;
    private final ServiceCsvExporter serviceExporter;

    public ImportExportController(
            GuestCsvImporter guestImporter,
            GuestCsvExporter guestExporter,
            RoomCsvImporter roomImporter,
            RoomCsvExporter roomExporter,
            ServiceCsvImporter serviceImporter,
            ServiceCsvExporter serviceExporter
    ) {
        this.guestImporter = guestImporter;
        this.guestExporter = guestExporter;
        this.roomImporter = roomImporter;
        this.roomExporter = roomExporter;
        this.serviceImporter = serviceImporter;
        this.serviceExporter = serviceExporter;
    }

    /**
     * Импортирует гостей из CSV файла.
     * @param path путь к CSV файлу
     */
    public void importGuests(String path) throws Exception {
        guestImporter.importFromCsv(path);
    }

    /**
     * Экспортирует гостей в CSV файл.
     * @param path путь для сохранения CSV файла
     */
    public void exportGuests(String path) throws Exception {
        guestExporter.exportToCsv(path);
    }

    /**
     * Импортирует комнаты из CSV файла.
     * @param path путь к CSV файлу
     */
    public void importRooms(String path) throws Exception {
        roomImporter.importFromCsv(path);
    }

    /**
     * Экспортирует комнаты в CSV файл.
     * @param path путь для сохранения CSV файла
     */
    public void exportRooms(String path) throws Exception {
        roomExporter.exportToCsv(path);
    }

    /**
     * Импортирует услуги из CSV файла.
     * @param path путь к CSV файлу
     */
    public void importServices(String path) throws Exception {
        serviceImporter.importFromCsv(path);
    }

    /**
     * Экспортирует услуги в CSV файл.
     * @param path путь для сохранения CSV файла
     */
    public void exportServices(String path) throws Exception {
        serviceExporter.exportToCsv(path);
    }
}

