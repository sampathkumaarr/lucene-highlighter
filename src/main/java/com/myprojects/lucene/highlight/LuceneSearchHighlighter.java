package com.myprojects.lucene.highlight;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

import static com.myprojects.lucene.constants.Constants.INDEX_DIR_PATH_FILES_CONTENT_STORE;

public class LuceneSearchHighlighter {

    public static void main(String[] args) throws Exception 
    {
        //Get directory reference
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR_PATH_FILES_CONTENT_STORE));
         
        //Index reader - an interface for accessing a point-in-time view of a lucene index
        IndexReader indexReader = DirectoryReader.open(dir);
         
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
         
        //analyzer with the default stop words
        Analyzer analyzer = new StandardAnalyzer();
         
        //Query parser to be used for creating TermQuery
        QueryParser qp = new QueryParser("contents", analyzer);
         
        //Create the query
        Query query = qp.parse("cottage private discovery concluded");
         
        //Search the lucene documents
        TopDocs hits = indexSearcher.search(query, 10);

        //Uses HTML &lt;B&gt;&lt;/B&gt; tag to highlight the searched terms
        Formatter formatter = new SimpleHTMLFormatter();
         
        //It scores text fragments by the number of unique query terms found
        //Basically the matching score in layman terms
        QueryScorer scorer = new QueryScorer(query);
         
        //used to markup highlighted terms found in the best sections of a text
        Highlighter highlighter = new Highlighter(formatter, scorer);
         
        //It breaks text up into same-size texts but does not split up spans
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);
         
        //breaks text up into same-size fragments with no concerns over spotting sentence boundaries.
        //Fragmenter fragmenter = new SimpleFragmenter(10);
         
        //set fragmenter to highlighter
        highlighter.setTextFragmenter(fragmenter);
         
        //Iterate over found results
        for (int i = 0; i < hits.scoreDocs.length; i++) 
        {
            int docid = hits.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docid);
            String title = doc.get("path");
             
            //Printing - to which document result belongs
            System.out.println("Path " + " : " + title);
             
            //Get stored text from found document
            String text = doc.get("contents");
 
            //Create token stream
            TokenStream stream = TokenSources.getAnyTokenStream(indexReader, docid, "contents", analyzer);
             
            //Get highlighted text fragments
            String[] frags = highlighter.getBestFragments(stream, text, 10);
            for (String frag : frags) 
            {
                System.out.println("=======================");
                System.out.println(frag);
            }
        }
        dir.close();
    }
}