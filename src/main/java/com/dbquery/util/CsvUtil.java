package com.dbquery.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author yuanguangxin
 */
public class CsvUtil {

    public static void exportWithCsvPrinter(HttpServletResponse response, List<List<String>> data, List<String> header) throws Exception {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
        CSVPrinter csvFilePrinter = new CSVPrinter(response.getWriter(), csvFileFormat);
        csvFilePrinter.printRecord(header);
        for (List<String> line : data) {
            csvFilePrinter.printRecord(line);
        }
        csvFilePrinter.close();
    }
}
