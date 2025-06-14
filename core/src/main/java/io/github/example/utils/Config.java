package io.github.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties;

    static {
        properties = new Properties();
        FileHandle file = Gdx.files.internal("config.properties");
        try {
            properties.load(file.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public static float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }
}
