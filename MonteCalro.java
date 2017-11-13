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

	int all_playouts = 0,turn = 0,total_turn = 0,x,y;
	Disc stone[] = new Disc[20];
	Vector can_put = new Vector();
	int try_num = 1;//playout回数
	int difference[] = new int[try_num];//playoutの結果(score)を保持する
	Board state = new Board();//Boardクラスのオブジェクト作成
	Disc disc = new Disc();
	Point p = null;

	public void move(Board board)//打てる手が複数あればモンテカルロ木探索を行う
	{
		can_put = board.getMovablePos();
		BookManager book = new BookManager();
		Vector movables = book.find(board);
		switch(movables.size())
		{
			case 0:
				board.pass();
				break;
			case 1:
				board.move((Point) movables.get(0));
				break;
			default:
				board.move(select_best_move(board,can_put));
				break;
		}
		return;
	}

	public Point select_best_move(Board board, Vector can_put2)//打てる手に対して勝率の
	{				 										   //一番高い手を返す
		BookManager book = new BookManager();
		Vector movables = book.find(board);
		int win_sum[] = new int[can_put2.size()];
		int win = 0;
		int best_put = 0;
		double best_value = 0;
		double win_rate[] = new double[can_put2.size()];
		System.out.print("候補手:");
		for(int j = 0; j < can_put2.size(); j++)
		{
			stone[j] = (Disc)can_put2.get(j);
			System.out.print(stone[j] + ",");
		}
	//	for(;;)
		//{
			//System.out.println("");
			for(int i = 0; i < can_put2.size(); i++)
			{
				for(int m = 0; m < try_num; m++)//プレイアウトを繰り返す
				{
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
			//break;
		//}
		System.out.println();
		System.out.println("(x,y) = " + stone[best_put] + ", best_value = " + best_value + ", try_num = " + try_num);
		return stone[best_put];
	}

	public int playout(Board board, int num, Point put)//プレイアウトをランダムに行う
	{
		Board clone = state.clone();//Boardクラスのクローン作製
		BookManager book = new BookManager();
		Vector movables = book.find(clone);
		all_playouts++;
		System.out.println();
		int win = 0,number = 0;
		for(int d = 0; d < 10; d++)
		{
			for(int e = 0; e < 10; e++)
			{
				clone.RawBoard[e][d] = board.RawBoard[e][d];
			}
		}
		clone.initMovable();
		//System.out.println("候補手:" + clone.getMovablePos());
		System.out.print(put + ", ");
		clone.move(put);
		total_turn = board.Turns;
		while(59 - total_turn != number)
		{
			movables = book.find(clone);
			switch(movables.size())
			{
				case 0:
					clone.pass();
					break;
				case 1:
					clone.move((Point) movables.get(0));
					//if((number + 1 == 59 - total_turn))
					//{
						/*System.out.println((Point) movables.get(0));
						for(int d = 0; d < 10; d++)
						{
							for(int e = 0; e < 10; e++)
							{
								System.out.printf("%2d ",clone.RawBoard[e][d]);
							}
							System.out.println();
						}*/
					//}
					break;
				default:
					 	x = 0;
						y = 0;
						disc.color = clone.getCurrentColor();
						for(;;)
						{
							Random value1 = new Random();
							Random value2 = new Random();
							disc.x = value1.nextInt(8) + 1;
							disc.y = value2.nextInt(8) + 1;
							if(clone.checkMobility(disc) == clone.NONE)	continue;
							clone.move(disc);
							//if((number + 1 == 59 - total_turn))
							//{
								/*System.out.println(disc);
								for(int d = 0; d < 10; d++)
								{
									for(int e = 0; e < 10; e++)
									{
										System.out.printf("%2d ",clone.RawBoard[e][d]);
									}
									System.out.println();
								}*/
							//}
							break;
						}
					break;
			}
			number++;
			//System.out.print(number + ",");
		}
		difference[num] = clone.countDisc(Disc.BLACK) - clone.countDisc(Disc.WHITE);
		System.out.print("石差 = " + difference[num] + ", " + "手数計 = " + number);
		if(difference[num] >= 0)//黒が勝っていれば1
		{
			 win = 1;
		}
		return win;
	}
}
/*
	//playout関数無限ループ使わないver(上手くいってない)
	Vector put_stone = new Vector();
	Disc put1[] = new Disc[20];
	put_stone = clone.getMovablePos();
	for(int j = 0; j < put_stone.size(); j++)
	{
		put1[j] = (Disc)put_stone.get(j);
	}
	x = 0;
	Random value = new Random();
	x = value.nextInt(put_stone.size());
	clone.move((Point)put1[x]);



	public double select_best_uct()
	{
		if(
	}

	public int create_node()
	{

	}
	public void add_child()
	{

	}
	public double serach_uct()
	{

	}
*/