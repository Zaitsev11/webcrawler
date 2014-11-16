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
	
	
	
	
	private static final int numOfPagesWithParagraph = 5;
	private static String firstParagraph = "initialCrawl";
	//Starting point for the webcrawl.
	static String nextURL = "http://en.wikipedia.org/wiki/List_of_alcoholic_beverages";
	static boolean firstPage = true;
	static long ms = System.currentTimeMillis();
	//Set the number of Pages to Crawl here:
	static int numPagesToCrawl = 2;
	
	static String url = "jdbc:mysql://localhost:8889/";
	static String dbName = "database";
	static String driver = "com.mysql.jdbc.Driver";
	static String userName = "root";
	static String password = "root";
	
	
	
	public static void main(String[] args) {
		// TODO After every page parsed, remove duplicate entries in the SQL database
		// TODO, Make this perform ALL SQL queries we want, including customer queries.
		
		
		//getFirstParagraph(nextURL);
		System.out.println("What would you like to do?");
		System.out.println("To crawl the web, press 1");
		System.out.println("To delete the database, press 2");
		//System.out.println("To write a custom SQL query, press 3");
		//System.out.println("To see how many results are in the database, press 4");
		
		
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();
		
		switch (input) {
			case 1:  System.out.println("You Pressed 1");
					 System.out.println("Parsing: " + nextURL);
					 crawler(nextURL, firstParagraph);
					 
			case 2:  System.out.println("You Pressed 2");
					 return;
					 
			default: System.out.println("You Pressed the wrong key");
		}
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
	            
	        	//view-source:http://en.wikipedia.org/wiki/List_of_alcoholic_beverages
	        		        	
	        	Pattern pattern = Pattern.compile("<a href=\"/wiki/(.+?)\"");
	        	//Pattern pattern = Pattern.compile("<a href=\"http:(.+?)\"");
	        	Matcher matcher = pattern.matcher(line);
	        	
	        	while(matcher.find()){
	        		//String theURL = "http:" + matcher.group(1);
	        		String theURL = "http://en.wikipedia.org/wiki/" + matcher.group(1);
	        		String theName = matcher.group(1);
	        		//Default table is table1, where the entries go
	        		String table = "table1";
	        		//TODO Need to decode theName from HTML char to ASCII before passing to method insert();
	        		//TODO Remove the underscore character from theName "_"
	        		//TODO If theName contains ".jpg", then push it to table2 and new method insertTable2();
	        		//TODO Figure out what to do if theName contains "List_of"
	        		//TODO Figure out what to do if theName contains "Category"
	        		//TODO Figure out what to do if theName contains "File"
	        		if (theName.contains("File")){
	        			table = "file";
	        		}
	        		if (theName.contains("List_of")){
	        			table = "list";
	        		}
	        		
	        		insert(theURL, theName, table, firstParagraph);
	        	}
	        	//System.out.println(matcher.group(1));
	        	
	        	
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	    if (firstPage){
	    	//return;
	    	long time = System.currentTimeMillis() - ms;
			System.out.println("Parsed in "+time+" ms");
	    	nextPage();
	    }
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
					//Add to ArrayList al
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
			
			//SELECT COUNT(URL) FROM table1;
			
		}
		
	}
	
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
	        	//view-source:http://en.wikipedia.org/wiki/List_of_alcoholic_beverages
	        	
	        	// TODO I need to finish this method
	        	/* It needs to grab the xTh result from the table then grab the 1st paragraph of the URL
	        	 * 
	        	 */
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
	        	
	        	Pattern pattern = Pattern.compile("<p>(.+?)</p>");
	        	//Pattern pattern = Pattern.compile("<a href=\"http:(.+?)\"");
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
	            // nothing to see here
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
