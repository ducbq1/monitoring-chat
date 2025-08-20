package com.example.demo.model;

public class RecordLockStatus {
    private RecordLock recordLock;
    private Boolean locked;

    private RecordLockStatus(RecordLock recordLock, Boolean locked) {
        this.recordLock = recordLock;
        this.locked = locked;
    }

    public RecordLock getRecordLock() {
        return recordLock;
    }

    public Boolean getLocked() {
        return locked;
    }

    public static RecordLockStatus of(RecordLock recordLock, Boolean locked) {
        return new RecordLockStatus(recordLock, locked);
    }
}
