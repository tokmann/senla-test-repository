package task_6.io.interfaces;

import java.io.IOException;

/**
 * Контракт для классов, импортирующих данные из CSV формата.
 * Определяет метод для десериализации данных из файла.
 */
public interface CsvImporter {

    /**
     * Импортирует данные из CSV файла по указанному пути.
     * @param filePath абсолютный или относительный путь к CSV файлу
     */
    void importFromCsv(String filePath) throws IOException;
}
