import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Random;
import javax.swing.*;

/**
 * 贪吃蛇demo
 * @author LYT
 *
 */
public class SnakeGame extends JComponent {
			
	private final int MAX_SIZE = 400; //蛇身体最长长度	
	private Tile head; //蛇头
	private Tile[] body = new Tile[MAX_SIZE]; //蛇身
	private int bodyLength = 0; //身体长度	
	
	private String direction = "R"; //前进方向,初始向右
	private String curDirection = "R"; //当前前进方向
	
	private boolean isEaten = false; //食物是否被吃
	private boolean isRunning = false; //蛇是否在动
	private boolean pause = true; //是否暂停	
	
	private int randomX, randomY; //随机生成的坐标

	/**
	 * 初始化布局、蛇的节点， 设置keyListener，启动线程
	 */
	public SnakeGame() {
		//布局	
		Layout();
		
		//初始化头部坐标
		ProduceRandom();
		head = new Tile(randomX, randomY);
		
		//初始化身体所有节点
		for (int i = 0; i < MAX_SIZE; i++) 
			body[i] = new Tile(0, 0);
		
		//按键改变方向、暂停/开始
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (isRunning && curDirection != "L")
						direction = "R";
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (isRunning && curDirection != "R")
						direction = "L";
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (isRunning && curDirection != "D")
						direction = "U";
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (isRunning && curDirection != "U")
						direction = "D";
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					//按空格开始/暂停
					if (!pause) {
						pause = true;
						isRunning = false;
					}
					else {
						pause = false;
						isRunning = true;
					}
				}
			}
		});

		//开始计时
		new Timer();
		//启动
		StartMoving();
		
		setFocusable(true);
	}
	
	private JLabel label = new JLabel("分数：");
	private JLabel score = new JLabel("0");	
	private JLabel label2 = new JLabel("时间：");
	private JLabel label3 = new JLabel("游戏说明：");
	private JTextArea text = new JTextArea("空格键开始/暂停,使用上下左右移动。得分越高,速度越快。");
	private JLabel time = new JLabel("");	
	private Font f = new Font("宋体",Font.PLAIN,15); 
	private Font f2 = new Font("宋体",Font.PLAIN,13);
	private JPanel jPanel = new JPanel();	
	
	/**
	 * 页面布局
	 */
	private void Layout() {
		//布局	
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		add(label);
		label.setBounds(500, 50, 80, 20);
		label.setFont(f);
		
		add(score);
		score.setBounds(500, 75, 80, 20);
		score.setFont(f);	
		
		add(label2);
		label2.setBounds(500, 100, 80, 20);
		label2.setFont(f);
					
		add(time);
		time.setBounds(500, 125, 80, 20);
		time.setFont(f);		

		add(jPanel);
		jPanel.setBounds(500, 150, 93, 1);
		jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		add(label3);
		label3.setBounds(500, 170, 80, 20);
		label3.setFont(f);
		
		add(text);
		text.setBounds(500, 195, 100, 350);
		text.setFont(f2);
		text.setLineWrap(true);
		text.setOpaque(false);
	}
	
	@Override
	/**
	 * 画图
	 * 每次调用repaint()进行刷新
	 * @param g1
	 */
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		//消除线段锯齿状边缘
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//画蛇头
		Image pic = null;
		if (curDirection == "L") {
			pic = Toolkit.getDefaultToolkit().getImage("head_l.png");
		}
		if (curDirection == "R") {
			pic = Toolkit.getDefaultToolkit().getImage("head_r.png");	
		}
		if (curDirection == "U") {
			pic = Toolkit.getDefaultToolkit().getImage("head_u.png");	
		}
		if (curDirection == "D") {
			pic = Toolkit.getDefaultToolkit().getImage("head_d.png");		
		}
		g.drawImage(pic, head.x, head.y, 20, 20, this);	

		//刷新身体
		Image bdy = Toolkit.getDefaultToolkit().getImage("body.png");
		for (int i = 0; i < bodyLength; i++) 
			g.drawImage(bdy, body[i].x, body[i].y, 20, 20, this);
		
		//刷新食物
		if (EatFood())  
			ProduceRandom();	
		Image food = Toolkit.getDefaultToolkit().getImage("food.png");
		g.drawImage(food, randomX, randomY, 20, 20, this);
		
		//墙
		g.setColor(Color.gray);
		g.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g.setBackground(Color.black);
		g.drawRect(2, 7, 491, 467);	
	}
	
	/**
	 * 生成随机X,Y坐标
	 */
	public void ProduceRandom() {
		Random rand = new Random();
		randomX = (rand.nextInt(21) + 1) * 22 + 7;
		randomY = (rand.nextInt(20) + 1) * 22 + 12;
		l:while (true) {
			//初始化
			if (bodyLength == 0) {
				break;
			} 
			//如果生成的坐标在蛇身上，则重新生成
			else {
				for (int i = 0; i < bodyLength; i++) {
					if (body[i].x == randomX && body[i].y == randomY) {
						randomX = (rand.nextInt(21) + 1) * 22 + 7;
						randomY = (rand.nextInt(20) + 1) * 22 + 12;
						break;
					}
					else if (i == bodyLength - 1) {
						break l;	
					}
				}
			}
		}		
	}
	
	/**
	 * 是否撞墙
	 */
	@SuppressWarnings("deprecation")
	private void HitWall() {
		if ((curDirection == "L" && head.x < 7) || (curDirection == "R" && head.x > 489) ||
			(curDirection == "U" && head.y < 12) || (curDirection == "D") && head.y > 472) {
			isRunning = false;
			int result = JOptionPane.showConfirmDialog(null, "游戏结束！是否重新开始？",
						"提示", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_NO_OPTION) 
				reset();
			else 
				run.stop();
		}
	}
	
	/**
	 * 是否咬到自己
	 */
	@SuppressWarnings("deprecation")
	private void HitItself() {
		for (int i = 0; i < bodyLength; i++) {
			if (body[i].x == head.x && body[i].y == head.y) {
				isRunning = false;
				int result = JOptionPane.showConfirmDialog(null, "游戏结束！是否重新开始？",
							"提示", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_NO_OPTION) 
					reset();
				else 
					run.stop();
			}
		}
	}
	
	/**
	 * 是否吃了食物
	 * @return true 吃了
	 * 		   false 没吃
	 */
	private boolean EatFood() {
		if (head.x == randomX && head.y == randomY)
			isEaten = true;
		else 
			isEaten = false;
		return isEaten;
	}
	
	private Thread run; //蛇运动状态更新线程
	private long refreshTime = 300; //每300ms刷新一次	
	
	/**
	 * 蛇开始move
	 */
	private void StartMoving() {
		run = new Thread() {
			@Override
			public void run() {
				while (true) {
					//每隔300ms更新一次
					try {
						Thread.sleep(refreshTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Tile temp = new Tile(0, 0); //中间变量
					Tile temp2 = new Tile(0, 0);
					
					if (!pause) {
						temp.x = head.x;
						temp.y = head.y;
						//头部移动
						if (direction == "L")
							head.x -= 22;
						if (direction == "R")
							head.x += 22;
						if (direction == "U")
							head.y -= 22;
						if (direction == "D")
							head.y += 22;
						curDirection = direction;
						
						//身体移动
						for (int i = 0; i < bodyLength; i++) {
							temp2.x = body[i].x;
							temp2.y = body[i].y;
							body[i].x = temp.x;
							body[i].y = temp.y;
							temp.x = temp2.x;
							temp.y = temp2.y;
						}
						
						//吃到食物，+1s，同时加速，增加游戏难度
						if (EatFood()) {
							bodyLength++;
							body[bodyLength-1].x = temp.x;
							body[bodyLength-1].y = temp.y;
							score.setText(""+(bodyLength));
							if (refreshTime > 50)
								refreshTime -= 5;
						}
						
						//刷新图形
						repaint();
						
						//刷新完判断是否gameover
						HitWall();
						HitItself();
					}
				}
			}
		};
		run.start();
	}
	
	/**
	 * 蛇身体节点的类
	 * @author LYT
	 */
	class Tile {
		int x;
		int y;
		
		public Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private int hour = 0, min = 0, sec = 0;	//记录游戏时间
	
	/**
	 * 计时器
	 * @author LYT
	 */
	class Timer extends Thread {
		public Timer() {
			this.start();
		}
		
		@Override
		public void run() {
			while (true) {
				if (isRunning) {
					sec ++;
					if (sec >= 60) {
						sec = 0;
						min++;
					}
					if (min >= 60) {
						min = 0;
						hour++;
					}
					showTime();
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private void showTime() {
			String strTime = "";
			if (hour < 10) strTime = "0" + hour + ":";
			else 		   strTime = "" + hour + ":";
			
			if (min < 10) strTime = strTime + "0" + min + ":";
			else		  strTime = strTime + "" + min + ":";
			
			if (sec < 10) strTime = strTime + "0" + sec;
			else 		  strTime = strTime + "" + sec;
			
			time.setText(strTime);
		}
	}
	
	/**
	 * 重新开始，初始化状态
	 */
	public void reset() {
		//初始化头部坐标
		ProduceRandom();
		head = new Tile(randomX, randomY);
		//初始化身体节点坐标
		for (int i = 0; i < MAX_SIZE; i++) {
			body[i].x = 0;
			body[i].y = 0;
		}
		 hour = 0;
		 min = 0;
		 sec = 0;
		 direction = "R";
		 curDirection = "R";
		 isEaten = false;
		 isRunning = true;
		 pause = false;
		 refreshTime = 300;
		 bodyLength = 0;
		 score.setText("0");
		 
		 run = new Thread();
		 run.start();
	}
	
	public static void main(String[] args) {
		SnakeGame snake = new SnakeGame();
		
		JFrame game = new JFrame();
		Image img = Toolkit.getDefaultToolkit().getImage("title.png"); //窗口图标
		game.setIconImage(img);
		game.setTitle("贪吃蛇");	
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setSize(610, 545);
		game.setResizable(false);	
		game.add(snake);
		game.setVisible(true);
	}
}

