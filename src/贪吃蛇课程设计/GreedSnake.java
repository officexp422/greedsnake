package ̰���߿γ����;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
//����
public class GreedSnake extends KeyAdapter//�̳����������ڽ��ռ����¼�
{
	JFrame mainFrame;
	Canvas paintCanvas;//����һ���հ׻�ͼ����
	JLabel labelScore;//�Ƿ���
	SnakeModel snakeModel=null;//��
	public static final int DEFAULT_WIDTH=500;
	public static final int DEFAULT_HEIGHT=300;
	public static final int nodeWidth=10;
	public static final int nodeHeight=10;

	//GreedSnake();��ʼ����Ϸ����
	public GreedSnake()
	{
		//���ý���Ԫ��
		mainFrame=new JFrame("̰������Ϸ");
		Container cp=mainFrame.getContentPane();
		labelScore=new JLabel("�������÷���Ϊ��",JLabel.CENTER);
		cp.add(labelScore,BorderLayout.NORTH);
		paintCanvas=new Canvas();
		paintCanvas.setSize(DEFAULT_WIDTH+1,DEFAULT_HEIGHT+1);
		paintCanvas.addKeyListener(this);//��Ϸ��ע�������Ӧ�¼�
		cp.add(paintCanvas,BorderLayout.CENTER);
		JPanel panelButtom=new JPanel();
		panelButtom.setLayout(new GridLayout(4,1));
		JLabel labelHelp;//������Ϣ ��ǩ
		labelHelp=new JLabel("��P����ͣ��Ϸ",JLabel.CENTER);
		panelButtom.add(labelHelp);
		labelHelp=new JLabel("��C��������Ϸ",JLabel.CENTER);
		panelButtom.add(labelHelp);
		labelHelp= new JLabel("��F��C���ı��ٶ�",JLabel.CENTER);
		panelButtom.add(labelHelp);
		cp.add(panelButtom,BorderLayout.SOUTH);
		mainFrame.addKeyListener(this);
		mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		begin();
	}
		//keyPressed()���������
		public void keyPressed(KeyEvent e)
		{
			int keyCode=e.getKeyCode();
			if(snakeModel.running)
				switch(keyCode)
				{
				case KeyEvent.VK_UP:
					snakeModel.changeDirection(SnakeModel.UP);
					break;
				case KeyEvent.VK_DOWN:
					snakeModel.changeDirection(SnakeModel.DOWN);
					break;
				case KeyEvent.VK_LEFT:
					snakeModel.changeDirection(SnakeModel.LEFT);
					break;
				case KeyEvent.VK_RIGHT:
					snakeModel.changeDirection(SnakeModel.RIGHT);
					break;
					//����F/S��Ӧ�����¼���
				case KeyEvent.VK_F:
					snakeModel.speedUp();//����
					break;
				case KeyEvent.VK_S:
					snakeModel.speedDown();//����
					break;
					//��P/C��Ӧ��ͣ�����¼�
				case KeyEvent.VK_P:
				case KeyEvent.VK_C:
					snakeModel.changePauseState();//��ͣ�����
					break;
				default: break;
				}
			//���¿�ʼ
			if(keyCode==KeyEvent.VK_R||keyCode==KeyEvent.VK_ENTER)
			{
				snakeModel.running=false;
				begin();
			}
		}
		//repaint():������Ϸ���棨�����ߺ�ʳ�
		void repaint()
		{
			Graphics g=paintCanvas.getGraphics();
			//��������ɫ
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
			//��̰����
			g.setColor(Color.BLUE);
			LinkedList<Node> na=snakeModel.nodeArray;
			Iterator<Node> it =na.iterator();//�������������е�Ԫ��
			while(it.hasNext())//��鼯���������Ƿ���Ԫ��
			{
				Node n=(Node)it.next();//��next����������������е���һ��Ԫ��
				drawNode(g,n);
			}
			//��ʳ����ɫ
			g.setColor(Color.RED);//��ʳ����ɫ
			Node n=snakeModel.food;
			drawNode(g,n);
			updateScore();
		}
		//drawNode():�滭ĳһ�ڵ㣨�����ʳ�
		private void drawNode(Graphics g,Node n)
		{
			g.fillRect(n.x*nodeWidth, n.y*nodeHeight, nodeWidth-1, nodeHeight-1);
		}
		//updateScore():�ı�Ƿ���
		public void updateScore()
		{
			String s="�������÷���Ϊ��"+snakeModel.score;
			labelScore.setText(s);
		}
		//begin():��Ϸ��ʼ������̰����
		void begin()
		{
			if(snakeModel==null||!snakeModel.running)
			{
				snakeModel=new SnakeModel(this,DEFAULT_WIDTH/nodeWidth,DEFAULT_HEIGHT/nodeHeight);
				(new Thread(snakeModel)).start();//��ʼ����̰����̰�����߳�
			}
		}
		//main():������
		public static void main(String[] args)
		{
			GreedSnake gs=new GreedSnake();
		}
	}
	//Node:�����
	class Node
	{
		int x;
		int y;
		Node(int x,int y)
		{
			this.x=x;
			this.y=y;
		}
	}
	//SnakeModel:̰����ģ��
	class SnakeModel implements Runnable
	{
		//̰����ģ�ͼ̳�runnable()�ӿ�
		GreedSnake gs;
		boolean[][] matrix;//�������ݱ�����������
		LinkedList<Node> nodeArray=new LinkedList<Node>();
		Node food;
		int maxX;//��󳤶�
		int maxY;//�����
		int direction=2;//�����ߵ��˶�����
		boolean running=false;
		int timeInterval=200;//���ʱ�䣨�ٶȣ�
		double speedChangeRate=0.5;//�صĵĸı�̶�
		boolean paused=false;//��Ϸ״̬
		int score=0;
		int countMove=0;
		//UP��DOWN��ż����RIGHT��LEFT������
		public static final int UP=2;
		public static final int DOWN=4;
		public static final int LEFT=1;
		public static final int RIGHT=3;
		//GreedModel():��ʼ������
		public SnakeModel(GreedSnake gs, int maxX,int maxY)
		{
			this.gs=gs;
			this.maxX=maxX;
			this.maxY=maxY;
			matrix=new boolean[maxX][];
			for(int i=0;i<maxX;++i)
			{
				matrix[i]=new boolean[maxY];
				Arrays.fill(matrix[i], false);//û���ߺ�ʳ��ĵ�����false
			}
			//��ʼ��̰����
			int initArrayLength=maxX>20?10:maxX/2;
			for(int i=0;i<initArrayLength;++i)
			{
				int x=maxX/2+i;
				int y=maxY/2;
				nodeArray.addLast(new Node(x,y));
				matrix[x][y]=true;//̰���ߴ�����
			}
			food=createFood();
			matrix[food.x][food.y]=true;//ʳ�ﴦ��true
		}
		//changeDirection():�ı��˶�����
		public void changeDirection(int newDirection)
		{
			if(direction%2!=newDirection%2)
			{
				//��������/���³�ͻ
				direction=newDirection;
			}
		}
		//moveOn():̰�����˶�����
		public boolean moveOn()
		{
			Node n=(Node)nodeArray.getFirst();
			int x=n.x;//̰�����˶�λ��(x,y)
			int y=n.y;
			switch(direction)
			{
			case UP:y--;break;
			case DOWN:y++;break;
			case LEFT:x--;break;
			case RIGHT:x++;break;
			}
			if((0<=x&&x<maxX)&&(1<=y&&y<maxY))
			{
				if(matrix[x][y])//�Ե�ʳ�����ײ������
				{
					if(x==food.x&&y==food.y)//�Ե�ʳ��
					{
						nodeArray.addFirst(food);//��ͷ������һ�����
						//�Ʒֹ������ƶ����Ⱥ��ٶ��й�
						int scoreGet=(10000-200*countMove)/timeInterval;
						score=scoreGet>0?scoreGet:10;
						countMove=0;
						food=createFood();//��ʳ��λ�ø���food
						matrix[food.x][food.y]=true;//�������λ�ø�����
						return true;
					}
					else return false;//ײ��
				}
				else//ʲô��û�������ı����λ��
				{
					nodeArray.addFirst(new Node(x,y));//����ͷ��
					matrix[x][y]=true;
					n=(Node)nodeArray.removeLast();//ȥ��β��
					matrix[n.x][n.y]=false;
					countMove++;
					return true;
				}
			}
			return false;//Խ�磨ײ��ǽ�ڣ�
		}
		//run():̰�����˶��߳�
		public void run()
		{
			running=true;
			while(running)
			{
				try
				{
					Thread.sleep(timeInterval);
				}
				catch(Exception e)
				{
					break;
				}
				if(!paused)
				{
					if(moveOn())//δ����
					{
						gs.repaint();
					}
					else//��Ϸ����
					{
						JOptionPane.showMessageDialog(null, "GAME OVER","Game Over",JOptionPane.INFORMATION_MESSAGE);
						break;
					}
				}
			}
			running=false;
		}
		//createFood():����ʳ�Ｐ���õص�
		private Node createFood()
		{
			int x=0;
			int y=0;
			do
			{
				Random r=new Random();
				x=r.nextInt(maxX);//����ʳ��λ������Ϸ����
				y=r.nextInt(maxY);
			}
			while(matrix[x][y]);//����ط�Ҫ��Ҫ���Ϸֺš�������ԭ�ĵط��Ǽ��ϵ�,���Ҿ��ò�Ӧ�ü�
			return new Node(x,y);
		}
		//speedUp():�ӿ����˶��ٶ�
		public void speedUp()
		{
			timeInterval*= speedChangeRate;
		}
		//speedDown():�������˶��ٶ�
		public void speedDown()
		{
			timeInterval/=speedChangeRate;
		}
		//changePauseState():�ı���Ϸ״̬����ͣ�������
		public void changePauseState()
		{
			paused=!paused;
		}
	}
