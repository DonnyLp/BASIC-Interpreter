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

    public char peek(int i) {
        return basicFile.charAt(i);
    }



    public String peekString(int i) {
        return this.basicFile.substring(index, i);
    }


    public void swallow(int i) {
        this.index += i;
    }


    public boolean isDone() {
        return index >= this.basicFile.length();
    }


    public String remainder() {
        return this.basicFile.substring(this.index);
    }

    public int getIndex(){
        return this.index;
    }

    public int getFileLength(){
        return this.basicFile.length();
    }
}
