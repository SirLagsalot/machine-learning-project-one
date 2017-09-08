import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello wurld");
        File file = new File(args[0]);
        String[] formattedLines = convertFileToARFF(file);
        writeToFile("path", formattedLines);
    }

    private static String[] convertFileToARFF(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader.lines()
                    .map(Main::formatLine)
                    .toArray(String[]::new);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String formatLine(String line) {
        String formattedLine = "";
        // Parse individual line, return in ARFF format
        return formattedLine;
    }

    private static void writeToFile(String path, String[] lines) {
        // Create .arff file, write lines to it
    }
}
