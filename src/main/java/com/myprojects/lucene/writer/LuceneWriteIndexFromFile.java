package com.myprojects.lucene.writer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static com.myprojects.lucene.constants.Constants.INDEX_DIR_PATH_FILES_CONTENT_STORE;
import static com.myprojects.lucene.constants.Constants.INPUT_FILES_FOR_FILES_CONTENT_STORE;

public class LuceneWriteIndexFromFile {

    public static void main(String[] args) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR_PATH_FILES_CONTENT_STORE));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        Path directoryPath = Paths.get(INPUT_FILES_FOR_FILES_CONTENT_STORE);
        if (Files.isDirectory(directoryPath)) {
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes basicFileAttributes) throws IOException {
                        Document document = new Document();
                        document.add(new StringField("file_name_path", filePath.toString(), Field.Store.YES));
                        document.add(new LongPoint("modifiedTime", basicFileAttributes.lastModifiedTime().toMillis()));
                        document.add(new TextField("contents", new String(Files.readAllBytes(filePath)), Store.YES));

                        //Updates a document by first deleting the document(s) containing term and then adding the new document. The delete and then add are atomic as seen by a reader on the same index (flush may happen only after the add).
                        indexWriter.updateDocument(new Term("file_name_path", filePath.toString()), document);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        indexWriter.close();
    }
}
