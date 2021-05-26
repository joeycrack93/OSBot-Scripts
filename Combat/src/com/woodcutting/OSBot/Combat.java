package com.woodcutting.OSBot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.List;

import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.NPCS;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "MysteriousCorn", info = "CombatMage", name = "CombatMage", version = 0, logo = "")

public class Combat extends Script{

    public int bank;
    public String foodType;
    public boolean isReady = false;

    private GUI gui;
    private Area originArea;
    private Timer time;

    private Predicate<NPC> suitableNPC = n ->
            n.hasAction("Attack") &&
            !n.isUnderAttack() &&
            originArea.contains(n) &&
            n.getHealthPercent() > 0 &&
            getMap().canReach(n);


    public void onMessage(Message m){
        if(m.getMessage().contains("You do not have enough ")){
            log("Not enough Runes, Logging out");
            stop(true);
        }
    }

    @Override
    public void onStart(){
        log("BOT HAS STARTED /////////////////////");

        gui = new GUI();
        gui.run(this);

        time = new Timer();
        originArea = new Area(myPlayer().getX() - 10, myPlayer().getY() - 10,myPlayer().getX() + 10, myPlayer().getY() + 10);
    }

    @Override
    public int onLoop() throws InterruptedException{
        if(isReady) {
            eat();
            randomSleep(1);
            List<NPC> npcs = getNpcs().getAll().stream().filter(suitableNPC).collect(Collectors.toList());
            if (!npcs.isEmpty()) {
                npcs.sort(Comparator.<NPC>comparingInt(a -> getMap().realDistance(a)).thenComparingInt(b -> getMap().realDistance(b)));
                if (npcs.get(0).interact("Attack")) {
                    log("Attacking" + npcs.get(0).getName());
                    new ConditionalSleep(50000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            eat();
                            randomSleep(1);
                            return npcs.get(0).getHealthPercent() == 0;
                        }
                    }.sleep();
                    sleep(random(500, 3000));
                }
            }
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
        g.drawString(time.toElapsedString(), 250, 400);
    }

    private void randomSleep(double rate) throws InterruptedException{
        if(random(1,1000) <= rate * 50){
            int rand = random(1000,10000);
            log("Sleeping due to looking away for " + rand/1000 + " sec");
            sleep(rand);
            log("Done sleeping");
        } //looking away
        else if(random(1,1000) < rate * 50){
            mouse.moveOutsideScreen();
            int rand = random(4000,15000);
            log("Sleeping due to mouse outside of screen for " + rand/1000 + "sec");
            sleep(rand);
            log("Done sleeping");
        } //mouse outside of game
    }

    private void eat(){
        if(getSkills().getDynamic(Skill.HITPOINTS) < (getSkills().getStatic(Skill.HITPOINTS) / 2)){
            if(getInventory().contains(foodType)){
                getInventory().getItem(foodType).interact("Eat");
            }
            else if(!myPlayer().isAnimating()){
                log("Out of food, logging out");
                stop(true);
            }
        }
    }

}
