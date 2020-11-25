package DCA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class App 
{

    // Externalised these functions for simplicity and code clarity

    private static int[] initialiseArray(int[] array){ 
        for(int i=0; i < array.length; ++i){
            array[i] = 0;
        }
        return array;
    }

    private static String removePunctuation(String s){

        if(s.endsWith(".") || s.endsWith("?") || s.endsWith("!") || s.endsWith(",") || s.endsWith(":") || s.endsWith(";") || s.endsWith("\"") || s.endsWith("\'")) 
        {
            s = s.substring(0, s.length() - 1);
        }

        if(s.startsWith("\"") || s.startsWith("\'"))
        {
            s = s.substring(1);
        }

        return s;
    }

    public static boolean validateFile(File file){
        if(file.isFile() && file.getName().endsWith(".txt")){
            return true;
        }
        else{
            return false;
        }
    }

    public static HashMap<String, Integer> textFileAnalytics(String path) throws IOException {
        
        DecimalFormat df = new DecimalFormat("#.###");                              // Setting up a DecimalFormat for our averages later
        df.setRoundingMode(RoundingMode.CEILING);

        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
                    
            /****************************************************   
                
                We're going to assume the following:
                1) A word has to have at least one, English letter, number, or ampersand, [a-z | A-Z | 0-9 | &].
                2) Punctuation does not count towards the size of the word itself i.e. Test! is 4 letters long.
                3) The longest english word in a major dictionary (albeit contrived) is 45 letters long.
                4) Apostrophes within words, and punctuation in date formatted numbers, will count towards the length of said word i.e. 25/11/2020 is a size 10 word and Daniel's is a size 8 word.

            *****************************************************/

            int wordCount = 0;                                              // Cumulative word count
            int totalWordLengths = 0;                                       // Cumulative length of all the words
            int[] wordSizes = initialiseArray(new int[45]);                 // Longest word in the bible is 21 letters (Maher-shalal-hash-baz) although this is often shortened to 18 by removal of hyphens
                                                                            // We'll allow for the largest possible word from the oxford dictionary (45 letters)

            for (String line = null; (line = br.readLine()) != null;){      // We'll read in one line at a time from a buffered reader, this should save time
                String[] array = line.split(" ", -1);                       // Split on White Space
                for (int i=0; i<array.length ; ++i)                         // Iterate over all the words in the line (now separated into an array of size equal to the number of words in the line)
                {
                    array[i] = removePunctuation(array[i]);                 // Strip out any punctuation from the end of our words
                    int wordLength = array[i].length();
                    if (wordLength >= 1 && !array[i].equals("*******")){    // This is more of a guard to stop any "words" that might reach here of 0 length - shouldn't happen but lets be sure, and also ignores the "*******"'s we get on each date
                        ++wordCount;                                        // +1 to word count
                        ++wordSizes[wordLength - 1];                        // +1 to words of this size
                        totalWordLengths += wordLength;                     // We want to track the total length of all words together for our average later
                    }
                }
            }

            double averageWordLength = (double) totalWordLengths/ (double) wordCount;   // Casting to double here as we should expect some decimal places (3dp guaranteed in the print using a Decimal Format)

            int highestWordLengthFreq = 0;
            
            String baseFileName = new File(path).getName();

            File file = new File("output" + baseFileName);

            if(file.exists()){
                file.delete();
            }

            FileWriter writer = new FileWriter(file);

            writer.write("File Name: " + baseFileName + "\n");

            ArrayList<Integer> highestWordLength = new ArrayList<Integer>();            // This ArrayList allows us to have a dynamically allocated Array just in case we end up with multiple "highest frequency word lengths"
            for(int k=0;k<wordSizes.length;++k){                                        // This logic lets us find our modal word lengths
                if(wordSizes[k] != 0){
                    writer.write("Number of words of length " + (k+1) + " is " + wordSizes[k] + "\n");
                    System.out.println("Number of words of length " + (k+1) + " is " + wordSizes[k]);
                    if(wordSizes[k] > highestWordLengthFreq){                           // In this case we have a new modal length, so we reset our ArrayList
                        highestWordLengthFreq = wordSizes[k];
                        highestWordLength.clear();
                        highestWordLength.add(k+1);
                    }
                    else if(wordSizes[k] == highestWordLengthFreq){                     // In this case we have multiple modal lengths, so we add to our ArrayList
                        highestWordLength.add(k+1);
                    }
                }
            }

            writer.write("Word Count: " + wordCount + "\n");
            writer.write("Average Word Length: " + df.format(averageWordLength) + "\n");
            writer.write("The most frequently occuring word length is " + highestWordLengthFreq + ", for word length " + highestWordLength + "\n");
            writer.write("*****************************************************************************************");
            writer.close();

            System.out.println("Word Count: " + wordCount);
            System.out.println("Average Word Length: " + df.format(averageWordLength));
            System.out.println("The most frequently occuring word length is " + highestWordLengthFreq + ", for word length " + highestWordLength);

            returnMap.put("wordCount", wordCount);
            returnMap.put("totalWordLengths", totalWordLengths);
            returnMap.put("modalLenFreq", highestWordLengthFreq);

            for(int p=0;p<highestWordLength.size();++p){
                returnMap.put("modalLen"+p, highestWordLength.get(p));
            }
            
            System.out.println();                                                       // Purely for clarity when reading multiple files at once

            return returnMap;
            // wordCount, totalWordLengths, modalLenFreq, modalLen0, modalLen1, modalLen2, ...
        }
    }

    // ******************************************************** Main ******************************************************** //
    public static void main( String[] args ) throws IOException
    {
        

        File folder = new File("./SingleTest");                                     // Files will be read from this location, we're going with an input folder method
        File[] listOfFiles = folder.listFiles();
    
        for (int l=0; l < listOfFiles.length; ++l){

            File file = listOfFiles[l];
            boolean validFile = validateFile(file);                                 // Basic file validation

            if (validFile){                                                         

                String path = file.getAbsolutePath();

                System.out.println("File Name: " + file.getName());

                HashMap<String, Integer> analyticsMap = textFileAnalytics(path);

                analyticsMap.clear();
            }
            else{
                System.out.println("Error: File " + file.getName() + " is not a .txt file, please supply only .txt files");
            }
        }
    }
}
