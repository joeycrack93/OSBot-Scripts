package com.woodcutting.OSBot;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;
import java.util.Random;

@ScriptManifest(author = "MysteriousCorn", info = "Dicing", name = "Dicing", version = 0, logo = "")

public class Dicing extends Script {

    private long startingCash;
    private long currMoney;
    private long moneyOffered;
    private int iter;
    private int iter2 = 20;
    private Position coords;
    private Random rand;
    public int winNumber;

    boolean gaveCoins = false;
    boolean traded = false;

    private String playerName;

    private GUI gui;

    public void onMessage(Message m){
        if(m.getTypeId() == 101 && !traded){
            playerName = m.getUsername();
            traded = true;
        }
    }

    @Override
    public void onStart() {
        log("BOT HAS STARTED /////////////////////");
        gui = new GUI();
        gui.run(this);

        startingCash = getInventory().getAmount("Coins");
        currMoney = startingCash;
        iter = 0;

        rand = new Random();
        coords = myPlayer().getPosition();
    }

    public int onLoop() throws InterruptedException{
        iter++;
        if(currMoney > 200000){
            spamMessage();

            if(traded){
                log("Interaction started ---------------------");

                sleep(1000);

                iter2 = 40;
                if(isInRange(getPlayers().closest(playerName).getPosition())) {
                    getPlayers().closest(playerName).interact("Trade with");
                    new ConditionalSleep(150000) {
                        @Override
                        public boolean condition() {
                            if(!isInStart()){
                                if(getTrade().isCurrentlyTrading()){
                                    getTrade().declineTrade();
                                    log("Got moved, declined trade");
                                }
                                else {
                                    getMouse().move(260, 185);
                                    getMouse().click(false);
                                    log("Moving away, moving back");
                                    return true;
                                }
                            }
                            if(iter2 == 40) {
                                log("Waiting for trade window to pop up");
                                iter2 = 0;
                            }
                            iter2++;
                            return getTrade().isCurrentlyTrading();
                        }
                    }.sleep();//waiting to be traded
                }
            }

            if (getTrade().isCurrentlyTrading() && traded) {
                //first trade window
                log("Currently in first window");
                new ConditionalSleep(15000){
                    @Override
                    public boolean condition(){
                        int amount = 0;
                        if(getTrade().getTheirOffers().contains("Coins")) {
                            amount = getTrade().getTheirOffers().getItem("Coins").getAmount();
                        }
                        if(amount <= 200000 && amount >= 10000 && getTrade().didOtherAcceptTrade()){
                            moneyOffered = amount;
                            gaveCoins = true;
                            return true;
                        }
                        return false;
                    }
                }.sleep();

                if(gaveCoins) {
                    acceptTrade();
                    log("Accepted first trade, Money Offered: " + moneyOffered);
                    gaveCoins = false;
                    sleep(1000);
                    getTrade().acceptTrade();

                    new ConditionalSleep( 15000) {
                        @Override
                        public boolean condition() {
                            if(!getTrade().isCurrentlyTrading()){
                                if(currMoney != getInventory().getAmount("Coins")){
                                    gaveCoins = true;
                                    log("Second window accepted");
                                }
                                return true;
                            }
                            return false;
                        }
                    }.sleep(); //waiting for second player to accept 2nd window

                    if(gaveCoins) {
                        getKeyboard().typeString(moneyOffered/1000 + "K has been accepted.");
                        sleep(1000);

                        int won = rollDice();

                        if (won > winNumber) {
                            getKeyboard().typeString("Rolled a " + won + ". " + playerName + " has won " + (moneyOffered * 2)/1000 + "K!", true);
                            log("Player won with roll of " + won);
                            sleep(3000);

                            iter2 = 100;
                            new ConditionalSleep( 60000) {
                                @Override
                                public boolean condition() {
                                    if(iter2 == 100) {
                                        getPlayers().closest(playerName).interact("Trade with");
                                        log("Waiting to be traded back after win");
                                        iter2 = 0;
                                    }
                                    iter2++;
                                    return getTrade().isCurrentlyTrading();
                                }
                            }.sleep();

                            if(getTrade().isCurrentlyTrading()) {
                                getTrade().offer("Coins", (int) (2 * moneyOffered));
                                acceptTrade();
                            }

                            log("Offered coins and accepted first window");

                            new ConditionalSleep( 60000) {
                                @Override
                                public boolean condition() {
                                    return getTrade().didOtherAcceptTrade();
                                }
                            }.sleep();
                            getTrade().acceptTrade();

                            log("Gave coins to other player");
                        }
                        else {
                            getKeyboard().typeString("Rolled a " + won + ". " + playerName + " has lost!", true);
                            log("Player has lost with roll of " + won);
                            playerName = "";
                        }
                    }
                    else if(getTrade().isCurrentlyTrading()){
                        getTrade().declineTrade();
                    }
                }
                else if (getTrade().isCurrentlyTrading()){
                    getTrade().declineTrade();
                }
                playerName = "";
                moneyOffered = 0;
                gaveCoins = false;
                currMoney = getInventory().getAmount("Coins");
            }
            if(traded)
                log("Interaction ended --------------------------");
            traded = false;
        }
        if(!isInStart()){
            getWalking().walk(coords);
        }
        return 10;
    }

    @Override
    public void onExit(){
        log("Money difference from start to finish:" + (currMoney - startingCash));
        log("BOT HAS ENDED //////////////////////");
    }

    @Override
    public void onPaint(Graphics2D g){

    }

    private void spamMessage() throws  InterruptedException {
        if (iter > 900) {
            getKeyboard().typeString("Place your bets | " + winNumber +  "+ to win | 10K Min, 200k Max | EzMoneyMakin");
            iter = 0;
        }
    }

    private boolean isInStart(){
        if(myPlayer().getPosition().getX() != coords.getX() || myPlayer().getPosition().getY() != coords.getY()){
            return false;
        }
        return true;
    }

    private boolean isInRange(Position pos){
        if(pos.getX() <= coords.getX() + 1 && pos.getX() >= coords.getX() - 1){
            if(pos.getY() <= coords.getY() + 1 && pos.getY() >= coords.getY() - 1){
                return true;
            }
        }
        return false;
    }

    private void acceptTrade() throws InterruptedException{
        getMouse().move(random(230, 288), random(174,198));
        sleep(500);
        getMouse().click(false);
    }

    private int rollDice(){
        int rolled = rand.nextInt(100);
        log("Rolled:" + rolled);
        return rolled;
    }

}
