package com.woodcutting.OSBot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI {

    public void run(Cutting main) {

        JFrame jFrame = new JFrame("OSBOT GUI Tutorial");
        jFrame.setSize(300, 500);
        jFrame.setResizable(false);

        JPanel settingsPanel = new JPanel();
        TitledBorder leftBorder = BorderFactory.createTitledBorder("Settings");
        leftBorder.setTitleJustification(TitledBorder.LEFT);
        settingsPanel.setBorder(leftBorder);
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setSize(280, 500);
        jFrame.add(settingsPanel);
        GridBagConstraints c = new GridBagConstraints();

        JLabel treeSelection = new JLabel("Select a Tree:");
        treeSelection.setSize(150, 20);
        c.gridx = 0;
        c.gridy = 0;
        settingsPanel.add(treeSelection, c);

        JComboBox<String> treeList = new JComboBox<String>(new String[] { "None", "Tree", "Oak", "Willow", "Yew", "Magic tree"});
        treeList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLogTypes(main, (String)treeList.getSelectedItem());
            }
        });
        treeList.setSize(110, 20);
        c.gridx = 1;
        c.gridy = 0;
        settingsPanel.add(treeList, c);


        JLabel bankSelection = new JLabel("Select a Bank:");
        bankSelection.setSize(95, 20);
        c.gridx = 0;
        c.gridy = 1;
        settingsPanel.add(bankSelection , c);

        JComboBox<String> bankList = new JComboBox<String>(new String[] { "Drop", "Varrock West", "Varrock East"});
        bankList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBank(main, bankList.getSelectedIndex());
            }
        });
        bankList.setSize(110, 20);
        c.gridx = 1;
        c.gridy = 1;
        settingsPanel.add(bankList, c);

        JLabel time = new JLabel("Enter time for break");
        time.setSize(95,20);
        c.gridx = 0;
        c.gridy = 2;
        settingsPanel.add(time,c);

        JPanel timeFields = new JPanel();
        timeFields.setLayout(new GridLayout(1,0));
        JTextField timeH = new JTextField();
        JTextField timeM = new JTextField();
        JTextField timeS = new JTextField();
        timeFields.add(timeH);
        timeFields.add(timeM);
        timeFields.add(timeS);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        settingsPanel.add(timeFields, c);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            updateTime(main, timeH.getText(), timeM.getText(), timeS.getText());
            main.isReady = true;
            jFrame.setVisible(false);
        });
        startButton.setSize(70, 20);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 3;
        settingsPanel.add(startButton, c);

        jFrame.setVisible(true);
    }

    private void updateLogTypes(Cutting main, String edit){
        switch(edit){
            case "Tree":
                main.treeType = "Tree";
                main.logType = "Logs";
                main.xpPerLog = 25;
                break;
            case "Oak":
                main.treeType = "Oak";
                main.logType = "Oak logs";
                main.xpPerLog = 40;
                break;
            case "Willow":
                main.treeType = "Willow";
                main.logType = "Willow logs";
                main.xpPerLog = 67.5;
                break;
            case "Yew":
                main.treeType = "Yew";
                main.logType = "Yew logs";
                main.xpPerLog = 67.5;
                break;
        }
    }

    private void updateBank(Cutting main, int bank){
        main.bankChoice = bank;
    }

    private void updateTime(Cutting main, String h, String m, String s){
        if(h != "" && m != "" && s != "") {
            main.time.setEndTime(Long.parseLong(h), Long.parseLong(m), Long.parseLong(s));
        }
    }

}
