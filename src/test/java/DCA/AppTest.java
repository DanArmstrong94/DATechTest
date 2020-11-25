package DCA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    
    @Test
    public void validTestFile() throws IOException
    {
        File file = new File("valid.txt");

        if(file.exists()){
            file.delete();
        }

        assertTrue("File created", file.createNewFile());

        FileWriter writer = new FileWriter(file);
        writer.write("This is a test!");
        writer.close();

        String path = file.getAbsolutePath();
        assertTrue(App.validateFile(file));

        HashMap<String, Integer> map = App.textFileAnalytics(path);
        
        // wordCount, totalWordLengths, modalLenFreq, modalLen0, modalLen1, modalLen2, ...
        double averageWordLength = (double) map.get("totalWordLengths") / (double) map.get("wordCount");    // Casting to double here so we get those decimal places, we don't have our DecimalFormat here but we have a rational number so it's okay

        assertTrue(map.size() == 4);                    // Checking we have only 1 modal word length (4)
        assertTrue(map.get("wordCount") == 4);          // Checking the word count is 4
        assertTrue(averageWordLength == 2.75);          // Checking the total word lengths is 11, we also now know that the exclaimation point is removed from this calculation
        assertTrue(map.get("modalLenFreq") == 2);       // Checking the most used word length is used twice 
        assertTrue(map.get("modalLen0") == 4);          // Checking the most used word length is 4, again we can see the exclaimation point is removed

        File outputFile = new File("output" + file.getName());

        if(outputFile.exists()){
            outputFile.delete();
        }

        file.delete();
    }

    @Test
    public void invalidTestFile() throws IOException
    {  
        File file = new File("invalid.csv");

        if(file.exists()){
            file.delete();
        }

        assertTrue("File created", file.createNewFile());

        FileWriter writer = new FileWriter(file);
        writer.write("10,2,3,4");
        writer.close();

        assertFalse(App.validateFile(file));            // This should fail as the file type is not .txt

        file.delete();
    }

    @Test
    public void testingLongWord() throws IOException
    {
        File file = new File("longWord.txt");

        if(file.exists()){
            file.delete();
        }

        assertTrue("File created", file.createNewFile());

        FileWriter writer = new FileWriter(file);
        writer.write("pneumonoultramicroscopicsilicovolcanoconiosis");
        writer.close();

        String path = file.getAbsolutePath();
        assertTrue(App.validateFile(file));

        HashMap<String, Integer> map = App.textFileAnalytics(path);
        
        // wordCount, totalWordLengths, modalLenFreq, modalLen0, modalLen1, modalLen2, ...
        double averageWordLength = (double) map.get("totalWordLengths") / (double) map.get("wordCount");    // Casting to double here so we get those decimal places, we don't have our DecimalFormat here but we have a rational number so it's okay

        assertTrue(map.size() == 4);                    // Checking we have only 1 modal word length (any extras will increase the map size)
        assertTrue(map.get("wordCount") == 1);          // Checking the word count is 1
        assertTrue(averageWordLength == 45);            // Checking the total word lengths is 45 (one 45 letter word)
        assertTrue(map.get("modalLenFreq") == 1);       
        assertTrue(map.get("modalLen0") == 45);         // Checking the most used word length is 45 (only one word)

        File outputFile = new File("output" + file.getName());

        if(outputFile.exists()){
            outputFile.delete();
        }

        file.delete();
    }

    @Test
    public void punctuationInvalid() throws IOException
    {
        File file = new File("punctuation.txt");

        if(file.exists()){
            file.delete();
        }

        assertTrue("File created", file.createNewFile());

        FileWriter writer = new FileWriter(file);
        writer.write("! ? a?");
        writer.close();

        String path = file.getAbsolutePath();
        assertTrue(App.validateFile(file));

        HashMap<String, Integer> map = App.textFileAnalytics(path);
        
        // wordCount, totalWordLengths, modalLenFreq, modalLen0, modalLen1, modalLen2, ...
        double averageWordLength = (double) map.get("totalWordLengths") / (double) map.get("wordCount");    // Casting to double here so we get those decimal places, we don't have our DecimalFormat here but we have a rational number so it's okay

        assertTrue(map.size() == 4);                    // Checking we have only 1 modal word length (any extras will increase the map size)
        assertTrue(map.get("wordCount") == 1);          // Checking the word count is 1
        assertTrue(averageWordLength == 1);             // Checking the total word lengths is 1 (one 1 letter word)
        assertTrue(map.get("modalLenFreq") == 1);       
        assertTrue(map.get("modalLen0") == 1);          // Checking the most used word length is 1 (only one word)

        

        File outputFile = new File("output" + file.getName());

        if(outputFile.exists()){
            outputFile.delete();
        }

        file.delete();
    }

    @Test
    public void tooLongWord() throws IOException
    {
        File file = new File("tooLongWord.txt");

        if(file.exists()){
            file.delete();
        }

        assertTrue("File created", file.createNewFile());

        FileWriter writer = new FileWriter(file);
        writer.write("pneumonoultramicroscopicsilicovolcanoconiosisA");     // Note, this would make a 46 letter word which is too long for our assumptions
        writer.close();

        boolean exceptionTriggered = false;
        String path = file.getAbsolutePath();

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        assertTrue(App.validateFile(file));

        try{
            map = App.textFileAnalytics(path);  // Expecting an ArrayOutOfBoundsException
        }
        catch (Exception e){
            System.out.println(e);
            exceptionTriggered = true;          // If we hit this line we know that we have triggered an exception which is the aim of the test
        }
        
        assertTrue(exceptionTriggered);         // If we hit this line we havent triggered our exception which is not good!

        map.clear();

        File outputFile = new File("output" + file.getName());

        if(outputFile.exists()){
            outputFile.delete();
        }

        file.delete();
    }

    @Test
    public void quotations() throws IOException
    {
        File file = new File("quotations.txt");

        if(file.exists()){
            file.delete();
        }

        assertTrue("File created", file.createNewFile());

        FileWriter writer = new FileWriter(file);
        writer.write("\"This \'test\' is the best\"");
        writer.close();

        String path = file.getAbsolutePath();
        assertTrue(App.validateFile(file));

        HashMap<String, Integer> map = App.textFileAnalytics(path);
        
        // wordCount, totalWordLengths, modalLenFreq, modalLen0, modalLen1, modalLen2, ...
        double averageWordLength = (double) map.get("totalWordLengths") / (double) map.get("wordCount");    // Casting to double here so we get those decimal places, we don't have our DecimalFormat here but we have a rational number so it's okay

        assertTrue(map.size() == 4);                    // Checking we have only 1 modal word length (any extras will increase the map size)
        assertTrue(map.get("wordCount") == 5);          // Checking the word count is 5
        assertTrue(averageWordLength == 3.4);           // Checking the total word lengths is 3.4, this will confirm that we have removed all punctuation
        assertTrue(map.get("modalLenFreq") == 3);       // Should be 3
        assertTrue(map.get("modalLen0") == 4);          // Checking the most used word length is 4

        File outputFile = new File("output" + file.getName());

        if(outputFile.exists()){
            outputFile.delete();
        }

        file.delete();
    }
}
