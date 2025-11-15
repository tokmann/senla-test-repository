package task_6.controller;

import task_6.io.exporter.GuestCsvExporter;
import task_6.io.exporter.RoomCsvExporter;
import task_6.io.exporter.ServiceCsvExporter;
import task_6.io.importer.GuestCsvImporter;
import task_6.io.importer.RoomCsvImporter;
import task_6.io.importer.ServiceCsvImporter;

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

    public void importGuests(String path) throws Exception {
        guestImporter.importFromCsv(path);
    }

    public void exportGuests(String path) throws Exception {
        guestExporter.exportToCsv(path);
    }

    // комнаты
    public void importRooms(String path) throws Exception {
        roomImporter.importFromCsv(path);
    }

    public void exportRooms(String path) throws Exception {
        roomExporter.exportToCsv(path);
    }

    // услуги
    public void importServices(String path) throws Exception {
        serviceImporter.importFromCsv(path);
    }

    public void exportServices(String path) throws Exception {
        serviceExporter.exportToCsv(path);
    }
}

