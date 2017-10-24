/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.examples.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.http.HttpStatus;
import org.apache.http.Header;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawler extends WebCrawler {

	private static final Pattern FILTERS = Pattern.compile(
		      ".*(\\.(css|js|json|mid|mp2|mp3|mp4|css|rss|wav|avi|mov|mpeg|ram|m4v" +
		      "|rm|smil|wmv|swf|wma|zip|rar|gz|php|iso|ico))$");
	private static final Pattern PATTERNS = Pattern.compile(".*(\\.(pdf"+"|msword|doc|docx"+"|bmp|gif|jpg|png|jpe?g|tiff?))$");


	  HashMap < String, ArrayList > hmallurl = new HashMap();
	  HashMap < String, String > hmallouturl = new HashMap();
	  HashMap < String, ArrayList > hmvisit = new HashMap();	
	  HashMap <String, Integer> hmfetch = new HashMap();

	int okurl=0,nokurl=0,totalUrl=0,uniqueok=0,uniquenok=0,uniqueurl=0;
	int file1kb =0, file10kb=0, file100kb=0,file1mb=0,filebmb=0;
	int twoxx=0,threexx=0,fourxx=0,fivexx=0;
	int totalout=0,outurlok=0,outuniqueok=0,outurlnok=0,outuniquenok=0;
	
	int visit=0;
	  private static File storage_folder;

	    private static String domain = "http://www.nydailynews.com/";
	    public static void configure( String folder)
	    {
	    	storage_folder = new File(folder);
	    	    if (!storage_folder.exists()) {
	    	    	storage_folder.mkdirs();
	    	    }
	    }
    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        String type = "o";
        String com = "c";
      if( href.contains("http://www.nydailynews.com")||href.contains("https://www.nydailynews.com"))
      {
    	  okurl++;
    	  if (!(hmallurl.containsKey(href) && hmallurl.get(href).equals("OK")))
    	  { uniqueok++;
    	  	com = "ok";
    	  }
    	  ArrayList < String > al = new ArrayList < String > ();
    	  al.add("OK");
    	  al.add(referringPage.getContentType());
          hmallurl.put(href, al);
          type = "OK";

      }
      else
      {
    	  nokurl++;
    	  if (!(hmallurl.containsKey(href) && hmallurl.get(href).equals("N_OK")))
              uniquenok++;
    	  type = "N_OK";
    	  ArrayList < String > al = new ArrayList < String > ();
    	  al.add("N_OK");
    	  al.add(referringPage.getContentType());
          hmallurl.put(href, al);
      }

      	uniqueurl = uniqueok+uniquenok;
      	return !FILTERS.matcher(href).matches() && com.equals("ok");
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
    
        String url = page.getWebURL().getURL();
        String url2 = url.replaceAll(",", "_");
       String filesize = String.valueOf(page.getContentData().length);
        String outlinks = String.valueOf(page.getParseData().getOutgoingUrls().size());
        String extension = url2.substring(url2.lastIndexOf('.'));
        String hashedName;
        if (page.getParseData() instanceof HtmlParseData) {
        	 hashedName = UUID.randomUUID() + ".html";
        }else
        {
         hashedName = UUID.randomUUID() + extension;
        }
        File fname=new File(storage_folder.getAbsolutePath() + "/" + hashedName);
        try{
        	Files.write(page.getContentData(), fname);
        }catch (IOException iox) {
            logger.error("Failed to write file: " + iox);
          }
        for(WebURL tempurl: page.getParseData().getOutgoingUrls())
        {
        	totalout++;
        	 if (tempurl.getURL().contains("http://www.nydailynews.com/") || tempurl.getURL().contains("https://www.nydailynews.com/")) {
                outurlok++;
                 if (!(hmallouturl.containsKey(tempurl.getURL()) && hmallouturl.get(tempurl.getURL()).equals("OK")))
                     outuniqueok++;
                 hmallouturl.put(tempurl.getURL(), "OK");
             } else {
                 outurlnok++;

                 if (!(hmallouturl.containsKey(tempurl.getURL()) && hmallouturl.get(tempurl.getURL()).equals("N_OK")))
                     outuniquenok++;
                 hmallouturl.put(tempurl.getURL(), "N_OK");

             }

        }

        Integer fsize = page.getContentData().length;
        if (fsize < 1000)
            file1kb++;
        else if (fsize < 10000)
            file10kb++;
        else if (fsize < 100000)
            file100kb++;
        else if (fsize < 1000000)
            file1mb++;
        else
            filebmb++;
        
//        Content_Type.put(contentType, Content_Type.containsKey(contentType) ? Content_Type.get(contentType) + 1 : 1);

        ArrayList < String > al = new ArrayList < String > ();
        al.add(filesize);
        al.add(outlinks);
        al.add(page.getContentType());


        hmvisit.put(url2, al);
  
    }


  @Override
  protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
	  String url = String.valueOf(webUrl).replaceAll(",", "_");
  	
	  		if(statusCode >= 200 && statusCode<300)
            	 twoxx++;
             else if(statusCode >= 300 && statusCode<400)
            	 threexx++;
             else if(statusCode >= 400 && statusCode<500)
            	 fourxx++;
             else if(statusCode >= 500 && statusCode<300)
            	 fivexx++;
             
             hmfetch.put(url, statusCode);
             
  	

  }

    @Override
    public void onBeforeExit()
    {
    	FileWriter fileWritter = null;
          try {
        	  fileWritter = new FileWriter(storage_folder+"/csv/urls_New_York_Daily_News1.csv",true);

          BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
          for (String url: hmallurl.keySet()) {

            bufferWritter.write(url+","+hmallurl.get(url).get(0)+","+hmallurl.get(url).get(1)+"\n");
//
          }
          bufferWritter.close();
          }catch (IOException iox) {

        	  logger.error("Failed to write file: " + iox);
          }
          
          // visit csv 
          FileWriter fileWritter1 = null;
          try {
        	  fileWritter1 = new FileWriter(storage_folder+"/csv/visit_New_York_Daily_News1.csv",true);

          BufferedWriter bufferWritter = new BufferedWriter(fileWritter1);
          for (String url: hmvisit.keySet()) {

            bufferWritter.write(url+","+hmvisit.get(url).get(0)+","+hmvisit.get(url).get(1)+","+hmvisit.get(url).get(2)+"\n");
//
          }
          bufferWritter.close();
          }catch (IOException iox) {

        	  logger.error("Failed to write file: " + iox);
          }
          // fetch csv
          FileWriter fileWritter2 = null;
          try {
        	  fileWritter2 = new FileWriter(storage_folder+"/csv/fetch_New_York_Daily_News1.csv",true);

          BufferedWriter bufferWritter = new BufferedWriter(fileWritter2);
          for (String url: hmfetch.keySet()) {

            bufferWritter.write(url+","+hmfetch.get(url)+"\n");
          }
          bufferWritter.close();
          }catch (IOException iox) {

        	  logger.error("Failed to write file: " + iox);
          }
          
          System.out.println("\n\n fetch total: "+hmfetch.size());
          System.out.println("visit total: "+hmvisit.size());
          System.out.println("url total: "+hmallurl.size());
          System.out.println("total url extracted: "+ totalout);
          System.out.println("total unique url extracted: "+(outuniqueok+outuniquenok));
          System.out.println("unique url within news website: "+(outuniqueok));
          System.out.println("unique url outside news website: "+(outuniquenok));
          System.out.println("File size no: less than 1kb: "+file1kb+" |less than 10kb: "+file10kb+" |less than 100kb: "+file100kb+" |less than 1MB: "+file1mb+" |greater than 1mb: "+filebmb);
          System.out.println("Status code count: "+"2xx: "+twoxx+" |3xx: "+threexx+" |4xx: "+fourxx+" |5xx: "+fivexx);
          
          
          System.out.println("\n all URLs within: " + okurl + " URLs outside: " + nokurl + " unique ok: " + uniqueok + " uniquenok " + uniquenok + " unique " + uniqueurl);
       
          System.out.println("\n inside visited outgoing URLs within: " + outurlok + " URLs outside: " + outurlnok + " unique ok: " + outuniqueok + " uniquenok " + outuniquenok + " unique total  " + totalout);
          
    }

}
