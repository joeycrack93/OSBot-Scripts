package com.woodcutting.OSBot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "MysteriousCorn", info = "Drop Items", name = "Drop Items", version = 0, logo = "")

public class MouseTest extends Script{

    private String fileAddress = "C:\\Users\\Eric\\OSBot\\Data\\FishDrops";
    private File myFile;
    private File readFile;
    private Scanner reader;
    private FileWriter myWriter;
    public int fileInt;
    public int amountOfFiles;
    public ArrayList<Integer> randIndexes;

    private GUI gui;

    String mode;
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

        countFiles();

        bot.addMouseListener(new BotMouseListener() {
            @Override
            public void checkMouseEvent(MouseEvent mouseEvent) {
                if(mouseEvent.getID() == MouseEvent.MOUSE_CLICKED){
                    if(record) {
                        writeEvent(mouseEvent.getX(), mouseEvent.getY(), true);
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
    }

    public int onLoop() throws InterruptedException {
        Mouse mouse = getMouse();
        if (mode == "Mining"){
            if (record) {

            }
            else if (play && getInventory().isFull()) {

            }
        }
        else {
            if (record) {
                writeEvent(mouse.getPosition().x, mouse.getPosition().y, false);
            }
            else if (play && getInventory().isFull()) {
                moveMouseInstantly(0, random(400));
                if (random(0, 100) <= 85) {
                    setReadFile();
                    getKeyboard().pressKey(KeyEvent.VK_SHIFT);
                    if (mode == "Woodcutting") {
                        getKeyboard().pressKey(KeyEvent.VK_SHIFT);
                        getMouse().move(getInventory().getMouseDestination(0));
                        getMouse().click(false);
                        new ConditionalSleep(2500, 3000) {
                            @Override
                            public boolean condition() {
                                return getInventory().getItems().length != 28;
                            }
                        }.sleep();

                        getKeyboard().pressKey(KeyEvent.VK_SHIFT);
                        getMouse().move(getInventory().getMouseDestination(1));
                        getMouse().click(false);
                        new ConditionalSleep(2500, 3000) {
                            @Override
                            public boolean condition() {
                                return getInventory().getItems().length != 27;
                            }
                        }.sleep();
                    } //drops first two items which are missed in the dropped items files
                    while (reader.hasNextLine()) {
                        String data = reader.nextLine();
                        //format "0,00"
                        int index = data.indexOf(',');
                        if (data.charAt(0) == 'T') {
                            getKeyboard().pressKey(KeyEvent.VK_SHIFT);
                            getMouse().click(false);
                        } else {
                            int x = Integer.parseInt(data.substring(0, index));
                            int y = Integer.parseInt(data.substring(index + 1));
                            mouse.move(x, y);
                        }
                    }
                    getKeyboard().releaseKey(KeyEvent.VK_SHIFT);
                    reader.close();
                } else {
                    log("File chosen: Dropping by dropAll function");
                    getInventory().dropAllExcept("Feather", "Barbarian rod");
                    new ConditionalSleep(2500, 3000) {
                        @Override
                        public boolean condition() {
                            return !getInventory().isFull();
                        }
                    }.sleep();
                }
                mouse.move(0, random(400));
                moveMouseInstantly(-1, -1);
            }
        }
        return 3;
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

    private void writeEvent(int x, int y, boolean click){
        try {
            String isClick = "";
            if(click){isClick = "T";}

            myWriter.write(isClick + x + "," + y + "\n");
        }
        catch(IOException e){
            log("Error occurred writing to file");
            log(e.getMessage());
        }
    }

    private void countFiles(){
        File fl = new File("C:\\Users\\Eric\\OSBot\\Data\\FishDrops\\");
        amountOfFiles = fl.listFiles().length;
        fileInt = amountOfFiles + 1;

        randIndexes = new ArrayList<>(amountOfFiles);
        for(int i = 0; i < amountOfFiles; i++){
            randIndexes.add(i + 1);
        }
    }
}

