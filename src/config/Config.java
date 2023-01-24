package config;

import java.io.File;
import java.nio.file.Path;

public class Config {
    public static final String MOCK_DB_PATH = Path.of(new File("").getAbsolutePath(), "src", "mocks", "database")
            .toString();
}
