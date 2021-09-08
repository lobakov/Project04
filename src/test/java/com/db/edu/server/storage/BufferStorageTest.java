package com.db.edu.server.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

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
        assertEquals(bufferedWriter, bufferStorage.writers.get(filename));
    }

    @Test
    public void shouldReturnBufferedWriterWhenGetsFileName() {
        bufferStorage.writers.put(filename, bufferedWriter);
        assertEquals(bufferedWriter, bufferStorage.getBufferedWriterByFileName(filename));
    }
}
