package com.woodcutting.OSBot;

import org.osbot.rs07.api.model.Item;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class GUI {

    public void run(Fishing main) {

        JFrame jFrame = new JFrame("FISHING");
        jFrame.setSize(300, 500);
        jFrame.setResizable(false);

        JPanel settingsPanel = new JPanel();
        TitledBorder leftBorder = BorderFactory.createTitledBorder("Settings");
        leftBorder.setTitleJustification(TitledBorder.LEFT);
        settingsPanel.setBorder(leftBorder);
        settingsPanel.setLayout(null);
        settingsPanel.setBounds(5, 10, 280, 90);
        jFrame.add(settingsPanel);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(5, 350, 70, 20);
        jFrame.add(startPanel);

        //Labels and Drop downs below

        //Fishing Type
        JLabel fishSelection = new JLabel("Select fishing type:");
        fishSelection.setBounds(10, 40, 95, 20);
        settingsPanel.add(fishSelection);

        JComboBox<String> fishList = new JComboBox<String>(new String[] {"", "Small Net", "Fly Fishing"});
        fishList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFish(main, (String) fishList.getSelectedItem());
            }
        });
        fishList.setBounds(160, 40, 110, 20);
        settingsPanel.add(fishList);

        //Bank Selection
        JLabel bankSelection = new JLabel("Select a Bank:");
        bankSelection.setBounds(10, 70, 95, 20);
        settingsPanel.add(bankSelection);

        JComboBox<String> bankList = new JComboBox<String>(new String[] {"Drop", "Varrock East", "Varrock West", "Draynor", "Edgeville"});
        bankList.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBank(main, bankList.getSelectedIndex());
            }
        });
        bankList.setBounds(160, 70, 110, 20);
        settingsPanel.add(bankList);

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

    private void updateBank(Fishing main, int bank){
        main.bankChoice = bank;
    }

    private  void updateFish(Fishing main, String type){
        switch(type){
            case "Small Net":
                main.fishType = "Small Net";
                main.bankKeep = new int[]{303};
                break;
            case "Fly Fishing":
                main.fishType = "Lure";
                main.bankKeep = new int[]{309, 314};
                break;
        }
    }
}
