import java.util.Random;
import java.util.Vector;

abstract class AI3
{
	public abstract void move(Board board);
}

class MonteCalroAI extends AI3 implements Cloneable
{
	class Move extends Point
	{

		public Move()
		{
			super(0, 0);
		}

		public Move(int x, int y, int e)
		{
			super(x, y);
		}
	}
	int all_playouts = 0;
	Disc stone[] = new Disc[60];
	Vector can_put = new Vector();
	int try_num = 1;//playout回数
	int difference[] = new int[try_num];//playoutの結果(score)を保持する
	Board state = new Board();//Boardクラスのオブジェクト作成
	Board clone = state.clone();//Boardクラスのクローン作製
	ConsoleBoard board = new ConsoleBoard();
	private int currentColor;//現在の手番の色
	Point point = new Point();
	Disc disc = new Disc();

	public void move(Board board)//打てる手が複数あればモンテカルロ木探索を行う
	{
		can_put = board.getMovablePos();
		BookManager book = new BookManager();
		Vector movables = book.find(board);
		if(movables.size() >= 2)
		{
			board.move(select_best_move(board,can_put));
		}
		if(movables.isEmpty())
		{
			board.pass();
			return;
		}
		Point p = null;
		if(movables.size() == 1)
		{
			board.move((Point) movables.get(0));
			return;
		}
		return;
	}

	public Point select_best_move(Board board, Vector can_put2)//打てる手に対して勝率の
	{				 										   //一番高い手を返す
		BookManager book = new BookManager();
		Vector movables = book.find(board);
		int win_sum[] = new int[can_put2.size()];
		int win = 0;
		Point p = null;
		int best_put = 0;
		double best_value = -100;
		double win_rate[] = new double[can_put2.size()];
		Vector z = board.getUpdate();
		System.out.println(z);
		for(;;)
		{
			for(int j = 0; j < can_put2.size(); j++)
			{
				stone[j] = (Disc)can_put2.get(j);
				System.out.print(stone[j] + ",");
			}
			System.out.println("");
			for(int i = 0; i < can_put2.size(); i++)
			{
				for(int m = 0; m < try_num; m++)//プレイアウトを繰り返す
				{
					clone.Turns = 0;
					win = playout(board,m,stone[i]);
					win_sum[i] += win;
				}
				win_rate[i] = (double)win_sum[i] / try_num;//勝率を求める
				System.out.print(", win_sum = " + win_sum[i] + ", win_rate = " + win_rate[i]);
			}
			for(int l = 0; l < can_put2.size(); l++)
			{
				if(win_rate[l] > best_value)//最善手を更新
				{
					best_value = win_rate[l];
					best_put = l;
				}
			}
			break;
		}
		System.out.println();
		System.out.println("(x,y) = " + stone[best_put] + ", best_value = " + best_value + ", try_num = " + try_num);
		return stone[best_put];
	}

	public int playout(Board board, int num, Point put)//プレイアウトをランダムに行う
	{

		BookManager book = new BookManager();
		Vector movables = book.find(clone);
		clone.Turns = 0;
		//clone.CurrentColor = Disc.BLACK;
		all_playouts++;
		System.out.println();
		Board clone = state.clone();//Boardクラスのクローン作製
		//clone.init();
		int win = 0,number = 0;
		for(int d = 0; d < 10; d++)
		{
			for(int e = 0; e < 10; e++)
			{
				clone.RawBoard[e][d] = board.RawBoard[e][d];
				//System.out.printf("%2d ",clone.RawBoard[e][d]);
			/*	if(all_playouts < 10)
				{
					System.out.printf("%2d ",clone.RawBoard[e][d]);
				}*/
			}
			//System.out.println();
		}
		clone.initMovable();
		//System.out.print("候補手 = " + clone.getMovablePos());
		System.out.println("");
		System.out.print(put + ", ");
		//System.out.print(clone.getCurrentColor());
		clone.move(put);
		//System.out.print("候補手 = " + clone.getMovablePos());
		Point p = null;
		//System.out.println();
		while(!clone.isGameOver())//2手目以降の候補手が1手目と同じになっている
		{
			number++;			  //2手目以降に手番が入れ替わってない可能性が高い
			//System.out.print("HELLO");//loopに入ってるか確認用
			/*if((number == 0) && (clone.CurrentColor == -1))//手番が黒からじゃなければ交代
			{
				clone.CurrentColor = -clone.CurrentColor;
			}
			if(number ==1)
			{
				for(int d = 0; d < 10; d++)
				{
					for(int e = 0; e < 10; e++)
					{
						System.out.printf("%2d ",clone.RawBoard[e][d]);
					}
					System.out.println();
				}
				System.out.println();
			}*/
			if(movables.size() >= 2)
			{
				int x = 0,y = 0;
				disc.color = clone.getCurrentColor();
				for(;;)
				{
					Random value1 = new Random();
					Random value2 = new Random();
					disc.x = value1.nextInt(8) + 1;
					disc.y = value2.nextInt(8) + 1;
					if(clone.checkMobility(disc) == clone.NONE)	continue;
					clone.move(disc);
				//	System.out.print(disc + ",");
					/*if(all_playouts > 4)
					{
						System.out.println(disc);
					}*/
					break;
				}
			}
			if(movables.isEmpty())
			{
				clone.pass();
			}
			if(movables.size() == 1)
			{
				clone.move((Point) movables.get(0));
				/*if(all_playouts > 4)
				{
					System.out.println(movables.get(0));
				}*/
			}
		//	Point p = null;
			//System.out.print("候補手 = " + clone.getMovablePos());
			/*if((number < 5) && (all_playouts > 4))
			{
				System.out.print("手番 = " + clone.getCurrentColor());
			}*/
		}
		System.out.println();
		/*for(int d = 0; d < 10; d++)
		{
			for(int e = 0; e < 10; e++)
			{
				System.out.printf("%2d ",clone.RawBoard[e][d]);
			}
			System.out.println();
		}*/
		//System.out.println("Turns = " + clone.Turns);
		difference[num] = clone.countDisc(Disc.BLACK) - clone.countDisc(Disc.WHITE);
		System.out.print("石差 = " + difference[num] + ", " + "手数計 = " + number);
		if(difference[num] > 0)//黒が勝っていれば1
		{
			 win = 1;
		}
		return win;
	}
}