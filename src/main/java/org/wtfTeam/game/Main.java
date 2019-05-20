package org.wtfTeam.game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import org.wtfTeam.engine.GameEngine;
import org.wtfTeam.engine.IGameLogic;
 
public class Main {
 
	public static int Start_type = 0;
	
    public static void main(String[] args) {
		final JFrame frame = new JFrame();
		final JButton button1 = new JButton("开始游戏");
		final JButton button2 = new JButton("载入存档");
		final JButton button3 = new JButton("退出游戏");
		
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
		FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 500, 100);
		frame.setLayout(fl);
		
		Dimension dim2 = new Dimension(200,50);
		Font f=new Font("����",Font.BOLD,28);
		
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == button1) {
					frame.setState(Frame.ICONIFIED);
					Start_type = 0;
			        try {
			            boolean vSync = true;
			            IGameLogic gameLogic = new DummyGame();
			            GameEngine gameEng = new GameEngine("fakeMC", 1366, 768, vSync, gameLogic);
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
					rfile = new File("record\\camera_record.log");
					Start_type = 1;
					float fx, fy, fz;
			        try {
			        	reader = new BufferedReader(new FileReader(rfile));
			        	String s = reader.readLine();
			        	String st[] = s.split(" ");
			        	fx = Float.valueOf(st[0]);
			        	fy = Float.valueOf(st[1]);
			        	fz = Float.valueOf(st[2]);
			        	System.out.println(fx+ " " + fy + " " + fz + "\n");
			            boolean vSync = true;
			            IGameLogic gameLogic = new DummyGame(fx, fy, fz);
			            GameEngine gameEng = new GameEngine("fakeMC", 1366, 768, vSync, gameLogic);
			            gameEng.start();
			        } catch (Exception excp) {
			            excp.printStackTrace();
			            System.exit(-1);
			        }
				}
				if (e.getSource() == button3) {
					System.exit(0);
				}
			}
		};
		
		
		button1.setFocusPainted(false);
		button1.setFont(f);
		button1.setForeground(Color.WHITE);
		button1.setPreferredSize(dim2);
		button1.setContentAreaFilled(false);
		frame.add(button1);
		button1.addActionListener(listener);
		
		button2.setFocusPainted(false);
		button2.setFont(f);
		button2.setForeground(Color.WHITE);
		button2.setPreferredSize(dim2);
		button2.setContentAreaFilled(false);
		frame.add(button2);
		button2.addActionListener(listener);
		
		button3.setFocusPainted(false);
		button3.setFont(f);
		button3.setForeground(Color.WHITE);
		button3.setPreferredSize(dim2);
		button3.setContentAreaFilled(false);
		frame.add(button3);
		button3.addActionListener(listener);
		
		frame.setVisible(true);
    }
}