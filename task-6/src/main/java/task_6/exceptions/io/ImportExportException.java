package task_6.exceptions.io;

import task_6.exceptions.HotelException;

/**
 * Исключения, связанные с импортом/экспортом данных.
 */
public class ImportExportException extends HotelException {

    public ImportExportException(String message) {
        super(message);
    }
}
