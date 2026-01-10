package task_11.exceptions.io;

import task_11.exceptions.HotelException;

/**
 * Исключения, связанные с импортом/экспортом данных.
 */
public class ImportExportException extends HotelException {

    public ImportExportException(String message) {
        super(message);
    }
}
