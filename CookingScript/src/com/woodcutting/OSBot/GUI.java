package com.woodcutting.OSBot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI {

    public void run(Cooking main) {

        JFrame jFrame = new JFrame("Cooking");
        jFrame.setSize(300, 500);
        jFrame.setResizable(false);

        JPanel settingsPanel = new JPanel();
        TitledBorder leftBorder = BorderFactory.createTitledBorder("Settings");
        leftBorder.setTitleJustification(TitledBorder.LEFT);
        settingsPanel.setBorder(leftBorder);
        settingsPanel.setBounds(5, 10, 280, 90);
        jFrame.add(settingsPanel);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(5, 350, 70, 20);
        jFrame.add(startPanel);

        //Labels and Drop downs below

        //Fishing Type
        JLabel fishSelection = new JLabel("Select fish type:");
        fishSelection.setSize(10, 40);
        settingsPanel.add(fishSelection);

        JComboBox<String> fishList = new JComboBox<String>(new String[] {"", "Shrimps", "Anchovies", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish"});
        fishList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFishType(main, (String)fishList.getSelectedItem());
            }
        });
        fishSelection.setSize(110, 20);
        settingsPanel.add(fishList);

        //log selection
        JLabel logSelection = new JLabel("Select log type:");
        logSelection.setSize(10,40);
        settingsPanel.add(logSelection);

        JComboBox<String> logList = new JComboBox<String>(new String[] {"", "Logs", "Oak logs", "Willow logs"});
        logList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLogType(main, (String)logList.getSelectedItem());
            }
        });
        logList.setSize(110,20);
        settingsPanel.add(logList);

        //start button
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            main.isReady = true;
            jFrame.setVisible(false);
        });
        startButton.setBounds(5, 390, 70, 20);
        startPanel.add(startButton);

        jFrame.setVisible(true);
    }

    private void updateFishType(Cooking main, String fish){
        main.fishType = "Raw" + fish.toLowerCase();
        main.cookedFish = fish;
    }

    private void updateLogType(Cooking main, String log){
        main.logType = log;
        main.itemsToKeep[0] = 590;
        switch(log){
            case "Logs":
                main.itemsToKeep[1] = 1511;
                break;
            case "Oak logs":
                main.itemsToKeep[1] = 1521;
                break;
            case "Willow logs":
                main.itemsToKeep[1] = 1519;
                break;
        }
    }
}
