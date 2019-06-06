package project;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class Search
{
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String search(@RequestParam(required = false, defaultValue = "") String query) throws IOException, ParseException
	{
		System.out.println("[SYSTEM] Packet Received: " + query);
		Analyzer analyzer = new StandardAnalyzer();
		Directory directory = FSDirectory.open(Paths.get("index/"));
	    DirectoryReader directoryReader = DirectoryReader.open(directory);
	    IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
	    String[] fields = {"title", "metadata", "header", "url", "content"};
	    Map<String, Float> boosts = new HashMap<>();
	    boosts.put(fields[0], 1.0f);
	    boosts.put(fields[1], 0.5f);
	    boosts.put(fields[2], 0.5f);
	    boosts.put(fields[3], 0.25f);
	    boosts.put(fields[4], 0.25f);
	    MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
    	if(query.equals(""))
    	{
    		return "";
    	}
    	Query parsed = parser.parse(query);
    	ScoreDoc[] score = indexSearcher.search(parsed, 10).scoreDocs;
    	String returning = "";
    	for(int i = 0; i < score.length; i++)
    	{
    		Document result = indexSearcher.doc(score[i].doc);
    		returning += "*****[Rank]*****[" + (i + 1) + "] Score: " + score[i].score + "*****[/Rank]*****";
    		String title = result.get("title");
    		if(!title.equals(""))
    		{
    			returning += "*****[Title]*****" + title + "*****[/Title]*****";
    		}
    		String metadata = result.get("metadata");
    		if(!metadata.equals(""))
    		{
    			if(metadata.length() > 1000)
    			{
    				metadata = metadata.substring(0, 1000);
    			}
    			returning += "*****[Metadata]*****" + metadata + "*****[/Metadata]*****";
    		}
    		String header = result.get("header");
    		if(!header.equals(""))
    		{
    			if(header.length() > 1000)
    			{
    				header = header.substring(0, 1000);
    			}
    			returning += "*****[Header]*****" + header + "*****[/Header]*****";
    		}
    		String url = result.get("url");
    		if(!url.equals(""))
    		{
    			returning += "*****[URL]*****" + url + "*****[/URL]*****";
    		}
    		String content = result.get("content");
    		if(!content.equals(""))
    		{
    			if(content.length() > 1000)
    			{
    				content = content.substring(0, 1000);
    			}
    			returning += "*****[Content]*****" + content + "*****[/Content]*****";
    		}
    	}
	    directoryReader.close();
	    directory.close();
	    analyzer.close();
	    return returning;
	}
}