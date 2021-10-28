package org.semsys.triplestore;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimpleGraph {

    private Map<String, Map<String, Set<String>>> spo, pos, osp;

    public SimpleGraph() {
        spo = new HashMap<>();
        pos = new HashMap<>();
        osp = new HashMap<>();
    }

    public void addGraph(SimpleGraph sg) throws SimpleGraphException {
        for (Map.Entry<String, Map<String, Set<String>>> e : sg.spo.entrySet()) {
            String s = e.getKey();
            Map<String, Set<String>> po = e.getValue();
            for (Map.Entry<String, Set<String>> entry : po.entrySet()) {
                String p = entry.getKey();
                Set<String> oList = entry.getValue();
                for (String o : oList) {
                    add(s, p, o);
                }
            }
        }
    }

    public void loadFromCSV(String filename) throws IOException, CsvException, SimpleGraphException {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            List<String[]> triples = reader.readAll();
            for (String[] triple : triples) {
                String sub = triple[0];
                String pre = triple[1];
                String obj = triple[2];
                add(sub, pre, obj);
            }
        }
    }

    public void saveToCSV(String filename) throws IOException {
        List<String[]> csvData = new ArrayList<>();
        spo.forEach((s, po) -> po.forEach((p, oList) -> oList.forEach(o -> {
            String[] row = {s, p, o};
            csvData.add(row);
        })));
        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(filename))
                .withSeparator(',')
                .build()) {
            writer.writeAll(csvData);
        }
    }

    public void triples(String sub, String pred, String obj) {
        System.out.println("= triple results =");
        spo.forEach((s, po) -> {
            if (sub == null || s.equals(sub))
                po.forEach((p, oList) -> {
                    if (pred == null || p.equals(pred))
                        oList.forEach(o -> {
                            if (obj == null || o.equals(obj))
                                System.out.println(s + "," + p + "," + o);
                        });
                });
        });
    }

    public void add(String s, String p, String o) throws SimpleGraphException {
        if (s.isEmpty() || p.isEmpty() || o.isEmpty()) throw new SimpleGraphException("null s/p/o");
        addToIndex(spo, s, p, o);
        addToIndex(pos, p, o, s);
        addToIndex(osp, o, s, p);
    }

    public void remove(String s, String p, String o) throws SimpleGraphException {
        if (s.isEmpty() || p.isEmpty() || o.isEmpty()) throw new SimpleGraphException("null s/p/o");
        removeFromIndex(spo, s, p, o);
        removeFromIndex(pos, p, o, s);
        removeFromIndex(osp, o, s, p);
    }

    private void addToIndex(Map<String, Map<String, Set<String>>> sMap, String s, String p, String o) {
        if (!sMap.containsKey(s)) {
            sMap.put(s, new HashMap<>());
        }
        Map<String, Set<String>> pMap = sMap.get(s);
        if (!pMap.containsKey(p)) {
            pMap.put(p, new HashSet<>());
        }
        pMap.get(p).add(o);
    }

    private void removeFromIndex(Map<String, Map<String, Set<String>>> sMap, String s, String p, String o) {
        if (sMap.containsKey(s)) {
            Map<String, Set<String>> pMap = sMap.get(s);
            if (pMap.containsKey(p)) {
                pMap.get(p).remove(o);
            }
        }
    }
}
