import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import Files.AnalyzeDatResults;
import Files.DatFile;
import Files.NotDatFile;

public class DatDump {

    public static void main(String[] args) {
        String fileName = "";
        DatFile datFile = null;
        String dirFileName = "";
        String argumentLine = "";
        PrintStream dumpPS = null;
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
            datFile = new DatFile(fileName);
            File inputFile = new File(fileName);
            String flyFileName = inputFile.getName();
            String flyFileNameRoot = flyFileName.substring(0,
                    flyFileName.indexOf('.'));
            dirFileName = aa.dirName;

            //System.out.println("Output directory: " + dirFileName);
            String dumpFileName = dirFileName + "/" + flyFileNameRoot + ".dump";
            try {
                dumpPS = new PrintStream(dumpFileName);
            } catch (FileNotFoundException ex) {
                System.out.println(dumpFileName + " in use");
                System.exit(0);
            }
            System.out.println("Dump file " + dumpFileName);
            AnalyzeDat analyzeDat = new AnalyzeDat(datFile);
            analyzeDat.setTickNoRange(aa.tickRangeLower, aa.tickRangeUpper);
            analyzeDat
                    .setFileLimits(aa.filePositionLower, aa.filePositionUpper);
            analyzeDat.setRecTypes(aa.recTypes);
            analyzeDat.dump(dumpPS);
        } catch (NotDatFile e) {
            System.out.println("Dat file " + datFile.fileName()
                    + " Not a recognized structure");
            System.exit(0);
        } catch (BadArguments e) {
            System.out.println(e.getMsg());
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " doesn't exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
