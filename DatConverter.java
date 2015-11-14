import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import Files.AnalyzeDatResults;
import Files.DatFile;
import Files.NotDatFile;

public class DatConverter {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String cmdLineSyntax = "DatConverter -f <FLYXXX.DAT> [-d <output dir>]";
        String fileName = "";
        String dirFileName = "";
        PrintStream csvPS = null;
        DatFile datFile = null;
        String argumentLine = "";
        for (int i = 0; i < args.length; i++) {
            argumentLine += args[i] + " ";
        }
        try {
            Args aa = new Args(argumentLine);
            fileName = aa.fileName;
            if (!fileName.endsWith(".DAT")) {
                throw new BadArguments(
                        "Filename does not have a .DAT extension");
            }

            File inputFile = new File(fileName);
            String flyFileName = inputFile.getName();
            String flyFileNameRoot = flyFileName.substring(0,
                    flyFileName.indexOf('.'));
            dirFileName = aa.dirName;

            //System.out.println("Output directory: " + dirFileName);
            String csvFileName = dirFileName + "/" + flyFileNameRoot + ".csv";
            System.out.println("CSV file name: " + csvFileName);
            try {
                csvPS = new PrintStream(csvFileName);
            } catch (FileNotFoundException ex) {
                System.out.println(csvFileName + " in use");
                System.exit(0);
            }

            datFile = new DatFile(inputFile);
//            System.out.println("datFile "
//                    + datFile.getFile().getCanonicalPath());
            AnalyzeDat analyzeDat = new AnalyzeDat(datFile);
            analyzeDat.setCsv(csvPS);
            analyzeDat.setTickNoRange(aa.tickRangeLower, aa.tickRangeUpper);
            analyzeDat.setFileLimits(aa.filePositionLower, aa.filePositionUpper);
            analyzeDat.setRecTypes(aa.recTypes);
            System.out.println("Converting " + fileName);
            AnalyzeDatResults results = analyzeDat.analyze();
            System.out.println(results.toString());
            if (csvPS != null)
                csvPS.close();

        } catch (NotDatFile e) {
            System.out.println("Dat file " + datFile.fileName()
                    + " Not a recognized structure");
            System.exit(0);
        } catch (BadArguments e) {
            System.out.println(e.getMsg());
        } catch (FileNotFoundException e) {
           System.out.println(fileName + " doesn't exist");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
