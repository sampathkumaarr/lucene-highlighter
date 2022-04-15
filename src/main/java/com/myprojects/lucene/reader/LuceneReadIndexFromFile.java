package com.myprojects.lucene.reader;

import java.nio.file.Paths;
 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import static com.myprojects.lucene.constants.Constants.INDEX_DIR_PATH_FILES_CONTENT_STORE;

public class LuceneReadIndexFromFile {

    public static void main(String[] args) throws Exception {

        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR_PATH_FILES_CONTENT_STORE));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        QueryParser qp = new QueryParser("contents"/*field name*/, new StandardAnalyzer());
        Query query = qp.parse("apache"/*content to be searched*/);
        TopDocs topDocs = searcher.search(query, 10/*number of documents to return*/);
        System.out.println("Total Results:" + topDocs.totalHits);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println("Path:" + document.get("file_name_path"));
            System.out.println("Score:" + scoreDoc.score);
        }
    }
}