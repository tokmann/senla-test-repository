package task_7.exceptions.io;

public class InvalidFileFormatException extends ImportExportException {

    public InvalidFileFormatException(String filePath, String expectedFormat) {
        super("Неверный формат файла " + filePath + ". Ожидается: " + expectedFormat);
    }
}
