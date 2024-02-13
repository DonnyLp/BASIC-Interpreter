import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeHandler {

    private final String  basicFile;
    private int index;

    public CodeHandler(String fileName) throws IOException {
        Path myPath = Paths.get(fileName);
        this.basicFile = new String(Files.readAllBytes(myPath));
        this.index =  0;
    }

    //Grabs the characters, i characters ahead
    public char peek(int i) {
        return basicFile.charAt(index + i);
    }

    //grabs the collection of chars, i characters ahead
    public String peekString(int i) {
        return basicFile.substring(index, i + index);
    }

    //swallows the characters, i characters
    public void swallow(int i) {
        index += i;
    }

    //checks if at end of file
    public boolean isDone() {
        return index >= this.basicFile.length();
    }

    //returns the remainder of the file at the current index
    public String remainder() {
        return basicFile.substring(index);
    }


    public int getIndex(){
        return index;
    }

    public int getFileLength(){
        return basicFile.length();
    }
}
