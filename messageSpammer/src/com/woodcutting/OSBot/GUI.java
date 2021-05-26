package com.woodcutting.OSBot;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class GUI {

    public void run(MessageSpammer main) {

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

        JLabel time = new JLabel("Enter message");
        time.setSize(95,20);
        c.gridx = 0;
        c.gridy = 2;
        settingsPanel.add(time,c);

        JPanel timeFields = new JPanel();
        timeFields.setSize(110, 20);
        timeFields.setLayout(new GridLayout(1,0));
        JTextField timeS = new JTextField();
        timeS.setSize(110, 20);
        timeFields.add(timeS);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        settingsPanel.add(timeFields, c);


        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> {
            main.messages.add(timeS.getText());
        });
        c.gridx = 0;
        c.gridy = 4;
        settingsPanel.add(enterButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            jFrame.setVisible(false);
        });
        startButton.setSize(60, 20);
        c.gridx = 1;
        c.gridy = 4;
        settingsPanel.add(startButton, c);

        jFrame.setVisible(true);
    }

}
