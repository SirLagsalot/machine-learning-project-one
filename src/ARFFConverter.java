import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ARFFConverter {

    private static final String ATTRIBUTE_TAG = "@ATTRIBUTE";
    private static final String DATA_TAG = "@DATA";
    private static final String RELATION_TAG = "@RELATION";
    private static int classIndex = -1;

    /**
     * args[1]: Name of dataset
     * args[2]: List position of classification attribute; {first, last}
     */
    public static void main(String[] args) {
        File metadataFile = new File(args[0].concat(".metadata"));
        File dataFile = new File(args[0].concat(".data"));

        Stream<String> metaDataStream = getFileStream(metadataFile);
        Stream<String> dataStream = getFileStream(dataFile);

        assert metaDataStream != null;
        assert dataStream != null;

        String headerInfo = getHeaderInfo(metaDataStream.collect(Collectors.toList()));
        String relationInfo = RELATION_TAG.concat(" " + args[0]);
        String attributeInfo = getAttributeInfo(metaDataStream);
        String[] dataInfo = getDataInfo(dataStream);

        // Write to file
    }

    private static Stream<String> getFileStream(File file) {
        try {
            return new BufferedReader(new FileReader(file)).lines();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String getHeaderInfo(List<String> metadata) {
        StringBuilder headerLines = new StringBuilder();

        for (String line : metadata) {
            if (!line.contains("Attribute")) {
                headerLines.append("% ".concat(line));
            } else {
                break;
            }
        }

        return headerLines.toString();
    }

    private static String getAttributeInfo(Stream<String> metadata) {
        return "";
    }

    private static String[] getDataInfo(Stream<String> data) {
        return data.filter(line -> !line.isEmpty())
                .map(ARFFConverter::convertLineToARFF)
                .toArray(String[]::new);
    }

    private static String convertLineToARFF(String line) {
        List<String> features = Arrays.asList(line.split(","));
        // Handle splits for tabs and spaces
        String classification = features.remove(classIndex);
        return "";
    }

    private static File buildArffFile(String fileName, String[] header, String[] data) {
        return new File(fileName);
    }


    private static void writeFile(String path, String[] lines) {
        System.out.println(lines[0]);
        // Create .arff file, write lines to it
    }
}
