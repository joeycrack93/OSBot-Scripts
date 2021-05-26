package com.woodcutting.OSBot;

import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.input.mouse.MouseDestination;
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

@ScriptManifest(author = "MysteriousCorn", info = "Theiving", name = "Theiving", version = 0, logo = "")

public class Theiving extends Script {

    private String fileAddress = "C:\\Users\\Eric\\OSBot\\Data\\Theiving";
    private File myFile;
    private File readFile;
    private Scanner reader;
    private FileWriter myWriter;
    private Point currPos;
    private int pitch;
    private int yaw;
    private long prevTime;

    public int fileInt;
    public int amountOfFiles;
    public ArrayList<Integer> randIndexes;

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

        if(!getTabs().isOpen(Tab.INVENTORY)){
            getTabs().open(Tab.INVENTORY);
        }

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

                        writeCamera(getCamera().getPitchAngle(), getCamera().getYawAngle());
                        pitch = getCamera().getPitchAngle();
                        yaw = getCamera().getYawAngle();

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
    }

    public int onLoop() throws InterruptedException{
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
        else if (play) {
            setReadFile();

            while (reader.hasNextLine()) {
                String data = reader.nextLine();

                if(parameterChecks(reader)) {

                    if (data.charAt(0) == 'T') {
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
            reader.close();
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
        File fl = new File("C:\\Users\\Eric\\OSBot\\Data\\Theiving\\");
        amountOfFiles = fl.listFiles().length;
        fileInt = amountOfFiles + 1;

        randIndexes = new ArrayList<>(amountOfFiles);
        for(int i = 0; i < amountOfFiles; i++){
            randIndexes.add(i + 1);
        }
    }

    private boolean parameterChecks(Scanner reader) throws InterruptedException{

        if(getInventory().contains("Coin pouch") && getInventory().getItem("Coin pouch").getAmount() == 28 && random(100) <= 20){
            getInventory().getItem("Coin pouch").interact("Open-all");
            reader.close();
            setReadFile();

            return false;
        }

        if((myPlayer().getPosition().getX() != 2672 && myPlayer().getPosition().getX() != 2671) || myPlayer().getPosition().getY() != 3316){
            getWalking().webWalk(new Area(2672, 3316, 2671, 3316));
            new ConditionalSleep(15000){
                @Override
                public boolean condition(){
                    return (myPlayer().getPosition().getX() == 2672 || myPlayer().getPosition().getX() == 2671) && myPlayer().getPosition().getY() == 3316;
                }
            }.sleep();
            getCamera().movePitch(22);
            getCamera().moveYaw(89);
            sleep(500);
            reader.close();
            setReadFile();

            return false;
        }

        if(getSkills().getDynamic(Skill.HITPOINTS) < 10){
            while(getSkills().getDynamic(Skill.HITPOINTS) < getSkills().getStatic(Skill.HITPOINTS) - 10){
                if (getInventory().contains("Tuna")) {
                    getInventory().getItem("Tuna").interact("Eat");
                    sleep(random(100,500));
                } else {
                    log("Out of food, turning off play mode");
                    play = false;
                    while(reader.hasNextLine()){
                        reader.nextLine();
                    }
                    break;
                }
            }
            reader.close();
            setReadFile();

            return false;
        }
        return true;
    }
}
