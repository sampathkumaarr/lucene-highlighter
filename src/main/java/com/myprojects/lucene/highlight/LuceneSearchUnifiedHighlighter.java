package com.myprojects.lucene.highlight;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.uhighlight.Passage;
import org.apache.lucene.search.uhighlight.PassageFormatter;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.util.QueryBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LuceneSearchUnifiedHighlighter {

    public static void main(String[] args) throws Exception {

        PassageFormatter passageFormatter = new PassageFormatter() {
            @Override
            public Object format(Passage[] passages, String content) {
                StringBuilder stringBuilder = new StringBuilder();
                int previousPassageEnd = 0;
                for (Passage passage : passages) {
                    if (passage.getNumMatches() > 0) {
                        if (previousPassageEnd == 0 || (previousPassageEnd != passage.getStartOffset())) {
                            stringBuilder.append(content, previousPassageEnd, passage.getStartOffset());
                        }
                        stringBuilder.append(content, passage.getStartOffset(), passage.getMatchStarts()[0]);
                        for (int i = 0; i < passage.getNumMatches(); i++) {
                            stringBuilder.append("<hi>");
                            stringBuilder.append(content, passage.getMatchStarts()[i], passage.getMatchEnds()[i]);
                            stringBuilder.append("</hi>");
                            if (i != passage.getNumMatches()-1) {
                                stringBuilder.append(content, passage.getMatchEnds()[i], passage.getMatchStarts()[i+1]);
                            }
                        }
                        stringBuilder.append(content, passage.getMatchEnds()[passage.getNumMatches()-1], passage.getEndOffset());
                    } else {
                        stringBuilder.append(content, passage.getStartOffset(), passage.getEndOffset());
                    }
                    previousPassageEnd = passage.getEndOffset();
                }
                if (previousPassageEnd != content.length()) {
                    stringBuilder.append(content.substring(previousPassageEnd));
                }
                return stringBuilder.toString();
            }
        };

        //analyzer with the default stop words
        Analyzer analyzer = new StandardAnalyzer();

        /*QueryParser queryParser = new QueryParser("", analyzer);
        Query query = queryParser.parse("namenode");*/

        QueryBuilder queryBuilder = new QueryBuilder(analyzer);
        //Query booleanQuery = queryBuilder.createBooleanQuery("", "just AND test");
        //Query phraseQuery = queryBuilder.createPhraseQuery("", "another test");
        PhraseQuery phraseQueryAsProximity = new PhraseQuery.Builder().add(new Term("", "namenode"), 1)
                .add(new Term("", "snapshots"), 1).setSlop(6).build();

        //used to markup highlighted terms found in the best sections of a text
        UnifiedHighlighter unifiedHighlighter = new UnifiedHighlighter(null, analyzer);
        unifiedHighlighter.setFormatter(passageFormatter);

        String content = Files.readString(Paths.get("./src/main/resources/document_content.txt"));
        String highlighted = (String) unifiedHighlighter.highlightWithoutSearcher("", phraseQueryAsProximity, content, 100);
        System.out.println("Highlighted content:"+highlighted);

    }
}