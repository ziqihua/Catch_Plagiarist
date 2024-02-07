import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private BufferedReader br;

    private int c = -1;
    private int numGram;

    public DocumentIterator(Reader r, int n) {
        this.r = r;
        this.numGram = n;
        this.br = new BufferedReader(this.r);
        skipNonLetters();
    }

    /**
     * if non-letter is encountered, skip it and continue reading
     */
    private void skipNonLetters() {
        try {
            this.c = this.br.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.br.read();
            }
        } catch (IOException e) {
            this.c = -1;
        }
    }

    @Override
    public boolean hasNext() {
        return (c != -1);
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String answer = "";
        int wordCount = 0;

        try {
            while (wordCount < this.numGram) {
                answer = answer + Character.toLowerCase((char)this.c);
                this.c = this.br.read();

                // Check if we encounter the end of the file
                if (c == -1) {
                    return answer;
                }
                // Check if we encounter a non-Character
                if (!Character.isLetter(this.c)) {
                    wordCount++;
                    if (br.markSupported() && wordCount == 1) {
                        br.mark(1024); // Set mark without a limit
                    }
                    this.skipNonLetters();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        try {
            if (c != -1) {
                this.br.reset();
                this.skipNonLetters();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return answer; // return the next n-gram in the file
    }
}
