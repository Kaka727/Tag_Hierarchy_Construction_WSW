package org.ossean.classification.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndex {
	public static final String INDEX_PATH = "luceneIndex";
	//public static final String INDEX_PATH = "e:/sf_topics.txt";
	public static String idFieldName = "id";
	public static String contentFieldName = "content";

	public IndexWriter createIndexWriter(String indexPath, Analyzer analyzer)
			throws IOException {
		Directory dire = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter iw = new IndexWriter(dire, iwc);
		return iw;
	}

	/**
	 * creating post index
	 * 
	 * @param conn
	 * @param analyzer
	 * @throws IOException
	 */
	public void createIndex(List<String> lines, Analyzer analyzer)
			throws IOException {
		IndexWriter indexWriter = createIndexWriter(INDEX_PATH, analyzer);
		for (int i = 0; i < lines.size(); i++) {
			try {
				Document doc = new Document();
				doc.add(new TextField(idFieldName, i+"",
						Store.YES));
				doc.add(new TextField(contentFieldName, lines.get(i).toLowerCase(),
						Store.YES));
				indexWriter.addDocument(doc);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		indexWriter.commit();
		indexWriter.close();
	}

}
