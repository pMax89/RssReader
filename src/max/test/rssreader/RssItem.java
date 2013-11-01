package max.test.rssreader;

import android.util.Log;

public class RssItem {
	private Integer _id;
	private String title;
	private String link;
	private String description;
	private String image;
	private String pubDate;

	public RssItem(){
		
	}

	public RssItem(String title, String link, String description, String pubDate){
		this.title = title;
		this.link = link;
		this.pubDate = pubDate;
		this.description = description;
	}
	
	public void setId(Integer id){
		this._id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public Integer getId(){
		return this._id;
	}

	public void setTitle(String title) {
		this.title = title;
		//Log.i("This is the title:", title);
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
		//Log.i("This is the image:", image);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
		//Log.i("This is the link:", link);
	}
	
	public void setDescription(String description) {
		this.description = description;
		//Log.i("This is the description:", description);
	}

	public String getDescription() {
		return description;
	}
	
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
		//Log.i("This is the pubDate:", pubDate);
	}

	public String getPubDate() {
		return pubDate;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
}
