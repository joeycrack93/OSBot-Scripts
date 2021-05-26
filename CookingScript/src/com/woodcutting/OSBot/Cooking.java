package com.woodcutting.OSBot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "MysteriousCorn", info = "Cooking", name = "Cooking", version = 0, logo = "")

public class Cooking extends Script{

    public String fishType; //raw fish
    public String cookedFish;
    public String logType;
    public int[] itemsToKeep;
    public boolean isReady = false;

    private double xpGained;
    private Area areaToCookIn;
    private ArrayList<Position> positions;
    private Timer time;

    private GUI gui = new GUI();

    public void onMessage(Message m){
        if(m.getMessage().contains("You catch some shrimps")){
            xpGained += 10;
        }
        else if(m.getMessage().contains("You catch some anchovies")){
            xpGained += 40;
        }
        else if(m.getMessage().contains("You catch a trout")){
            xpGained += 50;
        }
        else if(m.getMessage().contains("You catch a salmon")){
            xpGained += 70;
        }
    }

    @Override
    public void onStart(){
        log("BOT HAS STARTED /////////////////////");
        gui.run(this);

        areaToCookIn = new Area(3161, 3488, 3161, 3490);

        itemsToKeep = new int[2];

        positions = new ArrayList<Position>();
        positions.add(new Position(3161, 3488, 0));
        positions.add(new Position(3161, 3489, 0));
        positions.add(new Position(3161 , 3490, 0));
        positions.add(new Position(3160, 3488, 0));
        positions.add(new Position(3160, 3489, 0));
        positions.add(new Position(3160 , 3490, 0));

        xpGained = 0;
        time = new Timer();
    }

    @Override
    public int onLoop() throws InterruptedException{
        if(isReady) {
            RS2Object fire = getObjects().closest("Fire");
            if(getInventory().getAmount(fishType) == 0 || fire == null || !areaToCookIn.contains(fire.getX(), fire.getY())) {
                getBank().open();
                new ConditionalSleep(2000){
                    public boolean condition(){
                        return getBank().isOpen();
                    }
                }.sleep();

                log(itemsToKeep[0]);
                getBank().depositAllExcept(itemsToKeep);
                sleep(random(500,1500));

                if (!getInventory().contains("Tinderbox")) {
                    getBank().withdraw("Tinderbox", 1);
                }

                getBank().withdraw(logType, 1);
                new ConditionalSleep(5000){
                    public boolean condition(){
                        return getInventory().getAmount(logType) != 0;
                    }
                }.sleep();

                getBank().withdraw(fishType, 26);
                new ConditionalSleep(5000){
                    public boolean condition(){
                        return getInventory().getAmount(fishType) != 0;
                    }
                }.sleep();
                getBank().close();
                getWalking().walk(positions.get(random(0, positions.size() - 1)));
                sleep(2000);

                //light fire
                getInventory().interact("Use", "Tinderbox");
                sleep(200);
                getMouse().move(getInventory().getMouseDestination(2));
                sleep(200);
                getMouse().click(false);
            }

            if(!myPlayer().isAnimating()) {
                getInventory().interact("Use", fishType);
                fire.interact("Use");
                getKeyboard().pressKey(KeyEvent.VK_SPACE);
            }
        }
        randomSleep();
        return random(500,800);
    }

    @Override
    public void onExit(){
        log("BOT HAS ENDED //////////////////////");
    }

    @Override
    public void onPaint(Graphics2D g){
        g.setColor(Color.BLACK);
        g.drawString("XP gained: " + xpGained, 250, 385);
        g.drawString(time.toElapsedString(), 250, 400);
    }

    private void randomSleep() throws  InterruptedException{
        if (random(1, 1000) <= 100) {
            int rand = random(10000, 30000);
            log("Sleeping due to looking away for " + rand / 1000 + " sec");
            sleep(rand);
            log("Done sleeping");
        } //looking away
        else if (random(1, 1000) < 75) {
            mouse.moveOutsideScreen();
            int rand = random(10000, 30000);
            log("Sleeping due to mouse outside of screen for " + rand / 1000 + "sec");
            sleep(rand);
            log("Done sleeping");
        } //mouse outside of game
    }

}