import java.io.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.lucene.document.Document;


import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Created by aalto on 1/25/17.
 */



public abstract class SBN {
    private static final String STREAM_PATH = "stream"+ File.separator;


    public static void main(String [] agrs) throws IOException {
        LuceneIndexer.generateIndex(STREAM_PATH);
        //ex01();
    }

    public static void elements() throws IOException {
        BufferedReader br_names = getBufferedReader("data/0-1/screennames.txt");
        BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
        BufferedWriter ybw = getBufferedWriter("de"); BufferedWriter nbw = getBufferedWriter("de");
        String name, id;
        Object2IntOpenHashMap<String> dates = new Object2IntOpenHashMap<>();

        while((name= br_names.readLine()) != null && (id= br_IDs.readLine()) != null){
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", id);
            classify(documents, name, id, ybw, nbw, dates);
        }

    }

    public static void classify(ArrayList<Document> documents, String name, String id, BufferedWriter nbw, BufferedWriter ybw) throws IOException {
        ArrayList<String> yHash = new ArrayList<>(); yHash.add("iovotosi"); yHash.add("bastaunsi"); yHash.add("sì"); yHash.add("si");
        ArrayList<String> nHash = new ArrayList<>(); nHash.add("iovotono");  nHash.add("iodicono"); nHash.add("no");
        int no  = 0; int yes = 0;
        for(Document d : documents){
            for(String s: d.getValues("tweetText")){
                if(yHash.contains(s.toLowerCase())) yes++;
                if(nHash.contains(s.toLowerCase())) no++;

            }
        }

        SimpleDateFormat inter = new SimpleDateFormat("dd_MM_yyyy").format(date);


        if(no>yes){
            //the user has more no hashtags
            nbw.write(String.format("%s,%s,", name, id));
        }else{
            //the user has more yes hashtags
            ybw.write(String.format("%s,%s,", name, id));
        }

    }



    /*** EXERCISE 1.2 ***/


    public static void saxBuilder(){
        String nTop1000 = getTop1000(); String yTop1000 = getTop1000();
        for (nTop1000:){

        }
        
        for
    }

    public static String getTop1000()



    /*** AUXILIARY FUNCTIONS ***/
    static BufferedReader getBufferedReader(String FILENAME) throws FileNotFoundException {
        return new BufferedReader(new FileReader(FILENAME));
    }

    static BufferedWriter getBufferedWriter(String FILENAME) throws IOException {
        return new BufferedWriter(new FileWriter(FILENAME));
    }


}
