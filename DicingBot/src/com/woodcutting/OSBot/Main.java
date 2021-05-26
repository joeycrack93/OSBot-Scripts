package com.woodcutting.OSBot;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
	// write your code here

        int win = 55;
        int count = 0;
        int count2 = 0;

        Random rand22 = new Random();
        for(int i = 0; i < 1000; i++){
            count = 0;
            count2 = 0;
            rand22.setSeed(i);
            for(int x = 0; x < 10; x++){
                if(rand22.nextInt(100) <= win){
                    count++;
                    count2++;
                }
            }
            for(int x = 10; x < 50; x++){
                if(rand22.nextInt(100) <= win){
                    count++;
                }
            }
            if(count ==32 && count2 == 6) {
                rand22.setSeed(i);
                System.out.println("Seed: " + i + " Wins: " + count);
                for(int j = 0; j < 50;j ++){
                    System.out.print(rand22.nextInt(100) + " ");
                }
                System.out.println();
                System.out.println();
            }
        }

        //32 = 67
        //681 = 71

    }
}
