package com.example.demo.core.model;

import java.util.List;
import java.util.Map;

public class TablePageDTO<T> {
    private MetaDataDTO metadata;
    private List<Map<String, Object>> columns;
    private List<T> records;
    private int number;
    private int size;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private boolean first;
    private boolean last;

    public MetaDataDTO getMetadata() {
        return metadata;
    }

    public TablePageDTO<T> setMetadata(MetaDataDTO metadata) {
        this.metadata = metadata;
        return this;
    }

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public TablePageDTO<T> setColumns(List<Map<String, Object>> columns) {
        this.columns = columns;
        return this;
    }

    public List<T> getRecords() {
        return records;
    }

    public TablePageDTO<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public TablePageDTO<T> setNumber(int number) {
        this.number = number;
        return this;
    }

    public int getSize() {
        return size;
    }

    public TablePageDTO<T> setSize(int size) {
        this.size = size;
        return this;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public TablePageDTO<T> setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public TablePageDTO<T> setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        return this;
    }

    public boolean isFirst() {
        return first;
    }

    public TablePageDTO<T> setFirst(boolean first) {
        this.first = first;
        return this;
    }

    public boolean isLast() {
        return last;
    }

    public TablePageDTO<T> setLast(boolean last) {
        this.last = last;
        return this;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public TablePageDTO<T> setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
        return this;
    }
}
