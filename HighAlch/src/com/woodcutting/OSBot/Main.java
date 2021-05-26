package com.woodcutting.OSBot;

import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    int fileNum;

	    Scanner reader = new Scanner(System.in);
        System.out.println("Enter amount of files: ");
        fileNum = reader.nextInt();

        File read;
        int time = 0;
        int clickAmount = 0;

        for(int i = 0; i < fileNum; i++){
            read = new File("C:\\Users\\Eric\\OSBot\\Data\\HighAlch\\mouse" + (i + 1) + ".txt");
            try {
                reader = new Scanner(read);
                while(reader.hasNext()){
                    String data = reader.nextLine();
                    if(data.charAt(0) == 'T'){
                        clickAmount++;
                    }
                    if(data.charAt(0) != 'T' && data.charAt(0) != 'F'){
                        time = time + Integer.parseInt(data);
                    }
                }
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }

        System.out.println("Amount of time H:" + time/3600000 + " M:" + time%3600000/60000 + " S:" + time%60000/1000);
        System.out.println("Amount of clicks: " + clickAmount /2 );
    }
}
