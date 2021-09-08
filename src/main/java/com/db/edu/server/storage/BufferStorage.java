package com.db.edu.server.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BufferStorage {
    private Map<String, BufferedWriter> writers;

    public BufferStorage() {
        writers = new ConcurrentHashMap<>();
    }

    public BufferedWriter getBufferedWriterByFileName(String fileName) {
        return writers.get(fileName);
    }

    public void save(String fileName, BufferedWriter writer) {
        writers.put(fileName, writer);
    }

    public void closeAllBuffers() {
        for (BufferedWriter writer: writers.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
