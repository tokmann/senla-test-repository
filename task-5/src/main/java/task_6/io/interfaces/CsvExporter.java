package task_6.io.interfaces;

import java.io.IOException;

public interface CsvExporter {
    void exportToCsv(String filePath) throws IOException;
}


