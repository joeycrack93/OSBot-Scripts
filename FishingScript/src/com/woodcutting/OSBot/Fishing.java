package com.woodcutting.OSBot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "MysteriousCorn", info = "Fishing", name = "Fishing", version = 0, logo = "")

public class Fishing extends Script{

    public int bankChoice = 0;
    public String fishType = ""; //action performed
    public int[] bankKeep; //items to keep while banking
    public boolean isReady = false;

    private double xpGained;
    private Area originArea;
    private Timer time;

    private GUI gui = new GUI();
    public Object lock = new Object();

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

    private Predicate<NPC> suitableFishingSpot = n ->
            n.hasAction(fishType) &&
            originArea.contains(n) &&
            getMap().canReach(n);

    @Override
    public void onStart(){
        log("BOT HAS STARTED /////////////////////");
        gui.run(this);
        originArea = new Area(myPlayer().getX() - 5, myPlayer().getY() - 5,myPlayer().getX() + 5, myPlayer().getY() + 5);

        xpGained = 0;
        time = new Timer();
    }

    @Override
    public int onLoop() throws InterruptedException{
        if(isReady) {
            if(fishType == "Lure" && getInventory().getAmount("Feather") == 0){
                log("Out of feathers");
                stop(true);
            }
            NPC fishingSpot = getFishingSpot();

            if (getInventory().isFull()) {
                log("Inv is full");
                bank();
            }
            else if (fishingSpot != null && !myPlayer().isAnimating()) {//fishing
                if (!fishingSpot.interact(fishType)) {
                    log("fails to fish");
                }
                sleep(1000);
                new ConditionalSleep(90000) {
                    @Override
                    public boolean condition() {
                        return !myPlayer().isAnimating();
                    }
                }.sleep();
            } //fishes

            else if(myPlayer().isAnimating()){
                getCamera().movePitch(67);
            }

            randomSleep();
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
        else if (random(1, 1000) < 100) {
            log("Checking amount of xp");
            getTabs().open(Tab.SKILLS);
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return getTabs().isOpen(Tab.SKILLS);
                }
            }.sleep();
            getMouse().move(random(680, 727), random(273, 293));
            sleep(random(2000, 5000));
            getTabs().open(Tab.INVENTORY);
        } //checks amount of xp

    }

    private NPC getFishingSpot(){
        if(random(0,100) < 20){
            List<NPC> fishingSpots = getNpcs().getAll().stream().filter(suitableFishingSpot).collect(Collectors.toList());
            fishingSpots.sort(Comparator.<NPC>comparingInt(a -> getMap().realDistance(a)).thenComparingInt(b -> getMap().realDistance(b)));
            return fishingSpots.get(random(0, fishingSpots.size()-1));
        }
        else{
            return getNpcs().closest("Fishing spot");
        }
    }

    private void dropItems(){
        for (int i = 1; i < 29; i++) {
            getMouse().move(getInventory().getMouseDestination(i));
            getKeyboard().pressKey(KeyEvent.VK_SHIFT);
            getMouse().click(false);
            getKeyboard().releaseKey(KeyEvent.VK_SHIFT);
        }
    }

    private void bank() throws InterruptedException{
        if (bankChoice == 0) { //drop items
            dropItems();
        } else if (bankChoice == 1 && !Banks.VARROCK_WEST.contains(myPlayer().getPosition())) {
            getWalking().webWalk(Banks.VARROCK_WEST);
        } else if (bankChoice == 2 && !Banks.VARROCK_EAST.contains(myPlayer().getPosition())) {
            getWalking().webWalk(Banks.VARROCK_EAST);
        } else if (bankChoice == 3 && !Banks.DRAYNOR.contains(myPlayer().getPosition())) {
            getWalking().webWalk(Banks.DRAYNOR);
        } else if(bankChoice == 4 && !Banks.EDGEVILLE.contains(myPlayer().getPosition())){
            getWalking().webWalk(Banks.EDGEVILLE);
        }else {
            if (!getBank().isOpen()) { // open the bank
                getBank().open();
                new ConditionalSleep(2500, 3000) {
                    @Override
                    public boolean condition() {
                        return getBank().isOpen();
                    }
                }.sleep();
            } else { //deposit the stuff
                getBank().depositAllExcept(bankKeep);
                sleep(1000);
                getWalking().webWalk(originArea);
                new ConditionalSleep((50000)) {
                    @Override
                    public boolean condition() {
                        return originArea.contains(myPlayer().getPosition());
                    }
                }.sleep();
            }
        }
    }
}
