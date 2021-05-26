package com.woodcutting.OSBot;

public class Timer {

    private final long start;
    private long stop;

    public Timer() {
        start = System.currentTimeMillis();
        stop = 10 * 3600000;
    }

    public long getElapsed() {
        return System.currentTimeMillis() - start;
    }

    public String toElapsedString() {
        long h = getElapsed() / 3600000;
        long m = getElapsed() % 3600000 / 60000;
        long s = getElapsed() % 60000 / 1000;
        return "Time Elapsed: " + h + ":" + m + ":" + s;
    }

    public void setEndTime(long h, long m, long s){
        stop = stop + (h * 3600000);
        stop = stop + (m * 60000);
        stop = stop + (s * 1000);
    }

    public long getTimeUntilEnd(){
        return stop - getElapsed() ;
    }

    public boolean isOver(){
        return getElapsed() > stop;
    }
}
