import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import static org.junit.Assert.*;

public class DocumentIteratorTest {

    @Test
    public void testHasNext() throws FileNotFoundException {
        Reader r = new FileReader("TestFile0.txt");
        //RandomAccessFile file = new RandomAccessFile("src/TestFile0.txt", "r");
        DocumentIterator di = new DocumentIterator(r, 4);
        assertTrue(di.hasNext());
    }

    @Test
    public void testNext() throws FileNotFoundException {
        Reader r = new FileReader("TestFile0.txt");
        //RandomAccessFile file = new RandomAccessFile("src/TestFile0.txt", "r");
        DocumentIterator di = new DocumentIterator(r, 4);
        assertTrue(di.hasNext());
        assertEquals("thishwisabout", di.next());
        assertEquals("hwisaboutcatching", di.next());
        assertEquals("isaboutcatchingplagiarists", di.next());
        assertFalse(di.hasNext());
    }
}