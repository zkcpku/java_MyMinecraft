package org.wtfTeam.game;

import org.wtfTeam.engine.GameEngine;
import org.wtfTeam.engine.IGameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {

    public static int Start_type = 0;
    public static String[] terrains = {"default", "desert", "mountain", "superflat", "forest"};
    public static String[] terrains_disp = {"默认地形", "沙漠", "山地", "平坦", "森林"};
    public static int[][] size = {{1024, 768}, {1366, 768}, {1800, 1200}};
    public static int curT = 0;
    public static int curS = 1;
    public static int setting = 0;

    public static void pairSet(JButton JB, boolean flag) {
        JB.setEnabled(flag);
        JB.setVisible(flag);
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        final JButton button1 = new JButton("开始游戏");
        final JButton button2 = new JButton("载入存档");
        final JButton button3 = new JButton("退出游戏");
        final JButton button4 = new JButton("中分辨率");
        final JButton button5 = new JButton("默认地形");
        final JButton button6 = new JButton("设置");

        ImageIcon background = new ImageIcon("src\\main\\resources\\background\\background.jpg");
        JLabel label = new JLabel(background);
        int Width = background.getIconWidth();
        int Height = background.getIconHeight();
        label.setBounds(0, 0, Width, Height);
        JPanel imagePanel = (JPanel) frame.getContentPane();
        imagePanel.setOpaque(false);
        frame.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
        frame.setTitle("Fake Minecraft");// ���ô���ı���
        frame.setSize(Width, Height);// ���ô���Ĵ�С����λ������
        frame.setDefaultCloseOperation(3);// ���ô���Ĺرղ���
        frame.setLocationRelativeTo(null);// ���ô����������һ������ľ���λ�ã�����null��ʾ�����������Ļ������λ��
        frame.setResizable(false);
        //FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 500, 100);
        //frame.setLayout(fl);

        Dimension dim2 = new Dimension(200, 50);
        Font f = new Font("楷体", Font.BOLD, 28);
        Font ff = new Font("楷体", Font.BOLD, 32);
        frame.setLayout(null);

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == button1) {
                    //frame.setState(Frame.ICONIFIED);
                    Start_type = 0;
                    try {
                        boolean vSync = true;
                        IGameLogic gameLogic = new Game();
                        GameEngine gameEng = new GameEngine("fakeMC", size[curS][0], size[curS][1], vSync, gameLogic);
                        gameEng.start();
                    } catch (Exception excp) {
                        excp.printStackTrace();
                        System.exit(-1);
                    }
                }
                if (e.getSource() == button2) {
                    frame.setState(Frame.ICONIFIED);
                    File rfile;
                    BufferedReader reader = null;
                    rfile = new File("camera_record.log");
                    Start_type = 1;
                    float fx, fy, fz;
                    try {
                        reader = new BufferedReader(new FileReader(rfile));
                        String s = reader.readLine();
                        String st[] = s.split(" ");
                        fx = Float.valueOf(st[0]);
                        fy = Float.valueOf(st[1]);
                        fz = Float.valueOf(st[2]);
                        System.out.println(fx + " " + fy + " " + fz + "\n");
                        boolean vSync = true;
                        IGameLogic gameLogic = new Game(fx, fy, fz);
                        GameEngine gameEng = new GameEngine("fakeMC", size[curS][0], size[curS][1], vSync, gameLogic);
                        gameEng.start();
                    } catch (Exception excp) {
                        excp.printStackTrace();
                        System.exit(-1);
                    }
                }
                if (e.getSource() == button3) {
                    //button3.setText("do not click again!");
                    System.exit(0);
                }
                if (e.getSource() == button4) {
                    curS = (curS + 1) % 3;
                    if (curS == 1) button4.setText("中分辨率");
                    else if (curS == 0) button4.setText("低分辨率");
                    else button4.setText("高分辨率");
                    //System.exit(0);
                }
                if (e.getSource() == button5) {
                    curT = (curT + 1) % 5;
                    button5.setText(terrains_disp[curT]);
                    //System.exit(0);
                }
                if (e.getSource() == button6) {
                    setting = (setting + 1) % 2;
                    if (setting == 1) {
                        pairSet(button1, false);
                        pairSet(button2, false);
                        pairSet(button3, false);
                        pairSet(button4, true);
                        pairSet(button5, true);
                        button6.setText("返回");
                    } else {
                        pairSet(button4, false);
                        pairSet(button5, false);
                        pairSet(button1, true);
                        pairSet(button2, true);
                        pairSet(button3, true);
                        button6.setText("设置");
                    }
                    //System.exit(0);
                }
            }
        };

        button1.setBounds(400, 100, 200, 50);
        button1.setFocusPainted(false);
        button1.setFont(f);
        button1.setForeground(Color.WHITE);
        button1.setPreferredSize(dim2);
        button1.setContentAreaFilled(false);
        frame.add(button1);
        button1.addActionListener(listener);

        button2.setBounds(400, 230, 200, 50);
        button2.setFocusPainted(false);
        button2.setFont(f);
        button2.setForeground(Color.WHITE);
        button2.setPreferredSize(dim2);
        button2.setContentAreaFilled(false);
        frame.add(button2);
        button2.addActionListener(listener);

        button3.setBounds(400, 360, 200, 50);
        button3.setFocusPainted(false);
        button3.setFont(f);
        button3.setForeground(Color.WHITE);
        button3.setPreferredSize(dim2);
        button3.setContentAreaFilled(false);
        frame.add(button3);
        button3.addActionListener(listener);

        button4.setBounds(200, 200, 250, 150);
        button4.setFocusPainted(false);
        button4.setFont(ff);
        button4.setForeground(Color.WHITE);
        //button4.setPreferredSize(dim2);
        button4.setContentAreaFilled(false);
        //button4.setBounds(350, 150, 100, 50);
        frame.add(button4);
        button4.addActionListener(listener);
        pairSet(button4, false);

        button5.setBounds(500, 200, 250, 150);
        button5.setFocusPainted(false);
        button5.setFont(ff);
        button5.setForeground(Color.WHITE);
        //button5.setPreferredSize(dim2);
        button5.setContentAreaFilled(false);
        frame.add(button5);
        button5.addActionListener(listener);
        pairSet(button5, false);

        button6.setBounds(400, 490, 200, 50);
        button6.setFocusPainted(false);
        button6.setFont(f);
        button6.setForeground(Color.WHITE);
        //button5.setPreferredSize(dim2);
        button6.setContentAreaFilled(false);
        frame.add(button6);
        button6.addActionListener(listener);
        frame.setVisible(true);
    }
}