package webCrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	/**
	 * Hello and Welcome.
	 * 
	 * This project is a basic web crawler which outputs to a mySQL database.
	 * The purpose of this program is to perform the following:
	 * 1) Parse Wikipedia for a list of alcoholic beverages
	 * 2) Add the first paragraph of each entry into the database
	 * 3) Query the database for related terms and group similar alcoholic beverages together (e.g. - if 'Coors light' and 'Budweiser'
	 * 	Wikipedia articles both have the word 'beer' in it, then they are similar.
	 * 4) 
	 * 
	 * TLDR; This program is a search engine specifically for alcoholic beverages.
	 * 
	 * The purpose of writing this program is to increase my knowledge and skills in programming in relation to 'big data' and how to handle the data.
	 * This program will be scalable in form.
	 * 
	 * Enjoy!
	 * 
	 */
	
	//How many pages would you like to grab the first paragraph from?
	private static final int numOfPagesWithParagraph = 5;
	
	//This is a placeholder for the paragraph field in the database
	private static String firstParagraph = "initialCrawl";
	
	//This is the starting point for the webcrawl
	static String nextURL = "http://en.wikipedia.org/wiki/List_of_alcoholic_beverages";
	
	//Start the webcrawl on the first page
	static boolean firstPage = true;
	
	//Used for determining how long the crawler has been working
	static long ms = System.currentTimeMillis();
	
	//Set the number of Pages to Crawl here:
	static int numPagesToCrawl = 2;
	
	//mySQL database information; don't forget to start the database :)
	static String url = "jdbc:mysql://localhost:8889/";
	static String dbName = "database";
	static String driver = "com.mysql.jdbc.Driver";
	static String userName = "root";
	static String password = "root";
	
	
	public static void main(String[] args) {
		// TODO, Make this perform ALL SQL queries we want, including customer queries.
		
		System.out.println("What would you like to do?");
		System.out.println("To crawl the web, press 1");
		System.out.println("To empty the tables in the database, press 2");
		System.out.println("To search the database, press 3");
		
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();
		
		switch (input) {
			case 1:  System.out.println("Parsing: " + nextURL);
					 crawler(nextURL, firstParagraph);
					 
			case 2:  System.out.println("You Pressed 2");
					 System.out.println("WARNING: You are about to delete all of the entries in all of the tables");
					 System.out.println("Are you sure you want to continue? (y/n)");
					 int confirmation = sc.nextInt();
					 if (confirmation == 'y'){
						 deleteTableContents();
					 } else{
						 return;
					 }

			case 3:  System.out.println("You Pressed 3");
					/* Implement the database search
					 * The search will consist of a dictionary API which will allow for results to appear if the user
					 * misspells the word.  It will also allow for similar words to be displayed in the results 
					 */
			 		 return;
			 
			default: System.out.println("You Pressed the wrong key");
		}
	}
	
	private static void deleteTableContents() {
		/* TODO Build out this method to perform the following SQL Query:
		 * 
		 * DELETE FROM table1;
		 * DELETE FROM file;
		 * DELETE FROM list;
		 */
		return;
	}

	public static void crawler(String nextURL, String firstParagraph) {
		URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    try {
	        url = new URL(nextURL);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        while ((line = br.readLine()) != null) {
	        	/*
	        	 * In Wikipedia, the links don't include the domain name.  The following is an example
	        	 * of HTML used in Wikipedia:
	        	 * <a href="/wiki/Bourbon_whiskey" title="Bourbon whiskey">
	        	 */

	        	Pattern pattern = Pattern.compile("<a href=\"/wiki/(.+?)\"");
	        	Matcher matcher = pattern.matcher(line);
	        	
	        	while(matcher.find()){
	        		String theURL = "http://en.wikipedia.org/wiki/" + matcher.group(1);
	        		String theName = matcher.group(1);
	        		//Default table is table1, where the entries go
	        		String table = "table1";
	        			        		
	        		//TODO decode from HTML to ASCII and remove underscores
	        		theName = decodeHTML(theName);
	        		
	        		//TODO If theName contains ".jpg", then push it to table2 and new method insertTable2();
	        		
	        		//New line of code, testing Git
	        		if (theName.contains("File")){
	        			table = "file";
	        		}
	        		if (theName.contains("List_of")){
	        			table = "list";
	        		}
	        		
	        		insert(theURL, theName, table, firstParagraph);
	        	}
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {

	        }
	    }
	    if (firstPage){
	    	long time = System.currentTimeMillis() - ms;
			System.out.println("Parsed in "+time+" ms");
	    	nextPage();
	    }
	}
	
	private static String decodeHTML(String theName) {
		//TODO Decode from HTML to ASCII
		
		char underscore = '_';
		char space = ' ';
		theName = theName.replace(underscore, space);
		return theName;
	}

	//Insert the URL into the database
	public static void insert(String theURL, String theName, String table, String firstParagraph) {

		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			try{
				int val = st.executeUpdate("INSERT into "+ table +" VALUES('"+ theURL +"','"+ theName +"','"+firstParagraph+"')");
				if(val==1)
					conn.close();
			} catch (SQLException e){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Called after a webpage is parsed
	public static void nextPage(){
		ArrayList<String> al = new ArrayList<String>();
		System.out.println("First page successfully parsed");
		firstPage = false;
		int pageNumber = 1;
		String numFields = "0";
		
		//x accounts for the crawl URL number
		for(int x = 0; x < numPagesToCrawl; x++){
			try {
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery("SELECT DISTINCT URL FROM table1 LIMIT "+x+",1");
				
				while (res.next()) {
					al.add(res.getString("URL"));
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			nextURL = al.get(x);
			System.out.println(nextURL);
			firstParagraph = "crawl num "+x; 
			crawler(nextURL, firstParagraph);
			pageNumber = x + 1;
			System.out.println("Page number " + pageNumber + " successfully parsed");
			
			try{
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery("SELECT COUNT(URL) FROM table1;");
				res.next();
				numFields = res.getString("COUNT(URL)");
				System.out.println("Number of records: " + numFields);
				long time = System.currentTimeMillis() - ms;
				System.out.println("Parsed in "+time+" ms");
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// TODO This method is not yet finished, needs testing
	/* It needs to grab the xTh result from the table then grab the 1st paragraph of the URL
	 * 
	 */
	
	public static void getFirstParagraph(String nextURL) {
		ArrayList<String> al = new ArrayList<String>();
		URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    try {
	        url = new URL(nextURL);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        while ((line = br.readLine()) != null) {
	        	for(int x = 0; x < numOfPagesWithParagraph; x++){
		        	try {
						Class.forName(driver).newInstance();
						Connection conn = DriverManager.getConnection(url+dbName,userName,password);
						Statement st = conn.createStatement();
						ResultSet res = st.executeQuery("SELECT DISTINCT URL FROM table1 LIMIT "+x+",1");
						
						while (res.next()) {
							//Add to ArrayList al
							al.add(res.getString("URL"));
						}
						conn.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        	
	        	/*
	        	 *  TODO We're looking for the first paragraph, as a result we'll be searching for the first
	        	 *  iteration of the <p> paragraph tags
	        	 */
	        	
	        	Pattern pattern = Pattern.compile("<p>(.+?)</p>");
	        	Matcher matcher = pattern.matcher(line);
	        	
	        	while(matcher.find()){
	        		String firstParagraph = matcher.group(1);
	        		update(nextURL, firstParagraph);
	        	}	        	
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	        	
	        }
	    }
	}

	private static void update(String nextURL, String firstParagraph) {

		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			int val = st.executeUpdate("UPDATE table1 SET firstParagraph='"+firstParagraph+"' WHERE URL='"+nextURL+"'");
			if(val==1)
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Record Updated Successfully");
	}
}
