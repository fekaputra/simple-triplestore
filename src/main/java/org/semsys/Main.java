package org.semsys;

import com.opencsv.exceptions.CsvException;
import org.semsys.triplestore.SimpleGraph;
import org.semsys.triplestore.SimpleGraphException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws SimpleGraphException, IOException, CsvException {
        SimpleGraph sg1 = new SimpleGraph();
        sg1.loadFromCSV("input/ex-1.csv");
        sg1.add("Fajar", "gender", "Female");
        sg1.add("Fajar", "gender", "Male");
        sg1.remove("Fajar", "gender", "Female");

        SimpleGraph sg2 = new SimpleGraph();
        sg2.loadFromCSV("input/ex-2.csv");
        sg1.addGraph(sg2);

        sg1.triples("Fajar", "gender", "Male");
        sg1.triples(null, "height", null);
        sg1.triples(null, null, "Male");

        sg1.saveToCSV("output/example-output.csv");
    }
}
