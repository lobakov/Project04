package com.db.edu.server.storage;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

public class BufferStorage {
    static Map<String, BufferedWriter> writers = new HashMap<>();

    public static BufferedWriter getBufferedWriterByFileName(String fileName) {
        return writers.get(fileName);
    }

//    public static BufferedWriter removeBufferedWriterByFileName(String fileName) {
//        return writers.remove(fileName);
//    }

    public static void save(String fileName, BufferedWriter writer) {
        writers.put(fileName, writer);
    }
}
