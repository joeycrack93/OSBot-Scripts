package com.woodcutting.OSBot;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


@ScriptManifest(author = "MysteriousCorn", info = "MessageSpammer", name = "MessageSpammer", version = 0, logo = "")

public class MessageSpammer extends Script {

    private boolean spamMode;
    public ArrayList<String> messages;

    private GUI gui;

    public void moveMouseInstantly(final int x, final int y) {
        getBot().getMouseEventHandler().generateBotMouseEvent(MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false, MouseEvent.NOBUTTON, true);
    }

    @Override
    public void onStart() {
        log("BOT HAS STARTED /////////////////////");
        messages = new ArrayList<String>();
        gui = new GUI();
        gui.run(this);
        spamMode = false;

        bot.addKeyListener(new BotKeyListener() {
            @Override
            public void checkKeyEvent(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == 91) {
                    //91 = '['
                    spamMode = true;
                    log("Spam mode enabled");
                }
                else if(keyEvent.getKeyCode() == 93){
                    // 93 = ']'
                    spamMode = false;
                    log("Spam mode disabled");
                }
            }
        });

    }

    public int onLoop() throws InterruptedException{
        if(spamMode) {
            getKeyboard().typeString(messages.get(random(messages.size())));
            sleep(5000);
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
}
