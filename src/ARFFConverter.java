import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        assert args[0] != null;
        assert args[1] != null;

        File metadataFile = new File(args[0].concat(".metadata"));
        File dataFile = new File(args[0].concat(".data"));

        Stream<String> metaDataStream = getFileStream(metadataFile);
        Stream<String> dataStream = getFileStream(dataFile);

        assert metaDataStream != null;
        assert dataStream != null;

        String[] dataSetPath = args[0].split("/");
        String datSetName = dataSetPath[dataSetPath.length - 1];

        List<String> metadataList = metaDataStream.collect(Collectors.toList());

        String headerInfo = getHeaderInfo(metadataList);
        String relationInfo = RELATION_TAG.concat(" ").concat(datSetName).concat("\n");
        String attributeInfo = getAttributeInfo(metadataList);
        String dataInfo = getDataInfo(dataStream);

        String outputPath = args[1] != null ? args[1] : "";

        writeFile(outputPath, Arrays.asList(headerInfo, relationInfo, attributeInfo, dataInfo));
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
        Iterator<String> iterator = metadata.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().contains("Attribute Information")) {
                iterator.forEachRemaining(line -> {
                    if (line.contains("Classification index")) {
                        String[] split = line.split(":\\s");
                        classIndex = Integer.parseInt(split[split.length - 1]);
                    } else if (line.trim().length() > 0) {
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
        ArrayList<String> attributes = new ArrayList<>(Arrays.asList(splitRegex.split(line)));
        String classification = attributes.remove(classIndex);

        StringBuilder arffLine = new StringBuilder();
        attributes.forEach(attr -> {
            arffLine.append(attr);
            arffLine.append(", ");
        });
        arffLine.append(classification);

        return arffLine.toString();
    }

    private static void writeFile(String outputPath, List<String> lines) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
            writer.write(lines.stream()
                    .reduce((sum, currLine) -> sum + "\n" + currLine)
                    .orElseGet(null));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
