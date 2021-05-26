package com.woodcutting.OSBot;

public class Timer {

    private long start;

    public Timer() {
        start = -1;
    }

    public void start(){
        start = System.currentTimeMillis();
    }

    public long getElapsed() {
        if(start != -1) {
            return System.currentTimeMillis() - start;
        }
        return 0;
    }

    public String toElapsedString() {
        long h = getElapsed() / 3600000;
        long m = getElapsed() % 3600000 / 60000;
        long s = getElapsed() % 60000 / 1000;
        return "Time Elapsed: " + h + ":" + m + ":" + s;
    }

    public String perHourConverter(long xpGained){
        if(start != -1) {
            long ans = (xpGained * 3600000) / getElapsed();
            return Long.toString(ans);
        }
        return "0";
    }

}
