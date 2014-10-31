package net.chuzarski.crowdednews.utils.reddit;


import java.util.Date;

/**
 * This object will encapsulate the data that is a single Reddit post
 */

public class RedditPost {

    private String title;
    private String linkURL;
    private String redditName;
    private String redditId;
    private String linkDomain;
    private boolean isStickied;
    private long createdTimeUTC;

    public static class Builder {

        //highly required
        private final String title;
        private final String linkURL;
        //currently optional
        private String redditName;
        private String linkDomain;

        private String redditId;
        private boolean isStickied;
        private long createdTimeUTC;

        public Builder(String title, String link) {
            this.title = title;
            this.linkURL = link;
        }

        //remaining setters
        public Builder redditName(String name) { this.redditName = name; return this; }
        public Builder redditId(String id) {this.redditId = id; return this;}
        public Builder isStickied(boolean sticky) {this.isStickied = sticky; return this; }
        public Builder timeCreated(long time) {this.createdTimeUTC = time; return this;}
        public Builder linkDomain(String d) { this.linkDomain = d; return this;}

        //builder!
        public RedditPost build() {
            return new RedditPost(this);
        }

    }

    private RedditPost(Builder b) {
        this.setTitle(b.title);
        this.setLinkURL(b.linkURL);
        this.setRedditName(b.redditName);
        this.setRedditId(b.redditId);
        this.setStickied(b.isStickied);
        this.setCreatedTimeUTC(b.createdTimeUTC);
        this.setLinkDomain(b.linkDomain);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getRedditName() {
        return redditName;
    }

    public void setRedditName(String redditName) {
        this.redditName = redditName;
    }

    public String getRedditId() {
        return redditId;
    }

    public void setRedditId(String redditId) {
        this.redditId = redditId;
    }

    public boolean isStickied() {
        return isStickied;
    }

    public void setStickied(boolean isStickied) {
        this.isStickied = isStickied;
    }

    public long getCreatedTimeUTC() {
        return createdTimeUTC;
    }

    public void setCreatedTimeUTC(long createdTimeUTC) {
        this.createdTimeUTC = createdTimeUTC;
    }

    public void setLinkDomain(String d) { this.linkDomain = d;}

    public String getLinkDomain() {return linkDomain;}

    public Date getCreatedDate() {

        //TODO This dirty workaround needs to somehow be replaced, but Reddit is broke.
        //The reddit created_utc timestamp is INVALID
        //(go ahead, test it http://currentmillis.com/
        //it for some reason shaves three digits off of the real time.
        //this for now will return a round-about date
        return new Date(Long.parseLong(Long.toString(createdTimeUTC) + "000"));
    }


}
