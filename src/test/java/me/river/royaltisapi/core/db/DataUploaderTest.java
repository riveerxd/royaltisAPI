package me.river.royaltisapi.core.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataUploaderTest {

    @Test
    void uploadGameData() {
        DataUploader uploader = new DataUploader();
        assertTrue(uploader.uploadGameData(null));
    }
}