import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeHandler{

        private String basicFile;
        private int index;

    
        public CodeHandler(String fileName) throws IOException{
            Path myPath = Paths.get(fileName);
            this.basicFile = new String(Files.readAllBytes(myPath));
        }

        // TODO : This looks "i" characters ahead and returns that character; doesn't move the index
        public char Peek(int i){
            return 'p';
        }

        // TODO : This will return a string of the next "ith" character, but doesn't move the index
        public String peekString(int i){
            return "";
        }

        // TODO : moves the index ahead "i" positions
        public void swallow(int i){

        }
        // TODO : returns true if we are at the end of the document
        public boolean isDone(){
            return false;
        }

        //TODO : returns the rest of the document as a string
        public String remainder(){
            return "";
        }


}
