package com.db.edu.server.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

public class BufferStorage {
    static Map<String, BufferedWriter> writers = new HashMap<>();
    static Map<String, BufferedReader> readers = new HashMap<>();

    public static BufferedWriter getBufferedWriterByFileName(String fileName) {
        return writers.get(fileName);
    }

    public static BufferedWriter removeBufferedWriterByFileName(String fileName) {
        return writers.remove(fileName);
    }

    public static void save(String fileName, BufferedWriter writer) {
        writers.put(fileName, writer);
    }

    public static BufferedReader getBufferedReaderByFileName(String fileName) {
        return readers.get(fileName);
    }

    public static BufferedReader removeBufferedReaderByFileName(String fileName) {
        return readers.remove(fileName);
    }

    public static void save(String fileName, BufferedReader reader) {
        readers.put(fileName, reader);
    }
}
