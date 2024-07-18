package codesquad.middleware.csv;

import codesquad.framework.coffee.annotation.Coffee;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Coffee
public class CsvInitializer {
    private static final String DIR_NAME = "csv";

    public String init(String fileName, String header) throws IOException {
        String baseDir = System.getProperty("user.dir");
        String csvDirPath = baseDir + File.separator + DIR_NAME;
        File csvDir = new File(csvDirPath);

        if (!csvDir.exists()) {
            if (!csvDir.mkdirs()) {
                throw new IOException("Failed to create directory");
            }
        }

        String filePath = csvDirPath + File.separator + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(header + "\r\n");
            }
        }
        return filePath;
    }

    public String getUrl(String filePath) {
        return "jdbc:csv:" + filePath;
    }
}
