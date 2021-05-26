package com.woodcutting.OSBot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class GUI {

    public void run(MouseTest main) {

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

        JLabel bankSelection = new JLabel("Type:");
        bankSelection.setSize(95, 20);
        c.gridx = 0;
        c.gridy = 1;
        settingsPanel.add(bankSelection , c);

        JComboBox<String> bankList = new JComboBox<String>(new String[] { "Fishing", "Woodcutting", "Mining"});
        bankList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        bankList.setSize(110, 20);
        c.gridx = 1;
        c.gridy = 1;
        settingsPanel.add(bankList, c);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            updateFileAmounts(main, (String)bankList.getSelectedItem());
            jFrame.setVisible(false);
        });
        startButton.setSize(70, 20);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 3;
        settingsPanel.add(startButton, c);

        jFrame.setVisible(true);
    }

    private void updateFileAmounts(MouseTest main, String type){
        main.mode = type;
    }
}
