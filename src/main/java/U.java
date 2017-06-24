import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aalto on 6/24/17.
 */
public class U {

    private static final CharArraySet stopwords = ItalianAnalyzer.getDefaultStopSet();

    public static String[] stem(String text){

        String txt = text.replaceAll("'", " ").replaceAll("@", " @").replaceAll("#", " #");
        String[] tokens = txt.replaceAll("[^a-zA-Z0-9#@_àòèùéì ]", "").toLowerCase().split("\\s+");
        ArrayList<String> tkns = new ArrayList(Arrays.asList(tokens));
        tkns.removeIf(x -> (x.startsWith("http") | x.equalsIgnoreCase("rt")));
        tkns.removeIf(x -> (stopwords.contains(x)));
        tkns.removeIf(x -> (x.length()<2));
        tokens = tkns.toArray(new String[tkns.size()]);

        return tokens;
    }

    public static void storeArrayList(List l, BufferedWriter bw) throws IOException {
        bw.write(l.toString().replace("[", "").replace("]", "")+"\n");
    }

    public static ArrayList<String> fetchArrayList(String FILENAME) throws IOException {
        BufferedReader br = getBufferedReader(FILENAME);
        ArrayList<String> list = new ArrayList<>();
        String line;
        while((line=br.readLine())!=null){
            list.add(line);
        }
        return list;
    }

    static BufferedReader getBufferedReader(String FILENAME) throws FileNotFoundException {
        return new BufferedReader(new FileReader(FILENAME));
    }

    static BufferedWriter getBufferedWriter(String FILENAME, Boolean append) throws IOException {
        if(append == true){
            return new BufferedWriter(new FileWriter(FILENAME, true));
        }else {
            return new BufferedWriter(new FileWriter(FILENAME));
        }
    }


}
