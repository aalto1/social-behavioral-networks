import java.io.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.lucene.document.Document;


import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Created by aalto on 1/25/17.
 */



public abstract class SBN {
    private static final String STREAM_PATH = "stream"+ File.separator;


    public static void main(String [] agrs) throws IOException {
        //LuceneIndexer.generateIndex(STREAM_PATH);
        ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", "270839361");
        e_0_1();

    }


    public static void e_0_1() throws IOException {
        BufferedReader br_names = getBufferedReader("data/0-1/screennames.txt");
        BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
        String name, id;

        while((name= br_names.readLine()) != null && (id= br_IDs.readLine()) != null){
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", id);
            classifyUser(documents, name, id);
        }

    }

    public static void classifyUser(ArrayList<Document> documents, String name, String id) throws IOException {
        ArrayList<String> yHash = new ArrayList<>(); yHash.add("iovotosi"); yHash.add("bastaunsi"); yHash.add("sì"); yHash.add("si");
        ArrayList<String> nHash = new ArrayList<>(); nHash.add("iovotono");  nHash.add("iodicono"); nHash.add("no"); nHash.add("votano");
        ArrayList<String> finalHash;
        int no  = 0; int yes = 0;
        for(Document d : documents){
            for(String s: d.getValues("hashtags")){
                if(yHash.contains(s.toLowerCase())) yes++;
                if(nHash.contains(s.toLowerCase())) no++;
            }
        }

        String supporter; String tweet; String distribution;
        if(no>yes){
            //the user has more no hashtags
            supporter = "out/noSupporters.txt"; tweet = "out/noTweets.txt"; distribution = "out/noDistribution.txt";
            finalHash = nHash;

        }else{
            //the user has more yes hashtags
            supporter   = "out/yesSupporters.txt"; tweet = "out/yesTweet.txt"; distribution = "out/yesDistribution.txt";
            finalHash = yHash;
        }
        BufferedWriter bwSupporter, bwTweet, bwDistribution;

        bwSupporter = getBufferedWriter(supporter, true);
        bwSupporter.write(String.format("%s,%s,", name, id)+"\n");

        bwDistribution = getBufferedWriter(distribution, true);
        bwTweet = getBufferedWriter(tweet, true);
        for (Document d: documents) {
            Boolean relevant = false;
            for(String s: d.getValues("hashtags")) {
                if (finalHash.contains(s.toLowerCase())) {
                    relevant = true; break;
                }
            }
            if(relevant){
                //bwTweet.write(String.format("%s,%s,%s", name, id, d.getField("tweetText")) + "\n");
                bwTweet.write(d.getField("tweetText") + "\n");
                Date date = new Date(Long.parseLong(d.get("creationDateMills")));
                bwDistribution.write(new SimpleDateFormat("dd_MM_yyyy").format(date) + "\n");
            }
        }
        bwSupporter.close(); bwTweet.close(); bwDistribution.close();
    }


    /*** EXERCISE 1.2 ***/


    public static void saxBuilder() throws IOException {
        String [] nTop1000 = getTop1000("out/noTweet.txt"); String [] yTop1000 = getTop1000("out/yesTweet.txt");
        /***for (nTop1000:){

        }

        for***/
    }

    public static String [] getTop1000(String FILENAME) throws IOException {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        BufferedReader br = getBufferedReader(FILENAME);
        String s; String [][] words = new String[2][];
        while((s=br.readLine())!=null){
            for(String word : s.split(" ")){
                if(map.putIfAbsent(word,1)==0) map.merge(word, 1, Integer::sum);
            }
        }
        words[0] = (String []) map.keySet().toArray();
        words[1] = Arrays.toString(map.values().toArray()).split("[\\[\\]]")[1].split(", ");
        Arrays.sort(words, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return Integer.compare(Integer.parseInt(o1[1]),Integer.parseInt(o1[1]));
            }
        });
        return words[0];
    }

    /*** EXERCISE 1.3 ***/

    public static void co_Occurence_Graph(){

    }



    /*** EXERCISE 1.4 ***/



    /*** AUXILIARY FUNCTIONS ***/
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
