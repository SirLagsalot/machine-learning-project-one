import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        File namesFile = new File(args[0]);
        File dataFile = new File(args[1]);
        String[] header = {}, data = {};
        try {
            header = parseHeader(namesFile);
            data = parseData(dataFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }


        File arffFile = buildArffFile(args[0].matches("*\\."), header, data);
        writeFile("path", arffFile);
    }

    private static String[] parseHeader(File headerFile) {
        Stream<String> lines = getFileStream(headerFile);
        if (lines != null) {
            String[] formattedLines = {};
            // Parse header
            return formattedLines;
        } else {
            return null;
        }
    }

    private static String[] parseData(File dataFile) throws FileNotFoundException {
        Stream<String> lines = getFileStream(dataFile);
        if (lines != null) {
            String[] formattedLines = {};
            // Parse header
            return formattedLines;
        } else {
            return null;
        }
    }

    private static Stream<String> getFileStream(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader.lines().map(Main::formatLine);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String formatLine(String line) {
        String formattedLine = "asdf";
        // Parse individual line, return in ARFF format
        return formattedLine;
    }

    private static File buildArffFile(String fileName, String[] header, String[] data) {
        return new File(fileName);
    }


    private static void writeFile(String path, String[] lines) {
        System.out.println(lines[0]);
        // Create .arff file, write lines to it
    }
}
