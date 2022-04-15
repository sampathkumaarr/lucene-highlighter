package com.myprojects.lucene.writer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static com.myprojects.lucene.constants.Constants.INDEX_DIR_PATH_EMPLOYEE_DATABASE;

public class LuceneWriteIndex {

	public static void main(String[] args) throws Exception {

		Directory directory = FSDirectory.open(Paths.get(INDEX_DIR_PATH_EMPLOYEE_DATABASE));
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

		List<Document> documents = new ArrayList<>();

		Document document1 = createLuceneDocument(1, "James", "Cottan", "Victoria House", "Bristol", "AB10 7AR");
		documents.add(document1);

		Document document2 = createLuceneDocument(2, "Patrick", "Jane", "Rose Cottage", "Birmingham", "AB12 8ER");
		documents.add(document2);
		indexWriter.deleteAll();

		indexWriter.addDocuments(documents);
		indexWriter.commit();
		indexWriter.close();
	}

	private static Document createLuceneDocument(int employeeId, String firstName, String lastName, String address1, String address2, String postcode) {
		Document document = new Document();
		document.add(new StringField("employeeId", String.valueOf(employeeId), Field.Store.YES));
		document.add(new TextField("firstName", firstName, Field.Store.YES));
		document.add(new TextField("lastName", lastName, Field.Store.YES));
		document.add(new TextField("address1", address1, Field.Store.YES));
		document.add(new TextField("address2", address2, Field.Store.YES));
		document.add(new TextField("postcode", postcode, Field.Store.YES));
		return document;
	}
}