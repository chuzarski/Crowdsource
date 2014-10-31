package net.chuzarski.crowdednews.utils.reddit;

public class RedditException extends Throwable {

    private int redditErrorCode;

    public RedditException(int errorCode) {
        this.redditErrorCode = errorCode;
    }

    public void setRedditErrorCode(int redditErrorCode) {
        this.redditErrorCode = redditErrorCode;
    }

    public int getRedditErrorCode() {
        return redditErrorCode;
    }
}
