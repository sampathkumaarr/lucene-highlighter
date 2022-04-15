package com.myprojects.lucene.reader;

import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static com.myprojects.lucene.constants.Constants.INDEX_DIR_PATH_EMPLOYEE_DATABASE;

public class LuceneReadIndex {

	public static void main(String[] args) throws Exception {

		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR_PATH_EMPLOYEE_DATABASE));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher indexSearcher = new IndexSearcher(reader);

		QueryParser queryParser = new QueryParser("employeeId", new StandardAnalyzer());
		Query queryEmployeeId = queryParser.parse("1");
		TopDocs topDocs = indexSearcher.search(queryEmployeeId, 10);
		System.out.println("*********************************");
		System.out.println("Number of document hits:"+topDocs.totalHits);
		for(ScoreDoc scoreDoc: topDocs.scoreDocs) {
			System.out.println("scoreDoc:" + scoreDoc.toString());
			System.out.println("Actual document:" + indexSearcher.doc(scoreDoc.doc).toString());
			System.out.println("---------------");
		}
		System.out.println("*********************************");

	}
}
