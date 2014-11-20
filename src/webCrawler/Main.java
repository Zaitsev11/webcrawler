package webCrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
	 * Please read the README for information on this project.
	 */
	
	//How many pages would you like to grab the first paragraph from?
	private static final int numOfPagesWithParagraph = 3800;
	
	//This is a placeholder for the paragraph field in the database
	private static String firstParagraph = "initialCrawl";
	
	//This is the starting point for the webcrawl
	static String nextURL = "http://en.wikipedia.org/wiki/List_of_alcoholic_beverages";
	
	//Start the webcrawl on the first page
	static boolean firstPage = true;
	
	//Used for determining how long the crawler has been working
	static long ms = System.currentTimeMillis();
	
	//Set the number of Pages to Crawl here:
	static int numPagesToCrawl = 1;
	
	//mySQL database information; don't forget to start the database :)
	static String url = "jdbc:mysql://localhost:8889/";
	static String dbName = "database";
	static String driver = "com.mysql.jdbc.Driver";
	static String userName = "root";
	static String password = "root";
	
	
	public static void main(String[] args) {
		
		System.out.println("What would you like to do?");
		System.out.println("To crawl the web, press 1");
		System.out.println("To empty the tables in the database, press 2");
		System.out.println("To search the database, press 3");
		System.out.println("To get the first paragraph, press 4");
		System.out.println("TO RUN THE TEST, press 5");
		
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();
		
		switch (input) {
			case 1:  System.out.println("Parsing: " + nextURL);
					 crawler(nextURL, firstParagraph);
					 return;
					 
			case 2:  System.out.println("You Pressed 2");
					 System.out.println("WARNING: You are about to delete all of the entries in all of the tables");
					 System.out.println("Are you sure you want to continue? (1 for yes; 2 for no)");
					 int confirmation = sc.nextInt();
					 if (confirmation == 1){
						 deleteTableContents();
						 return;
					 } else {
						 return;
					 }

			case 3:  System.out.println("You Pressed 3");
					/* Implement the database search
					 * The search will consist of a dictionary API which will allow for results to appear if the user
					 * misspells the word.  It will also allow for similar words to be displayed in the results 
					 */
					 System.out.println("Enter a string to search:");
					 
					 //TODO remember that the input is space delimited.  Needs to be fixed.
					 String searchString = sc.next();
					 searchDatabase(searchString);
			 		 return;
			
			case 4:  getFirstParagraph();
					 return;
					 
			case 5:  //Testing code
					 nextURL = "MYURL";
					 firstParagraph = "shōchū";
			String theURL = "theURL";
			String theName = "theName";
			String table = "table";
			//update(nextURL, firstParagraph);
					 insert(theURL, theName, table, firstParagraph);
					 return;
			 
			case 6:  getTaste();	
					 return;
					 
			default: System.out.println("You Pressed the wrong key");
					 return;
		}
	}

	private static void getTaste() {
		// TODO Build out this method
		/* This method will be updating the database and giving value to different fields depending on the words they use
		 * I probably want the word to read from a list in a different table on the mySQL database
		 * The list will consist of 
		 * 
		 */
		
		/* Implement the following SQL query:
		 * SELECT bitterness FROM words LIMIT 0,1;
		 * 
		 * Make this SQL query run for all 5 columns (bitter,sour,sweet, ect.)
		 * 
		 * If there's no result returned in a column, go to the next column
		 */
		
		String tasteType = "bitterness";
		
		//Code to get the word for bitterness
		
		ArrayList<String> al = new ArrayList<String>();
		
		
		//This code to build an arraylist of all of the words within the bitterness column
		
		/*
		 * The following code will get the first tasteType.  I need to build it out so that it gets the other types as well.
		 */
		
		for (int x = 0; x < 40; x++){
			try {
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery("SELECT "+tasteType+" FROM words LIMIT "+x+",1");
				
				while (res.next()) {
					al.add(res.getString(tasteType));
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				String test = al.get(x);
				if (test == ""){
					//I'm using empty strings in the database
					System.out.println("End of line");
					return;
				}
				
				System.out.println(test);

			} catch (IndexOutOfBoundsException e) {
				//This should never be called
				return;
			} 

		}

		
		/*
		 * Build this part out after

		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			//TODO the issue seems to lie with the following line of code
			int val = st.executeUpdate("UPDATE table1 SET "+tasteType+"="+tasteType+"+1 WHERE firstParagraph LIKE '%"+word+"%'");
			if(val==1)
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Record Updated Successfully");
		*/
		
	}

	private static void searchDatabase(String searchString) {
		ArrayList<String> al = new ArrayList<String>();
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery("SELECT DISTINCT * FROM table1 WHERE firstParagraph LIKE '%"+searchString+"%';");

			while (res.next()) {
				al.add(res.getString("name"));
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int x = 0;
		try{
			while(al.get(x) != null){
			String output = al.get(x);
			System.out.println(output);
			x++;
			}
		} catch (IndexOutOfBoundsException e){
			System.out.println("Number of results: "+x);
		}
	}

	private static void deleteTableContents() {
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			st.executeUpdate("DELETE FROM table1;");
			st.executeUpdate("DELETE FROM file;");
			st.executeUpdate("DELETE FROM list;");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Table entries successfully deleted");
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
	        		
	        		//The following decodes theName
	        		theName = decodeHTML(theName);
	        		
	        		//TODO If theName contains ".jpg", then push it to table2 and new method insertTable2();
	        		
	        		if (theName.contains("File")){
	        			table = "file";
	        		}
	        		if (theName.contains("List of")){
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
		// TODO This is decoding correctly when outputting to System.out, however it does not update the mySQL database correctly.  Medium priority.
		try {
			theName = URLDecoder.decode(theName ,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
				//TODO test this
				int val = st.executeUpdate("INSERT into "+ table +" VALUES('"+ theURL +"','"+ theName +"','"+firstParagraph+"','0','0','0','0','0')");
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
	
	public static void getFirstParagraph() {
		URL url2;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    for(int x = 0; x < numOfPagesWithParagraph; x++){	        
        	try {
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery("SELECT DISTINCT URL FROM table1 LIMIT "+x+",1");
				res.next();
				nextURL = res.getString("URL");
				System.out.println("The URL: " + nextURL);
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
		        try {
		        	//Change to 'while', instead of 'if' to get more than the first paragraph
		            if (is != null) is.close();
		        } catch (IOException ioe) {
		        	
		        }
		    }
        	
        	//Now that we have theUrl, let's get the paragraph!
		    try {
		        url2 = new URL(nextURL);
		        is = url2.openStream();  // throws an IOException
		        br = new BufferedReader(new InputStreamReader(is));  	
		    while ((line = br.readLine()) != null) {
		    	
		        try {
			        Pattern pattern = Pattern.compile("<p>(.+?)</p>");
		        	Matcher matcher = pattern.matcher(line);
		        	
		        	//TODO check this code for possible decode/SQL issues.  Reference GIT bug for more info
		        	if(matcher.find()){
		        		String firstParagraph = matcher.group(1);
		        		firstParagraph = removeHTMLFormatting(firstParagraph);
		        		System.out.println("The first paragraph: " + firstParagraph);
		        		update(nextURL, firstParagraph);
		        		break;
		        	}
		        	
		        } catch(Exception e){
		        } 
		    }

	    } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	        	
	        }
	    }
	    }
	}

	private static String removeHTMLFormatting(String firstParagraph) {
		firstParagraph = firstParagraph. replaceAll("\\<.*?>","");
		return firstParagraph;
	}

	private static void update(String nextURL, String firstParagraph) {

		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			//TODO the issue seems to lie with the following line of code
			int val = st.executeUpdate("UPDATE table1 SET firstParagraph='"+firstParagraph+"' WHERE URL='"+nextURL+"'");
			if(val==1)
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Record Updated Successfully");
	}
}
