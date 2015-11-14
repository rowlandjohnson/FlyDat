import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Args {

    String argumentString = "";

    int index = 0;

    public long tickRangeLower = 0;

    public long tickRangeUpper = Long.MAX_VALUE;

    public long filePositionLower = 0;

    public long filePositionUpper = Long.MAX_VALUE;

    public String fileName = "";

    public String dirName = ".";

    public short recTypes[] = new short[0];

    Pattern flagPattern = Pattern.compile("\\s*-(fp|[tfrd])");

    public Args(String _args) throws BadArguments {
        argumentString = _args;
        String argsSub = "";
        int length = argumentString.length();
        Matcher flagMatcher = null;
        while (index < length) {
            argsSub = argumentString.substring(index);
            //System.out.println("ArgsSub XX" + argsSub + "YY");
            flagMatcher = flagPattern.matcher(argsSub);
            if (flagMatcher.find()) {
                String flag = argumentString.substring(flagMatcher.start(1)
                        + index, flagMatcher.end(1) + index);
                index += flagMatcher.end(1);
                if (flag.equals("t")) {
                    long range[] = getRange();
                    tickRangeLower = range[0];
                    tickRangeUpper = range[1];
                } else if (flag.equals("f")) {
                    fileName = getFileName();
                    //System.out.println("FileName = " + fileName + "CC");
                } else if (flag.equals("r")) {
                    recTypes = getRecTypes();
                } else if (flag.equals("d")) {
                    dirName = getFileName();
                    //System.out.println("DirName = " + dirName + "CC");
                } else if (flag.equals("fp")) {
                    long range[] = getRange();
                    filePositionLower = range[0];
                    filePositionUpper = range[1];
                }
            } else {
                break;
            }
        }
        if (fileName.isEmpty())
            throw new BadArguments("No filename specified");
    }

    Pattern recTypesPattern = Pattern.compile("\\s*\\{(.*?)\\}");

    private short[] getRecTypes() {
        String numbersString = "";
        String argsSub = argumentString.substring(index);
        //System.out.println("ArgsSub XR" + argsSub + "YY");
        Matcher recTypesMatcher = recTypesPattern.matcher(argsSub);
        System.out.println("XX" + recTypesMatcher.groupCount());
        if (recTypesMatcher.find()) {
            numbersString = argsSub.substring(recTypesMatcher.start(1),
                    recTypesMatcher.end(1));
        }
        String numbers[] = numbersString.split("\\D");
        short intNumbers[] = new short[30];
        int retvSize = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (!numbers[i].isEmpty()) {
                intNumbers[retvSize++] = Short.parseShort(numbers[i]);
            }
        }
        short retv[] = new short[retvSize];
        for (int i = 0; i < retvSize; i++) {
            retv[i] = intNumbers[i];
        }
        index += recTypesMatcher.end(1);
        return retv;
    }

    Pattern rangePattern = Pattern
            .compile("\\s*\\{\\s*(\\d*)\\s*:\\s*(\\d*)\\s*(\\})");

    private long[] getRange() throws BadArguments {
        long retv[] = new long[2];
        retv[0] = 0;
        retv[1] = Long.MAX_VALUE;
        String argsSub = argumentString.substring(index);
        //System.out.println("ArgsSub XR" + argsSub + "YY");
        Matcher rangeMatcher = rangePattern.matcher(argsSub);
        if (rangeMatcher.find()) {
            String lower = argsSub.substring(rangeMatcher.start(1),
                    rangeMatcher.end(1));
            String upper = argsSub.substring(rangeMatcher.start(2),
                    rangeMatcher.end(2));
            if (!lower.isEmpty())
                retv[0] = Long.parseLong(lower);
            if (!upper.isEmpty())
                retv[1] = Long.parseLong(upper);
            if (retv[0] > retv[1])
                throw new BadArguments(
                        "Lower limit of range is greater than upper limit");
            index += rangeMatcher.end(3);
        }
        return retv;
    }

    Pattern fileNamePattern = Pattern.compile("\\s*(\\S+)\\s*");

    private String getFileName() throws BadArguments {
        String fileName = "";
        String argsSub = argumentString.substring(index);
        //System.out.println("ArgsSub XF" + argsSub + "YY");
        Matcher fileNameMatcher = fileNamePattern.matcher(argsSub);
        if (fileNameMatcher.find()) {
            fileName = argumentString.substring(fileNameMatcher.start(1)
                    + index, fileNameMatcher.end(1) + index);
            index += fileNameMatcher.end(1);
        } else {
            throw new BadArguments("No filename specified after -f flag");
        }
        return fileName;
    }

    public static void main(String[] args) {

        String testArgs[] = {
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -r { 1,23 , 444} -fp { 3344: 88844}",
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -fp { 3344: 88844} ",
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -d .. ",
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -d /Users/rowland/phantom/test -t { 123 : 567} -r { 1, 23 , 444, } -fp { 3344: 88844}",
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -d /Users/rowland/phantom/test -t { : 567}",
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -d /Users/rowland/phantom/test -t { :}",
                "-f /Users/rowland/phantom/FLYLOGS/FLY305.DAT -d /Users/rowland/phantom/test " };

        for (int i = 0; i < testArgs.length; i++) {
            System.out.println(testArgs[i]);
            Args aa = null;
            try {
                aa = new Args(testArgs[i]);
                System.out.println("FileName " + aa.fileName + "\nDirName "
                        + aa.dirName);
                System.out.println("tick#Low " + aa.tickRangeLower
                        + " tick#Hi " + aa.tickRangeUpper);
                System.out.println("filePosLow " + aa.filePositionLower
                        + " tick#Hi " + aa.filePositionUpper);
                System.out.print("recTypes ");
                for (int j = 0; j < aa.recTypes.length; j++) {
                    System.out.print("," +aa.recTypes[j]);
                }
            } catch (BadArguments e) {
                System.out.println("Bad Arguments " + e.getMsg());
            }

            System.out.println("\n\n");
        }
    }

}
