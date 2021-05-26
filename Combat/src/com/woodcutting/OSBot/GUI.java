package com.woodcutting.OSBot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI {

    public void run(Combat main) {

        JFrame jFrame = new JFrame("OSBOT GUI Tutorial");
        jFrame.setSize(300, 500);
        jFrame.setResizable(false);

        JPanel settingsPanel = new JPanel();
        TitledBorder leftBorder = BorderFactory.createTitledBorder("Settings");
        leftBorder.setTitleJustification(TitledBorder.LEFT);
        settingsPanel.setBorder(leftBorder);
        settingsPanel.setLayout(null);
        settingsPanel.setBounds(5, 10, 280, 180);
        jFrame.add(settingsPanel);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(5, 350, 70, 20);
        jFrame.add(startPanel);

        JLabel foodSelection = new JLabel("Select Food:");
        foodSelection.setBounds(10, 40, 95, 20);
        settingsPanel.add(foodSelection);

        JComboBox<String> foodList = new JComboBox<String>(new String[] { "None", "Shrimps", "Anchovies","Trout", "Salmon","Tuna", "Lobster"});
        foodList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFishTypes(main, (String)foodList.getSelectedItem());
            }
        });
        foodList.setBounds(160, 40, 110, 20);
        settingsPanel.add(foodList);


        JLabel bankSelection = new JLabel("Select a Bank:");
        bankSelection.setBounds(10, 70, 95, 20);
        settingsPanel.add(bankSelection);

        JComboBox<String> bankList = new JComboBox<String>(new String[] { "None", "Varrock West", "Varrock East"});
        bankList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBank(main, bankList.getSelectedIndex());
            }
        });
        bankList.setBounds(160, 70, 110, 20);
        settingsPanel.add(bankList);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            main.isReady = true;
            jFrame.setVisible(false);
        });
        startButton.setBounds(5, 390, 70, 20);
        startPanel.add(startButton);

        jFrame.setVisible(true);
    }

    private void updateFishTypes(Combat main, String edit){
        main.foodType = edit;
    }

    private void updateBank(Combat main, int bank){
        main.bank = bank;
    }

}
