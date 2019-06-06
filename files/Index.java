package project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.jsoup.Jsoup;

public class Index
{
	public static void main(String[] args) throws IOException, ParseException
	{
		System.out.print("Input index directory: ");
		Scanner scanner = new Scanner(System.in);
		String path = scanner.nextLine();
	    Analyzer analyzer = new StandardAnalyzer();
	    Directory directory = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    IndexWriter indexWriter = new IndexWriter(directory, config);
		System.out.print("Input data directory: ");
		path = scanner.nextLine();
    	String filename = path + "/" + path;
		int number = 0;
		boolean check = true;
    	File files = new File(filename + number + ".html");
    	while(files.exists())
    	{
    		org.jsoup.nodes.Document data;
    		try
    		{
    			data = Jsoup.parse(files, "UTF-8");
    		}
    		catch(Exception e)
    		{
    			System.out.println("[ERROR] Failed to parse file " + filename + number + ".html");
    			if(!check)
    			{
    				check = true;
	        		number++;
	        		files = new File(filename + number + ".html");
    			}
    			check = false;
    			continue;
    		}
    		String title = "";
    		try
    		{
    			title = data.title();
    		}
    		catch(Exception e)
    		{
    			
    		}
    		String metadata = "";
    		try
    		{
    			metadata = data.select("meta[name=description]").get(0).attr("content");
			}
			catch(Exception e)
			{
				
			}
	    	String header = "";
	    	try
	    	{
	    		for(int i = 1; i <= 6; i++)
	    		{
	    			String input = "";
	    			input += data.select("h" + i).eachText();
	    			if(!input.equals("[]"))
	    			{
	    				if(header != "" && i > 1)
	    				{
	    					header += "\n";
	    				}
	    				header += input.substring(1, input.length() - 1);
	    			}
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		
	    	}
	    	String url = "";
	    	try
	    	{
	    		scanner = new Scanner(files);
	    		scanner.nextLine();
	    		url = scanner.nextLine();
	    		scanner.close();
	    	}
	    	catch(Exception e)
	    	{
	    		
	    	}
	    	String content = "";
	    	try
	    	{
    			content = data.body().text();
	    	}
	    	catch(Exception e)
	    	{
	    		
	    	}
	    	Document doc = new Document();
	    	try
	    	{
	    		doc.add(new TextField("title", title, Field.Store.YES));
	    		doc.add(new TextField("metadata", metadata, Field.Store.YES));
	    		doc.add(new TextField("header", header, Field.Store.YES));
	    		doc.add(new StringField("url", url, Field.Store.YES));
	    		doc.add(new TextField("content", content, Field.Store.YES));
	    		indexWriter.addDocument(doc);
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println("[ERROR] Failed to parse file " + filename + number + ".html");
	    		if(check)
	    		{
	    			check = false;
	    			continue;
	    		}
	    	}
	    	check = true;
    		number++;
    		files = new File(filename + number + ".html");
    	}
	    indexWriter.close();
	    System.out.println("[SYSTEM] Index Built");
	}
}