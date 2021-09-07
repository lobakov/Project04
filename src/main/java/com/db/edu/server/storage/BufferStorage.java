package com.db.edu.server.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BufferStorage {
    static Map<String, BufferedWriter> writers = new HashMap<>();

    private BufferStorage() {
        throw new IllegalStateException("Utility class");
    }

    public static BufferedWriter getBufferedWriterByFileName(String fileName) {
        return writers.get(fileName);
    }

    public static void save(String fileName, BufferedWriter writer) {
        writers.put(fileName, writer);
    }

    public static void closeAllBuffers() {
        for (BufferedWriter writer: writers.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
