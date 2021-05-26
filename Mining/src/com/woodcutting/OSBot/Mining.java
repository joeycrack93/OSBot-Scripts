package com.woodcutting.OSBot;

import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Skill;
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

@ScriptManifest(author = "MysteriousCorn", info = "Mining", name = "Mining", version = 0, logo = "")

public class Mining extends Script{

        private String fileAddressMining = "C:\\Users\\Eric\\OSBot\\Data\\Mining";
        private Scanner dropReader;
        private Scanner mineReader;
        private FileWriter myWriter;
        private Point currPos;
        private int pitch;
        private int yaw;
        private long prevTime;

        private long startXP;
        private Timer time;

        public int fileInt;     //name of file it will write to next
        public int amountOfFilesDrop;
        public int amountOfFilesMining;
        public ArrayList<Integer> randIndexes;  //indexes for drop files
        public ArrayList<Integer> randMining;  //indexes for mining files

        boolean record = false; //true if its recording
        boolean play = false; //true if its playing
        boolean shouldCreate = false; //fixes keyevent happening twice

        public void moveMouseInstantly(final int x, final int y) {
            getBot().getMouseEventHandler().generateBotMouseEvent(MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false, MouseEvent.NOBUTTON, true);
        }

        @Override
        public void onStart() {
            log("BOT HAS STARTED /////////////////////");
            countFiles();
            time = new Timer();

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
                        shouldCreate = !shouldCreate;
                        if(shouldCreate){
                            createFile();
                            log("Record mode started.");

                            writeCamera(getCamera().getPitchAngle(), getCamera().getY());
                            pitch = getCamera().getPitchAngle();
                            yaw = getCamera().getYawAngle();

                            prevTime = System.currentTimeMillis();
                            currPos = getMouse().getPosition();
                            writeEvent(currPos.x, currPos.y, false, 0);
                            record = true;
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
                        startXP = getSkills().getExperience(Skill.MINING);
                        time.start();
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
            if (record) {
                if(currPos.x != mouse.getPosition().x || currPos.y != mouse.getPosition().y){
                    writeEvent(mouse.getPosition().x, mouse.getPosition().y, false, 0);
                    currPos = mouse.getPosition();
                    prevTime = System.currentTimeMillis();
                }
                if(getCamera().getPitchAngle() != pitch || getCamera().getYawAngle() != yaw){
                    writeCamera(getCamera().getPitchAngle(), getCamera().getYawAngle());
                    pitch = getCamera().getPitchAngle();
                    yaw = getCamera().getYawAngle();
                }
            }
            else if (play && !getInventory().isFull()) {
                setReadFile(true);

                while (mineReader.hasNextLine() && play) {
                    String data = mineReader.nextLine();

                    if(isInPosition(mineReader)) {
                        if (data.charAt(0) == 'T') {
                            try {
                                if (getMouse().getEntitiesOnCursor().get(0).getDefinition().getModifiedModelColors() == null) {
                                    new ConditionalSleep(3000) {
                                        @Override
                                        public boolean condition() {
                                            return getMouse().getEntitiesOnCursor().get(0).getDefinition().getModifiedModelColors() != null;
                                        }
                                    }.sleep();
                                    sleep(random(500));
                                }
                            }
                            catch(NullPointerException e){log("NullPointer");}
                            catch(IndexOutOfBoundsException e){log("Out of bounds at: " + getMouse().getPosition().x + "," + getMouse().getPosition().y);}
                            getMouse().click(false);
                        } else if (data.charAt(0) == 'F') {
                            int index = data.indexOf(',');
                            int x = Integer.parseInt(data.substring(1, index));
                            int y = Integer.parseInt(data.substring(index + 1));

                            if (x == -1) {
                                moveMouseInstantly(-1, -1);
                                sleep(random(15000));
                            } else {
                                mouse.move(x, y);
                                new ConditionalSleep(1000, 3000) {
                                    @Override
                                    public boolean condition() {
                                        return mouse.getPosition().x == x && mouse.getPosition().y == y;
                                    }
                                }.sleep();
                            }
                        } else if (data.charAt(0) == 'C') {
                            int index = data.indexOf(',');
                            getCamera().movePitch(Integer.parseInt(data.substring(1, index)));
                            getCamera().moveYaw(Integer.parseInt(data.substring(index + 1)));
                            sleep(500);
                        } else {
                            sleep(Long.parseLong(data));
                        }
                    }
                }
                mineReader.close();
            }
            else if (play && getInventory().isFull()) {
                dropItems();
            }
        return 2;
    }

        @Override
        public void onExit(){
            log("BOT HAS ENDED //////////////////////");
        }

        @Override
        public void onPaint(Graphics2D g){
            long currXP = getSkills().getExperience(Skill.MINING);
            g.setColor(Color.BLACK);
            g.drawString("Ores mined: " + (currXP - startXP)/35, 250, 370);
            g.drawString("XP gained:" + (currXP - startXP), 250, 385);
            g.drawString(time.toElapsedString(), 250, 400);
            g.drawString("XP/hr: " + time.perHourConverter(currXP - startXP), 250, 415);
        }

        private void createFile(){
            try{
                File myFile = new File(fileAddressMining + "\\mouse" + fileInt + ".txt");

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

        private void setReadFile(boolean mining){
            try {
                if(randIndexes.size() == 0 || randMining.size() == 0) {
                    log("ERROR: Used up all the files");
                    stop(false);
                }
                else {
                    File readFile;
                    if(mining){
                        int index = random(0, randMining.size() - 1);
                        int rand = randMining.get(index);
                        randMining.remove(index);
                        readFile = new File(fileAddressMining + "\\mouse" + rand + ".txt");
                        log("Mining File: " + rand + ", " + (randMining.size()-1) + " files remaining.");
                        mineReader = new Scanner(readFile);
                    }
                    else {
                        int index = random(0, randIndexes.size() - 1);
                        int rand = randIndexes.get(index);
                        randIndexes.remove(index);
                        String fileAddressDrop = "C:\\Users\\Eric\\OSBot\\Data\\FishDrops";
                        readFile = new File(fileAddressDrop + "\\mouse" + rand + ".txt");
                        log("Drop File: " + rand + ", " + (randIndexes.size()-1) + " files remaining.");
                        dropReader = new Scanner(readFile);
                    }
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

        private void writeCamera(int pitch, int yaw){
            try{
                myWriter.write("C" + pitch + "," + yaw + "\n");
            }
            catch(IOException e){
                log("Error occurred writing in camera");
                log(e.getMessage());
            }
        }

        private void countFiles(){
            File fl = new File("C:\\Users\\Eric\\OSBot\\Data\\FishDrops\\");
            amountOfFilesDrop = fl.listFiles().length;

            randIndexes = new ArrayList<>(amountOfFilesDrop);
            for(int i = 0; i < amountOfFilesDrop; i++){
                randIndexes.add(i + 1);
            }

            fl = new File("C:\\Users\\Eric\\OSBot\\Data\\Mining\\");
            amountOfFilesMining = fl.listFiles().length;
            fileInt = amountOfFilesMining + 1;

            randMining = new ArrayList<>(amountOfFilesMining);
            for(int i = 0; i < amountOfFilesMining; i++){
                randMining.add(i + 1);
            }
        }

        private void dropItems(){
            if (random(0, 100) <= 85) {
                setReadFile(false);
                getKeyboard().pressKey(KeyEvent.VK_SHIFT);
                /* drops the firsts two items that are missed in drop files*/
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

                while (dropReader.hasNextLine()) {
                    String data = dropReader.nextLine();
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
                dropReader.close();
            } else {
                log("File chosen: Dropping by dropAll function");
                getInventory().dropAllExcept("Feather", "Barbarian rod");
                new ConditionalSleep(2500, 30000) {
                    @Override
                    public boolean condition() {
                        return !getInventory().isFull();
                    }
                }.sleep();
            }
        }

        private boolean isInPosition(Scanner reader) throws InterruptedException {
            if(getInventory().isFull()){
                dropItems();
            }

            if (myPlayer().getPosition().getX() != 3295  || myPlayer().getPosition().getY() != 3310) {
                getGroundItems().closest("Iron ore").interact("Take");
                new ConditionalSleep(15000) {
                    @Override
                    public boolean condition() {
                        return (myPlayer().getPosition().getX() == 3295) && myPlayer().getPosition().getY() == 3310;
                    }
                }.sleep();
                sleep(500);
                reader.close();
                setReadFile(true);
                return false;
            }
            return true;
        }

}
