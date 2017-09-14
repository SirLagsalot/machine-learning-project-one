import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/*
    File converter for building .arff files given a file of metadata information
    and a file containing a comma, space, or tab separated list of data points.

    Authors: Zach Connelly, Karen Stengel, Keely Weisbeck
    Date: 14 September 2017
    Class: CSCI 447 Machine Learning
    Professor: Dr. John Sheppard
 */

public class ARFFConverter {

    private static final String ATTRIBUTE_TAG = "@ATTRIBUTE";
    private static final String DATA_TAG = "@DATA";
    private static final String RELATION_TAG = "@RELATION";
    private static final String COMMENT_SYMBOL = "% ";
    private static final String COMMA_SEP = ", ";
    private static final Pattern DATA_DELIMITER = Pattern.compile(",|,\\s|(\\t)+|(\\s)+");

    private static List<Integer> classIndices = new ArrayList<>();

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
            if (iterator.next().toLowerCase().contains("Attribute Information".toLowerCase())) {
                iterator.forEachRemaining(line -> {
                    if (line.toLowerCase().contains("Classification index".toLowerCase())) {
                        String[] indices = line.split(":\\s");
                        Arrays.asList(indices[indices.length - 1].split(",")).forEach(index -> classIndices.add(Integer.parseInt(index)));
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
        ArrayList<String> attributes = new ArrayList<>(Arrays.asList(DATA_DELIMITER.split(line)));

        ArrayList<String> classifications = new ArrayList<>();
        final int[] offset = {0};
        classIndices.forEach(index -> {
            classifications.add(attributes.remove(index - offset[0]));
            offset[0]++;
        });

        StringBuilder arffLine = new StringBuilder();

        attributes.forEach(attr -> arffLine.append(attr.concat(COMMA_SEP)));

        IntStream.range(0, classifications.size()).forEachOrdered(i -> {
            arffLine.append(classifications.get(i));
            if (i < classifications.size() - 1) {
                arffLine.append(COMMA_SEP);
            }
        });

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
