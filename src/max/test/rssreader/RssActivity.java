package max.test.rssreader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import max.test.rssreader.R;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RssActivity extends Activity {

	ListView mListView;
	ProgressDialog ShowProgress;
	RSSDatabaseHandler dbHandler;

	public static String TAG_ID = "id";

	private ArrayList<RssItem> mPostList = new ArrayList<RssItem>();
	private ArrayList<RssItem> rssList = new ArrayList<RssItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mListView = (ListView) findViewById(R.id.listView1);

		dbHandler = new RSSDatabaseHandler(this);
		/*
		 * SQLiteDatabase sqdb = dbHandler.getWritableDatabase(); sqdb.close();
		 */
		// RssItem item1 = new RssItem("testTtitle", "testLink",
		// "testDescription", "testPubdate");
		// dbHandler.addFeed(item1);
		// dbHandler.showFields();
		// dbHandler.deleteItem(item1);
		// dbHandler.close();

		ShowProgress = ProgressDialog.show(RssActivity.this, "",
				"Loading. Please wait...", true);
		new loadingTask().execute("http://feeds.nytimes.com/nyt/rss/World");

		/*
		 * Log.d("TAG!!!!", "RSSLIST " + rssList); Log.d("TAG@@@@@", "ID!!! " +
		 * rssList.get(0).getId() + " " + rssList.get(0).getTitle());
		 * Log.d("TAG@@@@@", "ID!!! " + rssList.get(24).getId() + " " +
		 * rssList.get(24).getTitle());
		 */

		/*
		 * ArrayList<RssItem> rssList = dbHandler.getAllRSS();
		 * dbHandler.close(); mListView.setAdapter(new
		 * EfficientAdapter(ParseActivity.this, rssList));
		 */

		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String sqlite_id = rssList.get(position).getId().toString();
				Intent in = new Intent(getApplicationContext(),
						DetailsActivity.class);
				in.putExtra(TAG_ID, sqlite_id);
				startActivity(in);

				// Log.d("TAG_BUTTON", "----" +
				// rssList.get(position).getId().toString());

			}
		});

	}

	class loadingTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {

			SAXHelper sh = null;
			try {
				sh = new SAXHelper(urls[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			sh.parseContent("");

			return "";

		}

		protected void onPostExecute(String s) {
			// Log.d("TAG", "mPostList" + mPostList.get(2).getDescription());
			rssList = dbHandler.getAllRSS();
			dbHandler.close();
			mListView
					.setAdapter(new EfficientAdapter(RssActivity.this, rssList));
			ShowProgress.dismiss();

		}
	}

	class SAXHelper {
		public HashMap<String, String> userList = new HashMap<String, String>();
		private URL url2;

		public SAXHelper(String url1) throws MalformedURLException {
			this.url2 = new URL(url1);
		}

		public RSSHandler parseContent(String parseContent) {
			RSSHandler df = new RSSHandler();
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(df);
				xr.parse(new InputSource(url2.openStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return df;
		}
	}

	class RSSHandler extends DefaultHandler {

		private RssItem currentPost = new RssItem();
		StringBuffer chars = new StringBuffer();

		private String imageUrl;

		private boolean parsingTitle;
		private boolean parsingDescription;
		private boolean parsingLink;
		private boolean parsingDate;
		private boolean parsingImage;

		public void startElement(String uri, String localName, String qName,
				Attributes atts) {
			// Log.d("TAG!!!!!", "CURRENT POST " + currentPost);
			chars = new StringBuffer();
			if ("item".equals(qName)) {

			}
			if ("title".equals(qName)) {
				parsingTitle = true;
			}
			if ("description".equals(qName)) {
				parsingDescription = true;
			}
			if ("link".equals(qName)) {
				parsingLink = true;
			}
			if ("pubDate".equals(qName)) {
				parsingDate = true;
			}
			if ("media:content".equals(qName)) {
				parsingImage = true;
				imageUrl = atts.getValue("url");
				// Log.v("log_tag", "Image url is: " + imageUrl);
				currentPost.setImage(imageUrl);
				// Log.v("LOG_TAG", "CURRENT POST " + currentPost);
			}

		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if ("item".equals(qName)) {

			}
			if ("title".equals(qName)) {
				parsingTitle = false;
			}
			if ("description".equals(qName)) {
				parsingDescription = false;
			}
			if ("link".equals(qName)) {
				parsingLink = false;
			}
			if ("pubDate".equals(qName)) {
				parsingDate = false;
			}
			if ("media:content".equals(qName)) {
				parsingImage = false;
			}

			if (localName.equalsIgnoreCase("item")) {
				// mPostList.add(currentPost);
				// Log.d("TAG!!!!!", "CURRENT POST " + currentPost);
				dbHandler.addFeed(currentPost);
				currentPost = new RssItem();
				dbHandler.close();

			}

		}

		public void characters(char ch[], int start, int length) {
			chars.append(new String(ch, start, length));
			if (parsingTitle) {
				currentPost.setTitle(new String(ch, start, length));
				parsingTitle = false;
			}
			if (parsingDescription) {
				currentPost.setDescription(new String(ch, start, length));
				parsingDescription = false;

			}
			if (parsingLink) {
				currentPost.setLink(new String(ch, start, length));
				parsingLink = false;

			}
			if (parsingDate) {
				currentPost.setPubDate(new String(ch, start, length));
				parsingLink = false;

			}
			// Log.d("TAG", "PARSER!! ----- " + currentPost.getTitle() +
			// currentPost.getDescription() + currentPost.getLink());

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
