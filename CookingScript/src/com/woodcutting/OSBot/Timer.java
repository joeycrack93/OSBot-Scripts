package com.woodcutting.OSBot;

public class Timer {

    private final long start;

    public Timer() {
        this.start = System.currentTimeMillis();
    }

    public long getElapsed() {
        return System.currentTimeMillis() - this.start;
    }

    public String toElapsedString() {
        long h = getElapsed() / 3600000;
        long m = getElapsed() % 3600000 / 60000;
        long s = getElapsed() % 60000 / 1000;
        return "Time Elapsed: " + h + ":" + m + ":" + s;
    }
}
