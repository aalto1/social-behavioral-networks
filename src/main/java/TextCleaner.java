/**
 * Created by aalto on 6/3/17.
 */


import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

/**
 *
 * @author frld
 */
public class TextCleaner {

    private CharArraySet stopWords;
    private String regex = null;

    public TextCleaner(){
        stopWords = ItalianAnalyzer.getDefaultStopSet();
        regex = "[^a-zA-Z0-9#@_àòèùéì ]";
    }

    public String[] cleanAndTokenize(String text){

        String txt = text.replaceAll("'", " ");
        txt = txt.replaceAll("@", " @");
        txt = txt.replaceAll("#", " #");
        String[] tokens = txt.replaceAll(this.regex, "").toLowerCase().split("\\s+");
        ArrayList<String> tkns = new ArrayList(Arrays.asList(tokens));
        tkns.removeIf(x -> {return (x.startsWith("http") | x.equalsIgnoreCase("rt"));});
        tkns.removeIf(x -> {return (this.stopWords.contains(x));});
        tkns.removeIf(x -> {return (x.length()<2);});
        tokens = tkns.toArray(new String[tkns.size()]);

        return tokens;
    }

}