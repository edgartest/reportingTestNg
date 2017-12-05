package com.pearson.test.qglobal;

import java.io.FileReader;
import java.io.Reader;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CsvparserExample {
    public CsvparserExample() {
    }

    public static void main(String[] args) throws Exception {
        Reader in = new FileReader("data/assesmentData/KTEA-3LIA.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        CSVRecord row = (CSVRecord)StreamSupport.stream(records.spliterator(), false).filter((r) -> {
            return r.get("smoke_ind").equals("y");
        }).findAny().get();
        System.out.println(row.get("TC_ID"));
    }
}
