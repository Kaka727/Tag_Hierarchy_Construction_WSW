package org.ossean.classification.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneSearcher {
	public static String idFieldName = "id";
	public static String contentFieldName = "content";

	IndexReader reader;

	public LuceneSearcher() {
		Directory dire;
		try {
			dire = FSDirectory.open(Paths.get(LuceneIndex.INDEX_PATH));
			reader = DirectoryReader.open(dire);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Integer> search(List<String> words){
		List<Integer> rt = new ArrayList<Integer>();
		BooleanQuery booleanQuery = new BooleanQuery();
		TermQuery tq;

		for (String word : words) {
			booleanQuery.add(new TermQuery(new Term(
					LuceneIndex.contentFieldName, word.toLowerCase())),
					BooleanClause.Occur.MUST);
		}

		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs topDocs = null;
		try {
			topDocs = searcher.search(booleanQuery, 100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < topDocs.totalHits; i++) {
			int docId = scoreDocs[i].doc;
			Document document = null;
			try {
				document = searcher.doc(docId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rt.add(Integer.parseInt(document.get(LuceneIndex.idFieldName)));
			System.out.println("记录:" + document.get(LuceneIndex.idFieldName)
					+ " " + document.get(LuceneIndex.contentFieldName));
		}
		
		return rt;
	}
}
