import java.io.*;
import java.util.*;

public class DocumentsProcessor implements IDocumentsProcessor {
    @Override
    public Map<String, List<String>> processDocuments(String directoryPath, int n) {
        File directory = new File(directoryPath);
        File[] subFileList = directory.listFiles();

        Map<String, List<String>> docNGrams = new HashMap<>();

        if (subFileList != null) {
            // Sort the files (sorting based on file names)
            Arrays.sort(subFileList);
            for (File subFile : subFileList) {
                List<String> nGrams = new ArrayList<>();
                try {
                    Reader r = new FileReader(subFile);
                    DocumentIterator docIterator = new DocumentIterator(r, n);
                    while (docIterator.hasNext()) {
                        nGrams.add(docIterator.next());

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                docNGrams.put(subFile.getName(), nGrams);
            }
        }
        return docNGrams;
    }

    @Override
    public List<Tuple<String, Integer>> storeNGrams(
        Map<String, List<String>> docs, String nwordFilePath) {
        List<Tuple<String, Integer>> docSizes = new ArrayList<>();
        try (Writer w = new FileWriter(nwordFilePath)) {
            for (String docName : docs.keySet()) {
                List<String> nGrams = docs.get(docName);
                int totalByteCount = 0;
                for (String nGram : nGrams) {
                    int charCount = nGram.length(); // Use the length of the string directly
                    w.write(nGram + " ");
                    totalByteCount += charCount;
                    totalByteCount++; // Add the white space after each n-gram
                }
                Tuple<String, Integer> tuple = new Tuple<>(docName, totalByteCount);
                docSizes.add(tuple);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return docSizes;
    }

    @Override
    public TreeSet<Similarities> computeSimilarities(
        String nwordFilePath, List<Tuple<String, Integer>> fileindex) {
        TreeSet<Similarities> similaritiesSet = new TreeSet<>();
        Map<String, Set<String>> nGramsAll = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(nwordFilePath));
            int totalCharRead = 0;
            int fileIdx = 0;
            int filePathRead = fileindex.get(fileIdx).getRight();
            while (scanner.hasNext()) {
                String nGram = scanner.next();
                totalCharRead += nGram.length();
                totalCharRead++; //account for the whitespace between 2 n-grams
                while (totalCharRead > filePathRead) {
                    fileIdx++;
                    filePathRead += fileindex.get(fileIdx).getRight();
                }
                String curFileName = fileindex.get(fileIdx).getLeft();
                Set<String> filesContainNGram = nGramsAll.getOrDefault(nGram, new HashSet<>());

                if (filesContainNGram.isEmpty()) {
                    filesContainNGram.add(curFileName); //add the file name to the initiated new Set
                } else {
                    filesContainNGram.add(curFileName);
                    for (String fileAdded : filesContainNGram) {
                        if (!fileAdded.equals(curFileName)) {
                            Similarities sim = new Similarities(curFileName, fileAdded);
                            if (!similaritiesSet.contains(sim)) {
                                sim.setCount(1);
                                similaritiesSet.add(sim);
                            } else {
                                Similarities existingSim = similaritiesSet.floor(sim);
                                existingSim.setCount(existingSim.getCount() + 1);
                            }
                        }
                    }
                }
                nGramsAll.put(nGram, filesContainNGram);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return similaritiesSet;
    }

    @Override
    public void printSimilarities(TreeSet<Similarities> sims, int threshold) {
        Comparator<Similarities> descendingOrderComparator = new Comparator<Similarities>() {
            public int compare(Similarities o1, Similarities o2) {
                if (o1.getCount() == o2.getCount()) {
                    return o1.compareTo(o2);
                }
                return o2.getCount() - o1.getCount();
            }
        };

        Set<Similarities> result = new TreeSet<>(descendingOrderComparator);
        for (Similarities sim : sims) {
            if (sim.getCount() > threshold) {
                result.add(sim);
            }
        }

        for (Similarities sim : result) {
            if (sim.getFile1().compareTo(sim.getFile2()) > 0) {
                System.out.println(
                    sim.getFile2() + " and " + sim.getFile1() + ": " + sim.getCount());
            } else {
                System.out.println(
                    sim.getFile1() + " and " + sim.getFile2() + ": " + sim.getCount());
            }
        }
    }

    public List<Tuple<String, Integer>> processAndStore(
        String directoryPath, String sequenceFile, int n) {
        List<Tuple<String, Integer>> docsSizes = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] subFileList = directory.listFiles();
        if (subFileList != null) {
            try (Writer w = new FileWriter(sequenceFile)) {
                // Sort the files (sorting based on file names)
                Arrays.sort(subFileList);
                for (File subFile : subFileList) {
                    String subFileName = subFile.getName();
                    int totalByteCount = 0;
                    try {
                        Reader r = new FileReader(subFile);
                        DocumentIterator docIterator = new DocumentIterator(r, n);
                        while (docIterator.hasNext()) {
                            String nGram = docIterator.next();
                            int charCount = nGram.length(); // Use the length of the string directly
                            totalByteCount += charCount;
                            totalByteCount++; // Add the white space after each n-gram
                            w.write(nGram + " ");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Tuple<String, Integer> tuple = new Tuple<>(subFileName, totalByteCount);
                    docsSizes.add(tuple);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return docsSizes;
    }
}
