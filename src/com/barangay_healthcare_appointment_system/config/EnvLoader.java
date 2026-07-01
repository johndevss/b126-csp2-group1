package com.barangay_healthcare_appointment_system.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads a .env file into memory, the same way `source .env` or
 * `export $(cat .env)` would work on the command line.
 *
 * This runs once when the app starts and caches the values,
 * so every class just calls EnvLoader.get("DB_HOST") instead of
 * re-reading the file every time.
 */
public class EnvLoader {

    private static final Map<String, String> envMap = new HashMap<>();
    private static boolean loaded = false;

    // Loads .env from the project root. Called automatically on first get().
    private static void load() {
        if (loaded) return; // don't reload every time, only once

        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // skip blank lines and comments, same as .env conventions you already know
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // split only on the FIRST "=" so passwords with "=" in them still work
                int separatorIndex = line.indexOf('=');
                if (separatorIndex == -1) continue; // malformed line, skip it

                String key = line.substring(0, separatorIndex).trim();
                String value = line.substring(separatorIndex + 1).trim();

                envMap.put(key, value);
            }
            loaded = true;

        } catch (IOException e) {
            System.err.println("[EnvLoader] Could not find .env file in project root.");
            System.err.println("[EnvLoader] Copy .env.example to .env and fill in your DB credentials.");
            // We don't crash here — DBConnection will fail with a clearer error later.
        }
    }

    /**
     * Get a value from .env by key.
     * Example: EnvLoader.get("DB_HOST")
     */
    public static String get(String key) {
        load(); // make sure it's loaded before anyone reads from it
        return envMap.get(key);
    }

    /**
     * Same as get(), but returns a fallback value if the key is missing.
     * Useful for optional settings.
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}