package com.db.edu.server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BufferStorageTest {
    private String filename = "testFile.txt";
    private BufferStorage bufferStorage;
    private BufferedWriter bufferedWriter;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        bufferStorage = new BufferStorage();
        bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new BufferedOutputStream(
                                new FileOutputStream(filename, false))));
    }

    @Test
    public void shouldSaveBufferedWriterWhenGetsFileName() {
        bufferStorage.save(filename, bufferedWriter);
        assertEquals(bufferedWriter, bufferStorage.getWriters().get(filename));
    }

    @Test
    public void shouldReturnBufferedWriterWhenGetsFileName() {
        bufferStorage.getWriters().put(filename, bufferedWriter);
        assertEquals(bufferedWriter, bufferStorage.getBufferedWriterByFileName(filename));
    }
}
