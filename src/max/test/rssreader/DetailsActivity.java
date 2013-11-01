package max.test.rssreader;


import max.test.rssreader.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DetailsActivity extends Activity{

	TextView tvTitle;
	TextView tvPubDate;
	TextView tvDescription;
	TextView tvLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		Intent i = getIntent();
		
		Integer item_id = Integer.parseInt(i.getStringExtra("id"));
		RSSDatabaseHandler rssHandler = new RSSDatabaseHandler(getApplicationContext());
		final RssItem item = rssHandler.getItem(item_id);
		//Log.d("Details_tag", "site = " + item.getTitle());
		
		tvTitle = (TextView) findViewById(R.id.title);
		tvPubDate = (TextView) findViewById(R.id.details);
		tvDescription = (TextView) findViewById(R.id.description);
		tvLink = (TextView) findViewById(R.id.link);
/*		tvTitle.setWidth(40);
		tvTitle.setGravity(Gravity.CENTER); // attempt at justifying text 
		tvTitle.setMaxLines(1);*/
		tvTitle.setText(item.getTitle());
		tvPubDate.setText(item.getPubDate());
		tvDescription.setText(item.getDescription());
		tvLink.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
        				.parse(item.getLink()));
        		startActivity(intent);
				
			}
		});
		
		
		
		
		

		
		
	}

}
