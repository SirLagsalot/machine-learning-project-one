import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ARFFConverter {

    private static final String ATTRIBUTE_TAG = "@ATTRIBUTE";
    private static final String DATA_TAG = "@DATA";
    private static final String RELATION_TAG = "@RELATION";
    private static final String COMMENT_SYMBOL = "% ";

    private static int classIndex = -1;

    /**
     * args[0]: Name of data set without file extension
     * args[1]: Output file path
     */
    public static void main(String[] args) {
        File metadataFile = new File(args[0].concat(".metadata"));
        File dataFile = new File(args[0].concat(".data"));

        Stream<String> metaDataStream = getFileStream(metadataFile);
        Stream<String> dataStream = getFileStream(dataFile);

        assert metaDataStream != null;
        assert dataStream != null;

        String[] dataSetPath = args[0].split("/");
        String datSetName = dataSetPath[dataSetPath.length -1];

        List<String> metadataList = metaDataStream.collect(Collectors.toList());
        String headerInfo = getHeaderInfo(metadataList);
        String relationInfo = RELATION_TAG.concat(" " + datSetName);
        String attributeInfo = getAttributeInfo(metadataList);
        String dataInfo = getDataInfo(dataStream);

        writeFile(args[1], headerInfo, relationInfo, attributeInfo, dataInfo);
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
                headerLines.append(COMMENT_SYMBOL.concat(line)).append("\n");
            } else {
                break;
            }
        }

        return headerLines.toString();
    }

    private static String getAttributeInfo(List<String> metadata) {
        StringBuilder formattedAttributes = new StringBuilder();
        formattedAttributes.append(ATTRIBUTE_TAG).append("\n");
        Iterator<String> iterator = metadata.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().contains("Attribute Information")) {
                iterator.forEachRemaining(line -> {
                    if (line.contains("Classification index")) {
                        String[] split = line.split(":\\s");
                        classIndex = Integer.parseInt(split[split.length -1]);
                    } else if (!line.isEmpty()) {
                        formattedAttributes.append(ATTRIBUTE_TAG.concat(" ").concat(line)).append("\n");
                    }
                });
                return formattedAttributes.toString();
            }
        }
        return null;
    }

    private static String getDataInfo(Stream<String> data) {
        StringBuilder formattedData = new StringBuilder();
        formattedData.append(DATA_TAG).append("\n");
        data.forEach(line -> {
            formattedData.append(convertLineToARFF(line));
            formattedData.append("\n");
        });
        return formattedData.toString();
    }

    /*
        Lines of data can be either comma, space, or tab separated.
        Outputs a line formatted as comma separated attribute values followed by the classification
     */
    private static String convertLineToARFF(String line) {
        Pattern splitRegex = Pattern.compile(",|\t|\\s");
        List<String> attributes = Arrays.asList(splitRegex.split(line));
        String classification = attributes.remove(classIndex);

        StringBuilder arffLine = new StringBuilder();
        attributes.forEach(attr -> {
            arffLine.append(attr);
            arffLine.append(", ");
        });
        arffLine.append(classification);

        return arffLine.toString();
    }

    private static void writeFile(String path, String... fileSections) {
        for (String section : fileSections) {
            System.out.println(section);
        }
    }
}
