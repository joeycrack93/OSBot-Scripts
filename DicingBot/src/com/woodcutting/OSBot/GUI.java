package com.woodcutting.OSBot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI {

    public void run(Dicing main) {

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

        JLabel bankSelection = new JLabel("I Need this");
        bankSelection.setSize(95, 20);
        c.gridx = 0;
        c.gridy = 1;
        settingsPanel.add(bankSelection , c);

        JComboBox<String> bankList = new JComboBox<String>(new String[] { "Dont mess with this"});
        bankList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        bankList.setSize(110, 20);
        c.gridx = 1;
        c.gridy = 1;
        settingsPanel.add(bankList, c);

        JLabel whatItem = new JLabel("Enter number to win:");
        whatItem.setSize(95,20);
        c.gridx = 0;
        c.gridy = 3;
        settingsPanel.add(whatItem,c);

        JPanel itemHolder = new JPanel();
        itemHolder.setSize(110, 20);
        itemHolder.setLayout(new GridLayout(1,0));
        JTextField itemName = new JTextField();
        itemName.setSize(110, 20);
        itemHolder.add(itemName);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        settingsPanel.add(itemHolder, c);


        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            main.winNumber = Integer.parseInt(itemName.getText());
            jFrame.setVisible(false);
        });
        startButton.setSize(70, 20);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 4;
        settingsPanel.add(startButton, c);

        jFrame.setVisible(true);
    }

    private void updateFileAmounts(Dicing main, String files){

    }

}
