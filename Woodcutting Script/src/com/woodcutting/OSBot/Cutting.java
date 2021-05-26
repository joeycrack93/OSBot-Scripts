package com.woodcutting.OSBot;

import java.awt.*;
import java.awt.event.KeyEvent;

import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;


@ScriptManifest(author = "MysteriousCorn", info = "Woodcutting", name = "Woodcutting", version = 0, logo = "")

public class Cutting extends Script{

    public int bankChoice;
    public String treeType = "";
    public String logType;
    public double xpPerLog;
    public boolean isReady = false;

    private long logCount; //total amount of logs cut down
    private Area originArea;
    public Timer time;

    private GUI gui = new GUI();

    public void onMessage(Message m){
        if(m.getMessage().contains("You get some ")){
            logCount++;
        }
    }

    @Override
    public void onStart(){
        log("BOT HAS STARTED /////////////////////");
        gui.run(this);

        originArea = new Area(myPlayer().getX() - 1, myPlayer().getY() - 1,myPlayer().getX() + 1, myPlayer().getY() + 1);

        logCount = 0;
        time = new Timer();

    }

    @Override
    public int onLoop() throws InterruptedException{
        if(isReady) {
            if(time.isOver()){
                stop(true);
            }

            RS2Object tree = getObjects().closest(treeType);
            Mouse mouse = getMouse();

            if (getInventory().isFull()) {
                log("Inv is full");
                if (bankChoice == 0) { //drop items
                    for (int i = 1; i < 29; i++) {
                        getMouse().move(getInventory().getMouseDestination(i));
                        getKeyboard().pressKey(KeyEvent.VK_SHIFT);
                        getMouse().click(false);
                        getKeyboard().releaseKey(KeyEvent.VK_SHIFT);
                    }
                } else if (bankChoice == 1 && !Banks.VARROCK_WEST.contains(myPlayer().getPosition())) {
                    getWalking().webWalk(Banks.VARROCK_WEST);
                } else if (bankChoice == 2 && !Banks.VARROCK_EAST.contains(myPlayer().getPosition())) {
                    getWalking().webWalk(Banks.VARROCK_EAST);
                } else {
                    if (!getBank().isOpen()) { // open the bank
                        getBank().open();
                        new ConditionalSleep(2500, 3000) {
                            @Override
                            public boolean condition() {
                                return getBank().isOpen();
                            }
                        }.sleep();
                    } else { //deposit the stuff
                        getBank().depositAll(logType);
                        new ConditionalSleep(2500, 3000) {
                            @Override
                            public boolean condition() {
                                return getInventory().getAmount(logType) == 0;
                            }
                        }.sleep();
                        getWalking().webWalk(originArea);
                        new ConditionalSleep((50000)) {
                            @Override
                            public boolean condition() {
                                return originArea.contains(myPlayer().getPosition());
                            }
                        }.sleep();
                    }
                }
            } // full inventory
            else if (tree != null && !myPlayer().isAnimating()) {//chopping down of the tree
                if (tree.hover()) {
                    mouse.move(mouse.getPosition().x + random(-10, 10), mouse.getPosition().y + random(-10, 10));
                    mouse.click(false);
                }
                sleep(random(1000, 5000));
            } // chops down logs

            //random events
            if (random(1, 1000) <= 25) {
                int rand = random(10000, 30000);
                log("Sleeping due to looking away for " + rand / 1000 + " sec");
                sleep(rand);
                log("Done sleeping");
            } //looking away
            else if (random(1, 1000) < 25) {
                mouse.moveOutsideScreen();
                int rand = random(10000, 30000);
                log("Sleeping due to mouse outside of screen for " + rand / 1000 + "sec");
                sleep(rand);
                log("Done sleeping");
            } //mouse outside of game
        }
        return random(500,800);
    }

    @Override
    public void onExit(){
        log("BOT HAS ENDED //////////////////////");
    }

    @Override
    public void onPaint(Graphics2D g){
        g.setColor(Color.BLACK);
        g.drawString(logType + " chopped: " + logCount, 250, 370);
        g.drawString("XP gained: " + (xpPerLog * logCount), 250, 385);
        g.drawString(time.toElapsedString(), 250, 400);
    }
}
