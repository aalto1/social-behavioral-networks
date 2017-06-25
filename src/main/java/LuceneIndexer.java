/**
 * Created by aalto on 6/3/17.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.UserMentionEntity;


public class LuceneIndexer {

    private final static String INDEX_FOLDER = "index"+File.separator;

    public LuceneIndexer(){}

    public static void generateIndex(String streamPath){

        System.out.println("Indexing task...");

        try{

            File indexFolder = new File(INDEX_FOLDER);
            if(indexFolder.isDirectory() && indexFolder.listFiles().length > 0)
                for(File file : indexFolder.listFiles()){
                    file.delete();
                }

            Directory dir = new SimpleFSDirectory(indexFolder);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
            IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_41,analyzer);
            IndexWriter writer = new IndexWriter(dir,cfg);

            File streamFolder = new File(streamPath);
            for (File tweetsDir : streamFolder.listFiles()) {
                System.out.println("Reading tweets in "+tweetsDir.getAbsolutePath());
                for (File file : tweetsDir.listFiles()) {

                    GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                    BufferedReader br = new BufferedReader(new InputStreamReader(gzip, "UTF-8"));

                    String content;
                    while((content = br.readLine()) != null){

                        content = content.substring(content.indexOf("{"));

                        //                    parse the JSON string in a "Status" object
                        Status tweet = TwitterObjectFactory.createStatus(content);

                        if(tweet.getLang().equals("it")){
                            Document doc = new Document();
                            doc.add(new StringField("userID", Long.toString(tweet.getUser().getId()), Field.Store.YES));

                            HashtagEntity[] he = tweet.getHashtagEntities();
                            for (HashtagEntity he1 : he) {
                                doc.add(new StringField("hashtags", he1.getText(), Field.Store.YES));
                            }

                            UserMentionEntity[] mentions = tweet.getUserMentionEntities();
                            for (UserMentionEntity m : mentions) {
                                doc.add(new StringField("mentions", m.getScreenName(), Field.Store.YES));
                            }
                            doc.add(new StringField("creationDateMills", Long.toString(tweet.getCreatedAt().getTime()), Field.Store.YES));
                            doc.add(new StringField("Date", getDateTimeStamp(new Date(tweet.getCreatedAt().getTime())), Field.Store.YES));

                            doc.add(new TextField("tweetText", tweet.getText(), Field.Store.YES));

                            writer.addDocument(doc);
                        }
                    }

                }

            }

            writer.commit();
            writer.close();

            System.out.println("..Done!");

        }catch(IOException e){

        } catch (TwitterException ex) {
            Logger.getLogger(LuceneIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getDateTimeStamp(Date date) {
        return new SimpleDateFormat("dd_MM_yyyy").format(date);
    }

    public static ArrayList<Document> queryOnField(String field, String value){

        ArrayList<Document> results = new ArrayList();

        try{

            Directory idxdir = FSDirectory.open(new File(INDEX_FOLDER));
            IndexReader ir = DirectoryReader.open(idxdir);
            IndexSearcher searcher = new IndexSearcher(ir);
            Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_41);//new StandardAnalyzer(Version.LUCENE_41);

            /*
                QUERYING
            */
            TotalHitCountCollector collector = new TotalHitCountCollector();
            QueryParser parser = new QueryParser(Version.LUCENE_41, field, analyzer);
            Query q = parser.parse(value);
            searcher.search(q, collector);
            TopDocs top = searcher.search(q, Math.max(1, collector.getTotalHits()));
            ScoreDoc[] hits = top.scoreDocs;

            for(ScoreDoc entry : hits){
                Document doc = searcher.doc(entry.doc);
                results.add(doc);
            }
            ir.close();

        }catch(IOException | ParseException e){
            Logger.getLogger(LuceneIndexer.class.getName()).log(Level.SEVERE, null, e);
        }

        return results;
    }

    public static ObjectOpenHashSet<String> userIDbyQuery(String field, ArrayList<String> valuesList){

        ObjectOpenHashSet<String> results = new ObjectOpenHashSet<>();

        try{

            Directory idxdir = FSDirectory.open(new File(INDEX_FOLDER));
            IndexReader ir = DirectoryReader.open(idxdir);
            IndexSearcher searcher = new IndexSearcher(ir);
            Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_41);//new StandardAnalyzer(Version.LUCENE_41);

            /*
                QUERYING
            */
            TotalHitCountCollector collector = new TotalHitCountCollector();
            QueryParser parser = new QueryParser(Version.LUCENE_41, field, analyzer);
//            Query q = parser.parse(value);
            Query q = parser.parse(String.join(" ", valuesList));
            searcher.search(q, collector);
            TopDocs top = searcher.search(q, Math.max(1, collector.getTotalHits()));
            ScoreDoc[] hits = top.scoreDocs;

            for(ScoreDoc entry : hits){
                String id = searcher.doc(entry.doc).get("userID");
                if(!results.contains(id))
                    results.add(id);
            }
            ir.close();

        }catch(IOException | ParseException e){
            Logger.getLogger(LuceneIndexer.class.getName()).log(Level.SEVERE, null, e);
        }

        return results;
    }


    public static int queryAndWrite(String field, ArrayList<String> valuesList, String outfilepath){

        int tweetsCounter = 0;

        try{

            Directory idxdir = FSDirectory.open(new File("index/"));
            IndexReader ir = DirectoryReader.open(idxdir);
            IndexSearcher searcher = new IndexSearcher(ir);
            Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_41);//new StandardAnalyzer(Version.LUCENE_41);

            /*
                QUERYING
            */
            TotalHitCountCollector collector = new TotalHitCountCollector();
            QueryParser parser = new QueryParser(Version.LUCENE_41, field, analyzer);
            Query q = parser.parse(String.join(" ", valuesList));
            searcher.search(q, collector);
            TopDocs top = searcher.search(q, Math.max(1, collector.getTotalHits()));
            ScoreDoc[] hits = top.scoreDocs;

            tweetsCounter = hits.length;

            System.out.println(field + ":\t" + hits.length);
            for(ScoreDoc entry : hits){
                Document doc = searcher.doc(entry.doc);
                String id = doc.get("userID");
                RWServices.writeString(id, outfilepath);
            }
            ir.close();

        }catch(IOException | ParseException e){
            Logger.getLogger(LuceneIndexer.class.getName()).log(Level.SEVERE, null, e);
        }

        return tweetsCounter;
    }


}