package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import statistic.StatisticCalculator;

import org.json.JSONArray;

public class RetrieveTicketsID {

   private static String readAll(Reader rd) throws IOException {
	      var sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try (
         var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    		  ) {
         String jsonText = readAll(rd);
         return new JSONArray(jsonText);
       } finally {
         is.close();
       }
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try (
         var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    		  ) {
         String jsonText = readAll(rd);
         return new JSONObject(jsonText);
       } finally {
         is.close();
       }
   }

  
   public static void main(String[] args) throws IOException, JSONException {
		   
	   var projName ="FALCON";
	   Integer j = 0;
	   Integer i = 0;
	   Integer total = 1;
	   JSONArray issues;
	   List<String> listDate = new ArrayList<>();
	   //Get JSON API for closed bugs w/ AV in the project
	   do {
         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
         j = i + 1000;
         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projName + "%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)"
                + "AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + i.toString() + "&maxResults=" + j.toString();
         JSONObject json = readJsonFromUrl(url);
         issues = json.getJSONArray("issues");
         total = json.getInt("total");
         for (; i < total && i < j; i++) {
            //Iterate through each bug
            Object fields = issues.getJSONObject(i%1000).get("fields");
            var resolutiondate = ((JSONObject) fields).get("resolutiondate").toString().substring(0, 7);
            listDate.add(resolutiondate);
         }  
      } while (i < total);
	  listDate.sort(null);
	  new StatisticCalculator(total, listDate);
   }
}
