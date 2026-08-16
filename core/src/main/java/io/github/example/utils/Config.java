package io.github.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Map<String, String> properties = new HashMap<>();

    static {
        FileHandle file = Gdx.files.internal("config.properties");
        for (String line : file.readString().split("\\r?\\n")) {
            line = line.trim();
            // Ignore empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator == -1) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();

            properties.put(key, value);
        }
    }

    public static String getString(String key) {
        return properties.get(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(properties.get(key));
    }

    public static float getFloat(String key) {
        return Float.parseFloat(properties.get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.get(key));
    }
}
