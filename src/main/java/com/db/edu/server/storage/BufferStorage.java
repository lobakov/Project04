package com.db.edu.server.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;

public class BufferStorage {
    static Map<String, BufferedWriter> writers;
    static Map<String, BufferedReader> readers;

    public static BufferedWriter getBufferedWriterByFileName(String fileName) {
        return writers.get(fileName);
    }

    public static void save(String fileName, BufferedWriter writer) {
        writers.put(fileName, writer);
    }

    public static BufferedReader getBufferedReaderByFileName(String fileName) {
        return readers.get(fileName);
    }

    public static void save(String fileName, BufferedReader reader) {
        readers.put(fileName, reader);
    }
}
