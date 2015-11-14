import java.io.File;
import java.io.IOException;

import Files.AnalyzeDatResults;
import Files.DatFile;

public class TestAllDatFiles {

    public static void main(String[] args) throws IOException {
        AnalyzeDatResults results = null;
        DatFile datFile = null;
        File directory = new File("/Users/rowland/phantom/FLYLOGS");
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getName().endsWith("DAT")) {
                    System.out.println("Analyzing Dat " + file.getName());
                    try {
                        datFile = new DatFile(file.getAbsolutePath());
                        AnalyzeDat analyzeDat = null;
                        analyzeDat = new AnalyzeDat(datFile);
                        analyzeDat.setDebug(false);
                        results = analyzeDat.analyze();
                        System.out.println(results);
                    } catch (Files.NotDatFile e) {
                        System.out.println("Dat file " + datFile.fileName()
                                + " Not a recognized structure");
                    }
                }
            }
        }
    }
}
