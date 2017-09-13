import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ARFFConverter {

    private static final String ATTRIBUTE_TAG = "@ATTRIBUTE";
    private static final String DATA_TAG = "@DATA";
    private static final String RELATION_TAG = "@RELATION";

    /**
     * args[1]: Name of dataset
     * args[2]: List position of classification attribute; {first, last}
     */
    public static void main(String[] args) {
        File metadataFile = new File(args[0].concat(".header"));
        File dataFile = new File(args[0].concat(".data"));
        boolean classLblIsFirst = Objects.equals(args[1], "first");

        Stream<String> metaDataStream = getFileStream(metadataFile);
        Stream<String> dataStream = getFileStream(dataFile);

        String headerInfo = getHeaderInfo(metaDataStream);
        String relationInfo = getRelationInfo(metaDataStream);
        String attributeInfo = getAttributeInfo(metaDataStream);
        String dataInfo = getDataInfo(dataStream);

        if (null != dataStream) {
            dataStream
                    .filter(line -> !line.isEmpty())
                    .map(line -> convertLineToARFF(line, classLblIsFirst))
                    .toArray(String[]::new);
        } else {
            System.exit(-1);
        }
    }

    private static Stream<String> getFileStream(File file) {
        try {
            return new BufferedReader(new FileReader(file)).lines();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String getHeaderInfo(Stream<String> metadata) {
        return "";
    }

    private static String getRelationInfo(Stream<String> metadata) {
        return "";
    }

    private static String getAttributeInfo(Stream<String> metadata) {
        return "";
    }

    private static String getDataInfo(Stream<String> metadata) {
        return "";
    }

    private static String convertLineToARFF(String line, boolean first) {
        List<String> features = Arrays.asList(line.split(","));
        String classification = first ? features.remove(0) : features.remove(features.size() - 1);
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
