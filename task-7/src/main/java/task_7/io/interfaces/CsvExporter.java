package task_7.io.interfaces;

import java.io.IOException;

/**
 * Контракт для классов, экспортирующих данные в CSV формат.
 * Определяет метод для сериализации данных в файл.
 */
public interface CsvExporter {

    /**
     * Экспортирует данные в CSV файл по указанному пути.
     * @param filePath абсолютный или относительный путь к файлу для сохранения
     */
    void exportToCsv(String filePath) throws IOException;
}


