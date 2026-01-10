package task_8.exceptions.io;

public class FileNotFoundException extends ImportExportException {

    public FileNotFoundException(String filePath) {
        super("Файл не найден: " + filePath);
    }
}

