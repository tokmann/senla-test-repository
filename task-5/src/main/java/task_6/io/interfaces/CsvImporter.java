package task_6.io.interfaces;

import java.io.IOException;

public interface CsvImporter {
    void importFromCsv(String filePath) throws IOException;
}
