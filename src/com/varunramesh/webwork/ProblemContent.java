package com.varunramesh.webwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

public class ProblemContent {

	public static List<Problem> ITEMS = new ArrayList<Problem>();
	
	public static Map<String, Problem> ITEM_MAP = new HashMap<String, Problem>();
	
	public static Connection c;
	
	public static Activity a;
	
	public static String u;
	
	public static WebView webview;
	
	public static ProblemListActivity listactivity;
	
	
	
	public static class Pair
	{
		String first;
		String second;
		
		@Override
		public String toString() {
			return first + " " + second;
		}
	}
	
	
	public static ArrayList<Pair> DATA = new ArrayList<Pair>();
	
	public static boolean complete;
	
	public static void addItem(Problem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
	
	public static String readRawTextFile(Context ctx, int resId)
    {
         InputStream inputStream = ctx.getResources().openRawResource(resId);

            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
             String line;
             StringBuilder text = new StringBuilder();

             try {
               while (( line = buffreader.readLine()) != null) {
                   text.append(line);
                   text.append('\n');
                 }
           } catch (IOException e) {
               return null;
           }
             return text.toString();
    }
	
	
	public static class Problem {
		public String id;
		public String name;
		public String url;
		
		
		public String attempts;
		public String remaining;
		public String score;
		public String worth;

		public Problem(String id, String name, String url, String attempts, String remaining, String worth, String score)
		{
			this.id = id;
			this.name = name;
			
			this.attempts = attempts;
			this.remaining = remaining;
			this.worth = worth;
			this.score = score;
			
			this.url = url;
		}

		@Override
		public String toString() {
			
			if(this.remaining.compareTo("unlimited") == 0)
			{
				return name + " - " + this.score + ", " + attempts + " Attempts";
			}
			else
			{
				return name + " - " + this.score + ", " + attempts + " out of " + remaining + " Attempts";
			}
		}
	}
}
