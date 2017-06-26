import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aalto on 6/26/17.
 */
public class Partisan {

    Long2IntOpenHashMap obsessions;
    Long authority;
    long id;
    int noScore;
    int yesScore;

    public Partisan(){

    }

    public ArrayList<String> computePartisanObsessions(){
        ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", ""+id);
        for(Document d : documents){
            int a = d.getField("mentions");
        }
    }

    /***Exercise 1.3 classify the partisans***/

    public static void classifyPartisan(LongOpenHashSet partisans) throws IOException {
        for (Long id: partisans) {
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", ""+id);
            classifyPolitician(documents, "GEEENO", ""+id);

        }

    }
}
