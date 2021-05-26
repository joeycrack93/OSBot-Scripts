package com.woodcutting.OSBot;

import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@ScriptManifest(author = "MysteriousCorn", info = "HighAlch", name = "HighAlch", version = 0, logo = "")

public class HighAlch extends Script {

    private String fileAddress = "C:\\Users\\Eric\\OSBot\\Data\\HighAlch";
    private File myFile;
    private File readFile;
    private Scanner reader;
    private FileWriter myWriter;
    private Point currPos;
    private long prevTime;

    public int fileInt;
    public int amountOfFiles;
    public ArrayList<Integer> randIndexes;
    public String itemName;

    private GUI gui;

    boolean record = false; //true if its recording
    boolean play = false; //true if its playing
    boolean shouldCreate = false; //fixes keyevent happening twice


    public void moveMouseInstantly(final int x, final int y) {
        getBot().getMouseEventHandler().generateBotMouseEvent(MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false, MouseEvent.NOBUTTON, true);
    }

    @Override
    public void onStart() {
        log("BOT HAS STARTED /////////////////////");
        gui = new GUI();
        gui.run(this);

        bot.addMouseListener(new BotMouseListener() {
            @Override
            public void checkMouseEvent(MouseEvent mouseEvent) {
                if(mouseEvent.getID() == MouseEvent.MOUSE_CLICKED){
                    if(record) {
                        long temp = System.currentTimeMillis();
                        writeEvent(mouseEvent.getX(), mouseEvent.getY(), true, temp - prevTime);
                        prevTime = temp;
                    }
                }
            }
        });
        bot.addKeyListener(new BotKeyListener() {
            @Override
            public void checkKeyEvent(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == 91) {
                    //91 = '['
                    record = true;
                    shouldCreate = !shouldCreate;
                    if(shouldCreate){
                        createFile();
                        log("Record mode started.");
                        prevTime = System.currentTimeMillis();
                        currPos = getMouse().getPosition();
                        writeEvent(currPos.x, currPos.y,false, 0);
                    }
                }
                else if(keyEvent.getKeyCode() == 93){
                    // 93 = ']'
                    record = false;
                    shouldCreate = !shouldCreate;
                    if(shouldCreate){
                        try{
                            myWriter.close();
                        }
                        catch (IOException e){
                            log("Error while closing writer");
                            e.printStackTrace();
                        }
                        log("Record mode ended.");
                    }
                }
                else if(keyEvent.getKeyCode() == 59){
                    //59 = ';'
                    play = true;
                    log("Play mode started");
                }
                else if(keyEvent.getKeyCode() == 222) {
                    //222 = '"'
                    play = false;
                    log("Play mode ended");
                }
            }
        });

        if(!getTabs().isOpen(Tab.MAGIC)){
            getTabs().open(Tab.MAGIC);
        }
    }

    public int onLoop() throws InterruptedException{
        Mouse mouse = getMouse();
        if (record) {
            if(currPos.x != mouse.getPosition().x || currPos.y != mouse.getPosition().y){
                writeEvent(mouse.getPosition().x, mouse.getPosition().y, false, 0);
                currPos = mouse.getPosition();
                prevTime = System.currentTimeMillis();
            }
        }
        else if (play) {
            if(getInventory().getAmount("Nature rune") == 0 || getInventory().getAmount(itemName) == 0){
                log("ran out of nature runes/item");
                play = false;
            }
            else {
                setReadFile();
                sleep(random(15000));
                if(!getTabs().isOpen(Tab.MAGIC)){
                    getTabs().open(Tab.MAGIC);
                }

                while (reader.hasNextLine()) {
                    if(getInventory().getAmount("Nature rune") == 0 || getInventory().getAmount(itemName) == 0){
                        log("ran out of nature runes/item");
                        play = false;
                        break;
                    }
                    
                    String data = reader.nextLine();
                    if (data.charAt(0) == 'T') {
                        getMouse().click(false);
                    } else if (data.charAt(0) == 'F') {
                        int index = data.indexOf(',');
                        int x = Integer.parseInt(data.substring(1, index));
                        int y = Integer.parseInt(data.substring(index + 1));

                        if(x == -1){
                            moveMouseInstantly(-1,-1);
                            sleep(random(15000));
                        }
                        else {
                            mouse.move(x, y);
                            new ConditionalSleep(1000, 3000) {
                                @Override
                                public boolean condition() {
                                    return mouse.getPosition().x == x && mouse.getPosition().y == y;
                                }
                            }.sleep();
                        }
                    } else {
                        sleep(Long.parseLong(data));
                    }
                }
                reader.close();
            }
        }
        return 10;
    }

    @Override
    public void onExit(){
        log("BOT HAS ENDED //////////////////////");
    }

    @Override
    public void onPaint(Graphics2D g){

    }

    private void createFile(){
        try{
            myFile = new File(fileAddress + "\\mouse" + fileInt + ".txt");

            if(myFile.createNewFile()){ log("File created"); }
            else{ log("File already exists"); }

            myWriter = new FileWriter(myFile);
            fileInt++;
        }
        catch(IOException e) {
            log("An error occurred trying to create file");
            e.printStackTrace();
        }
    }

    private void setReadFile(){
        try {
            if(randIndexes.size() == 0) {
                log("ERROR: Used up all the files");
                play = false;
            }
            else {
                log("Remaining Files: " + randIndexes.size());

                int index = random(0, randIndexes.size() - 1);
                int rand = randIndexes.get(index);

                randIndexes.remove(index);
                readFile = new File(fileAddress + "\\mouse" + rand + ".txt");
                log("File chosen: mouse" + rand);
                reader = new Scanner(readFile);
            }
        }
        catch(FileNotFoundException e){
            log("Something went wrong while setting reader");
            e.printStackTrace();
        }
    }

    private void writeEvent(int x, int y, boolean click, long time){
        try {
            if(click){
                myWriter.write(time + "\n");
                myWriter.write("T" + "\n");
            }
            else{
                myWriter.write("F" + x + "," + y + "\n");
            }
        }
        catch(IOException e){
            log("Error occurred writing to file");
            log(e.getMessage());
        }
    }
}
