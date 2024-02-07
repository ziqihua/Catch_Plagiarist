import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.*;
import java.util.*;

public class DocumentsProcessorTest {
    static InputStream originalIn;

    @Test
    public void testProcessDocuments() {
        DocumentsProcessor dp = new DocumentsProcessor();
        String directoryPath = "TestProcessDocsDir";
        int n = 2;
        Map<String, List<String>> docNGrams = dp.processDocuments(directoryPath, n);
        List<String> file1Grams = new ArrayList<>();
        file1Grams.add("thisis");
        file1Grams.add("isa");
        file1Grams.add("afile");
        List<String> file2Grams = new ArrayList<>();
        file2Grams.add("thisis");
        file2Grams.add("isanother");
        file2Grams.add("anotherfile");
        assertEquals(file1Grams,docNGrams.get("file1.txt"));
        assertEquals(file2Grams,docNGrams.get("file2.txt"));
    }

    @Test
    public void testStoreNGrams() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        String directoryPath = "TestNGramStoreDir";
        int n = 4;
        Map<String, List<String>> docs = dp.processDocuments(directoryPath, n);
        String nwordFilePath = "testOnlyStoreAllGrams.txt";
        List<Tuple<String, Integer>> actualTuple = dp.storeNGrams(docs, nwordFilePath);
        List<Tuple<String, Integer>> expectedTuple = new ArrayList<>();
        expectedTuple.add(new Tuple<>("file01.txt", 28));
        expectedTuple.add(new Tuple<>("file02.txt", 42));
        for (int i = 0; i < actualTuple.size(); i++) {
            assertEquals(expectedTuple.get(i).getLeft(), actualTuple.get(i).getLeft());
            assertEquals(expectedTuple.get(i).getRight(), actualTuple.get(i).getRight());
        }
        originalIn = System.in;
        InputStream actualAllGramsFile = new FileInputStream(nwordFilePath);
        System.setIn(actualAllGramsFile);
        StringBuilder actualAllGrams = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            actualAllGrams.append(scanner.nextLine());
        }
        String expectedGrams =
            "thisisatest isatestdocument thisisalsoa isalsoatest alsoatestdocument ";
        assertEquals(expectedGrams, actualAllGrams.toString());
        System.in.close();
        System.setIn(originalIn);
    }

    @Test
    public void testComputeSimilarities() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> eachDocNgrams = dp.processDocuments("TestSimilarities", 3);
        List<Tuple<String, Integer>> fileIdx =
            dp.storeNGrams(eachDocNgrams, "testSimilaritiesOutput.txt");
        TreeSet<Similarities> result =
            dp.computeSimilarities("testSimilaritiesOutput.txt", fileIdx);
        assertEquals(3, result.size());
    }

    @Test
    public void testPrintSimilarities() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> eachDocNgrams = dp.processDocuments("TestSimilarities", 3);
        List<Tuple<String, Integer>> fileIdx =
            dp.storeNGrams(eachDocNgrams, "testSimilaritiesOutput.txt");
        TreeSet<Similarities> result =
            dp.computeSimilarities("testSimilaritiesOutput.txt", fileIdx);
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        dp.printSimilarities(result, 0);
        String actualOutput = outputStream.toString();
        String expectedOutput =
            "file001.txt and file003.txt: 3\nfile001.txt and file002.txt: 3\n" +
            "file002.txt and file003.txt: 3\n";
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testProcessAndStore() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        String directoryPath = "TestNGramStoreDir";
        int n = 4;
        String sequenceFile = "testProcessStoreAllGrams.txt";
        List<Tuple<String, Integer>> actualTuple =
            dp.processAndStore(directoryPath, sequenceFile, n);
        List<Tuple<String, Integer>> expectedTuple = new ArrayList<>();
        expectedTuple.add(new Tuple<>("file01.txt", 28));
        expectedTuple.add(new Tuple<>("file02.txt", 42));
        for (int i = 0; i < actualTuple.size(); i++) {
            assertEquals(expectedTuple.get(i).getLeft(), actualTuple.get(i).getLeft());
            assertEquals(expectedTuple.get(i).getRight(), actualTuple.get(i).getRight());
        }
        originalIn = System.in;
        InputStream actualAllGramsFile = new FileInputStream(sequenceFile);
        System.setIn(actualAllGramsFile);
        StringBuilder actualAllGrams = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            actualAllGrams.append(scanner.nextLine());
        }
        String expectedGrams =
            "thisisatest isatestdocument thisisalsoa isalsoatest alsoatestdocument ";
        System.in.close();
        System.setIn(originalIn);
        assertEquals(expectedGrams, actualAllGrams.toString());
    }
}