package net.chuzarski.crowdednews.utils;

public class AppSession {


    //boolean vars to check if these have been set
    private boolean useSource = false;
    private boolean useFilter = false;

    private int newsSource;
    private int newsFilter;

    public void setSource(int s) {

        if(!useSource) {
            //source is now set by user
            useSource = true;
        }

        this.newsSource = s;
    }

    public void setFilter(int f) {

        if(!useFilter) {
            //filter is now set by user
            useFilter = true;
        }

        this.newsFilter = f;
    }

    public int getSource() {
        return newsSource;
    }

    public int getFilter() {
        return newsFilter;
    }

    public boolean useSource() {
        return useSource;
    }

    public boolean useFilter() {
        return useFilter;
    }
}
