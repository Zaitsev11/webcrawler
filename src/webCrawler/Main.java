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
	
	//This is a placeholder for the paragraph field in the database
	private static String firstParagraph = "none";
	
	//This is the starting point for the webcrawl
	static String nextURL = "http://en.wikipedia.org/wiki/List_of_alcoholic_beverages";
	
	//Start the webcrawl on the first page
	static boolean firstPage = true;
	
	//Used for determining how long the crawler has been working
	static long ms = System.currentTimeMillis();
	
	//Set the number of Pages to Crawl here:
	static int numPagesToCrawl = 100;
	
	//Initialize the following string
	static String tasteType;
	
	//Initialize the following string
	static String countString;

	
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
					 
					 //TODO remember that the input is space delimited.  Needs to be fixed. LOW priority, might be removed
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
		ArrayList<String> al = new ArrayList<String>();
		int num = 0;

		for (int intTasteType = 0; intTasteType < 5; intTasteType++){
			switch(intTasteType){
				case 0: tasteType = "bitterness";
						break;
						
				case 1: tasteType = "saltiness";
						break;
						
				case 2: tasteType = "sourness";
						break;
				
				case 3: tasteType = "sweetness";
						break;
						
				case 4: tasteType = "umami";
						break;
			}

			for (int x = 0; x >= 0; x++){
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
					//TODO Consider adding a kill on exception command
				}
				try{
					String test = al.get(num);
					if (test == ""){
						//I'm using empty strings in the database
						num++;
						break;
					}
					try {
						Class.forName(driver).newInstance();
						Connection conn = DriverManager.getConnection(url+dbName,userName,password);
						Statement st = conn.createStatement();
						int val = st.executeUpdate("UPDATE table1 SET "+tasteType+"="+tasteType+"+1 WHERE firstParagraph LIKE '%"+al.get(num)+"%'");
						if(val==1)
						conn.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					num++;
	
				} catch (IndexOutOfBoundsException e) {
					//This should never be called
					e.printStackTrace();
					return;
				} 
			}
		}
		//System.out.println(al.size()+" total words to use");
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
	        		
	        		/*
	        		 * TODO If theName contains ".jpg", then push it to table2 and new method insertTable2();
	        		 * This will be used to pair the result with a picture
	        		 */
	        		
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
				//TODO This is the cause of the issue when attempting to crawl the web:
				/* The issue does not specifically lie with the "INSERT IGNORE" part of the query
				 * 
				 */
				long time = System.currentTimeMillis();
				
				//TODO check if the previous line plays well with the SQL database
				st.executeUpdate("INSERT IGNORE into "+ table +" VALUES('"+ theURL +"','"+ theName +"','"+firstParagraph+"','0','0','0','0','0',"+time+")");
				conn.close();
				//System.out.println(val);
				//if(val==1)
					//conn.close();
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
				ResultSet res = st.executeQuery("SELECT DISTINCT URL FROM table1 ORDER BY ms LIMIT "+x+",1");
				
				while (res.next()) {
					al.add(res.getString("URL"));
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			nextURL = al.get(x);
			System.out.println(nextURL);
			//firstParagraph = "crawl num "+x; 
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
	    
	    try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery("SELECT COUNT(*) FROM table1 WHERE firstParagraph='none';");
			res.next();
			countString = res.getString("COUNT(*)");
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
	    
	    int count = Integer.parseInt(countString);
	    
	    for(int x = 0; x <= count; x++){	        
        	try {
				Class.forName(driver).newInstance();
				Connection conn = DriverManager.getConnection(url+dbName,userName,password);
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery("SELECT DISTINCT URL FROM table1 WHERE firstParagraph='none' ORDER BY ms LIMIT "+x+",1");
				res.next();
				nextURL = res.getString("URL");
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				//CONFIRMED. TODO This is where the error for this bug is:
				/*
				 * http://stackoverflow.com/questions/23494021/java-sql-sqlexception-illegal-operation-on-empty-result-set-dor-select-stateme
				 */
				System.out.println("ERROR IS HERE!");
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
		        	
		        	if(matcher.find()){
		        		String firstParagraph = matcher.group(1);
		        		firstParagraph = removeHTMLFormatting(firstParagraph);
		        		update(nextURL, firstParagraph);
		        		System.out.println("Paragraph number: "+x+" added out of: "+count);
		        		break;
		        	}
		        	
		        } catch(Exception e){
		        	e.printStackTrace();
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
		firstParagraph = firstParagraph.replaceAll("\\<.*?>","");
		//TODO test the following code,which removes everything between parenthesis
		firstParagraph = firstParagraph.replaceAll("\\(.*?\\)","");
		return firstParagraph;
	}

	private static void update(String nextURL, String firstParagraph) {

		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			Statement st = conn.createStatement();
			//TODO the encoding issue seems to lie with the following line of code
			int val = st.executeUpdate("UPDATE table1 SET firstParagraph='"+firstParagraph+"' WHERE URL='"+nextURL+"'");
			//TODO remove this next line in order to prevent this error from ocurring
			if(val==1)
			conn.close();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("ERROR HERE!!!~~~");
		}
	}
}
