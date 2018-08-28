import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Random;
import javax.swing.*;

/**
 * ̰����demo
 * @author LYT
 *
 */
public class SnakeGame extends JComponent {
			
	private final int MAX_SIZE = 400; //�����������	
	private Tile head; //��ͷ
	private Tile[] body = new Tile[MAX_SIZE]; //����
	private int bodyLength = 0; //���峤��	
	
	private String direction = "R"; //ǰ������,��ʼ����
	private String curDirection = "R"; //��ǰǰ������
	
	private boolean isEaten = false; //ʳ���Ƿ񱻳�
	private boolean isRunning = false; //���Ƿ��ڶ�
	private boolean pause = true; //�Ƿ���ͣ	
	
	private int randomX, randomY; //������ɵ�����

	/**
	 * ��ʼ�����֡��ߵĽڵ㣬 ����keyListener�������߳�
	 */
	public SnakeGame() {
		//����	
		Layout();
		
		//��ʼ��ͷ������
		ProduceRandom();
		head = new Tile(randomX, randomY);
		
		//��ʼ���������нڵ�
		for (int i = 0; i < MAX_SIZE; i++) 
			body[i] = new Tile(0, 0);
		
		//�����ı䷽����ͣ/��ʼ
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
					//���ո�ʼ/��ͣ
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

		//��ʼ��ʱ
		new Timer();
		//����
		StartMoving();
		
		setFocusable(true);
	}
	
	private JLabel label = new JLabel("������");
	private JLabel score = new JLabel("0");	
	private JLabel label2 = new JLabel("ʱ�䣺");
	private JLabel label3 = new JLabel("��Ϸ˵����");
	private JTextArea text = new JTextArea("�ո����ʼ/��ͣ,ʹ�����������ƶ����÷�Խ��,�ٶ�Խ�졣");
	private JLabel time = new JLabel("");	
	private Font f = new Font("����",Font.PLAIN,15); 
	private Font f2 = new Font("����",Font.PLAIN,13);
	private JPanel jPanel = new JPanel();	
	
	/**
	 * ҳ�沼��
	 */
	private void Layout() {
		//����	
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
	 * ��ͼ
	 * ÿ�ε���repaint()����ˢ��
	 * @param g1
	 */
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		//�����߶ξ��״��Ե
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//����ͷ
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

		//ˢ������
		Image bdy = Toolkit.getDefaultToolkit().getImage("body.png");
		for (int i = 0; i < bodyLength; i++) 
			g.drawImage(bdy, body[i].x, body[i].y, 20, 20, this);
		
		//ˢ��ʳ��
		if (EatFood())  
			ProduceRandom();	
		Image food = Toolkit.getDefaultToolkit().getImage("food.png");
		g.drawImage(food, randomX, randomY, 20, 20, this);
		
		//ǽ
		g.setColor(Color.gray);
		g.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g.setBackground(Color.black);
		g.drawRect(2, 7, 491, 467);	
	}
	
	/**
	 * �������X,Y����
	 */
	public void ProduceRandom() {
		Random rand = new Random();
		randomX = (rand.nextInt(21) + 1) * 22 + 7;
		randomY = (rand.nextInt(20) + 1) * 22 + 12;
		l:while (true) {
			//��ʼ��
			if (bodyLength == 0) {
				break;
			} 
			//������ɵ������������ϣ�����������
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
	 * �Ƿ�ײǽ
	 */
	@SuppressWarnings("deprecation")
	private void HitWall() {
		if ((curDirection == "L" && head.x < 7) || (curDirection == "R" && head.x > 489) ||
			(curDirection == "U" && head.y < 12) || (curDirection == "D") && head.y > 472) {
			isRunning = false;
			int result = JOptionPane.showConfirmDialog(null, "��Ϸ�������Ƿ����¿�ʼ��",
						"��ʾ", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_NO_OPTION) 
				reset();
			else 
				run.stop();
		}
	}
	
	/**
	 * �Ƿ�ҧ���Լ�
	 */
	@SuppressWarnings("deprecation")
	private void HitItself() {
		for (int i = 0; i < bodyLength; i++) {
			if (body[i].x == head.x && body[i].y == head.y) {
				isRunning = false;
				int result = JOptionPane.showConfirmDialog(null, "��Ϸ�������Ƿ����¿�ʼ��",
							"��ʾ", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_NO_OPTION) 
					reset();
				else 
					run.stop();
			}
		}
	}
	
	/**
	 * �Ƿ����ʳ��
	 * @return true ����
	 * 		   false û��
	 */
	private boolean EatFood() {
		if (head.x == randomX && head.y == randomY)
			isEaten = true;
		else 
			isEaten = false;
		return isEaten;
	}
	
	private Thread run; //���˶�״̬�����߳�
	private long refreshTime = 300; //ÿ300msˢ��һ��	
	
	/**
	 * �߿�ʼmove
	 */
	private void StartMoving() {
		run = new Thread() {
			@Override
			public void run() {
				while (true) {
					//ÿ��300ms����һ��
					try {
						Thread.sleep(refreshTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Tile temp = new Tile(0, 0); //�м����
					Tile temp2 = new Tile(0, 0);
					
					if (!pause) {
						temp.x = head.x;
						temp.y = head.y;
						//ͷ���ƶ�
						if (direction == "L")
							head.x -= 22;
						if (direction == "R")
							head.x += 22;
						if (direction == "U")
							head.y -= 22;
						if (direction == "D")
							head.y += 22;
						curDirection = direction;
						
						//�����ƶ�
						for (int i = 0; i < bodyLength; i++) {
							temp2.x = body[i].x;
							temp2.y = body[i].y;
							body[i].x = temp.x;
							body[i].y = temp.y;
							temp.x = temp2.x;
							temp.y = temp2.y;
						}
						
						//�Ե�ʳ�+1s��ͬʱ���٣�������Ϸ�Ѷ�
						if (EatFood()) {
							bodyLength++;
							body[bodyLength-1].x = temp.x;
							body[bodyLength-1].y = temp.y;
							score.setText(""+(bodyLength));
							if (refreshTime > 50)
								refreshTime -= 5;
						}
						
						//ˢ��ͼ��
						repaint();
						
						//ˢ�����ж��Ƿ�gameover
						HitWall();
						HitItself();
					}
				}
			}
		};
		run.start();
	}
	
	/**
	 * ������ڵ����
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
	
	private int hour = 0, min = 0, sec = 0;	//��¼��Ϸʱ��
	
	/**
	 * ��ʱ��
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
	 * ���¿�ʼ����ʼ��״̬
	 */
	public void reset() {
		//��ʼ��ͷ������
		ProduceRandom();
		head = new Tile(randomX, randomY);
		//��ʼ������ڵ�����
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
		Image img = Toolkit.getDefaultToolkit().getImage("title.png"); //����ͼ��
		game.setIconImage(img);
		game.setTitle("̰����");	
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setSize(610, 545);
		game.setResizable(false);	
		game.add(snake);
		game.setVisible(true);
	}
}

