package FileLog.DLsite;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class DLsiteDB {

	// jdbc fields
	static Connection connection;
	static PreparedStatement pst;
	static final String DB_URL = "jdbc:mysql://localhost:3306/DLsite";
	static final String username = "Jguest";
	static final String password = "java";

	// program fields
	static Scanner sc = new Scanner(System.in);
	static String type;
	static String id;
	static String title;
	static String author;
	static String extraInfo = "";
	static String parentFileID = "";
	static boolean endProgram = false;
	static final String QUIT = "q";

	// DataBase fields
	static final String ASMR = "音声作品";
	static final String BOOK = "電子書籍";
	static final String MOVIE = "動画";
	static final String GAME = "ゲーム";
	static final String GAME_PATCH = "ゲームパッチ・その他";
	static final String DOUJIN = "同人誌・CG";

	// table fields
	static final String ID = "作品番号";
	static final String TITLE = "作品タイトル";
	static final String AUTHOR = "サークル名";
	static final String EXTRA_INFO = "補足";
	static final String PARENT = "親ファイル";

	public static void createMySQLConnection() {

		System.out.println("データベース\"DLsite\"へ接続しています...");
		try {
			connection = DriverManager.getConnection(DB_URL, username, password);
			pst = connection.prepareStatement("");
			System.out.println("データベース\"DLsite\"へ接続しました。");
		} catch (SQLException e) {
			System.out.println("接続が失敗しました。データベースの存在及び名称、ユーザ名とパスワードを確認してください。");
		}
	}

	public static void getInfo() {
		boolean usrConfirmedData = false;

		while (!usrConfirmedData) {
			getType();

			// user stops entering information
			if (type.equals(QUIT)) {
				return;
			}

			getID();
			getTitle();
			getAuthor();

			if (type.equals(GAME)) {
				getExtraInfo();
			}

			if (type.equals(GAME_PATCH)) {
				getParentID();
				getExtraInfo();
			}

			// confirm input
			printInputData();
			String usrConfirm = sc.nextLine().toLowerCase();
			if (!usrConfirm.equals("n")) {
				usrConfirmedData = true;
			} else {
				System.out.println("ご入力した資料を放棄します。もう一度資料を入力してください。");
			}
		}
	}

	public static void getType() {
		boolean typeValid = false;
		System.out.println(
				"作品種類を入力してください： \n[1] 音声作品\n[2] 電子書籍\n[3] 動画\n[4] ゲーム\n[5] ゲームパッチ・その他\n[6] 同人誌・CG\n[Q]プログラムを終了する");
		while (!typeValid) {
			String usrType = sc.nextLine().trim().toLowerCase();
			switch (usrType) {
			case "1":
				type = ASMR;
				typeValid = true;
				break;
			case "2":
				type = BOOK;
				typeValid = true;
				break;
			case "3":
				type = MOVIE;
				typeValid = true;
				break;
			case "4":
				type = GAME;
				typeValid = true;
				break;
			case "5":
				type = GAME_PATCH;
				typeValid = true;
				break;
			case "6":
				type = DOUJIN;
				typeValid = true;
				break;
			case QUIT:
				type = QUIT;
				return;
			default:
				System.out.println(
						"無効の入力です。もう一度作品種類を入力してください： \n[1] 音声作品\n[2] 電子書籍\n[3] 動画\n[4] ゲーム\n[5] ゲームパッチ・その他\n[6] 同人誌・CG\n[Q]プログラムを終了する");
			}
		}
	}

	public static void getID() {
		boolean validID = false;
		String ifInvalid;
		while (!validID) {
			System.out.println("作品番号を入力してください：");
			id = sc.nextLine().trim();
			validID = true;
			if (id.length() == 0) {
				validID = false;
				continue;
			} else if (id.length() != 8) {
				System.out.println("ご入力された作品番号の長さは予想された長さと異なっています。続きますか？(Y/n)");
				ifInvalid = sc.nextLine().toLowerCase();
				if (ifInvalid.equals("n")) {
					validID = false;
				}
			}
		}

	}

	public static void getTitle() {
		boolean validTitle = false;
		while (!validTitle) {
			System.out.println("作品タイトルを入力してください：");
			title = sc.nextLine().trim();
			validTitle = true;
			if (title.length() == 0) {
				validTitle = false;
			}
		}
	}

	public static void getAuthor() {
		boolean validAuthor = false;
		while (!validAuthor) {
			System.out.println("作品のサークル名を入力してください：");
			author = sc.nextLine().trim();
			validAuthor = true;
			if (author.length() == 0) {
				validAuthor = false;
			}
		}
	}

	public static void getParentID() {
		System.out.println("親ファイルの番号入力してください。なかったら、Enterを押してください：");
		parentFileID = sc.nextLine().trim();
	}

	public static void getExtraInfo() {
		System.out.println("補足を追加したければ、入力してください。なかったら、Enterを押してください：");
		extraInfo = sc.nextLine().trim();
	}

	public static void printInputData() {
		System.out.println(
				"以下の資料でよろしいのでしょうか？(Y/n)\n作品種類：" + type + "\n作品番号：" + id + "\n作品番号：" + title + "\nサークル名：" + author);

		if (type.equals(GAME_PATCH)) {
			if (parentFileID.length() != 0) {
				System.out.println("親ファイル：" + parentFileID);
			} else {
				System.out.println("親ファイル：なし");
			}
		}

		if ((type.equals(GAME) || type.equals(GAME_PATCH))) {
			if (extraInfo.length() != 0) {
				System.out.println("補足：" + extraInfo);
			} else {
				System.out.println("補足：なし");
			}
		}

	}

	public static void insertData() {
		try {
			System.out.println("作品資料を保存しています...");

			String command = "INSERT INTO " + type + "( 作品番号, 作品タイトル, サークル名 ) VALUES( ?, ?, ? );";
			String gameCommand = "INSERT INTO " + type + " ( 作品番号, 作品タイトル, サークル名, 補足  ) VALUES( ?, ?, ?, ? );";
			String gamePatchCommand = "INSERT INTO " + type
					+ " ( 作品番号, 作品タイトル, サークル名, 補足, 親ファイル ) VALUES( ?, ?, ?, ?, ? );";

			if (type.equals(GAME)) {
				pst = connection.prepareStatement(gameCommand);
				if (extraInfo.length() == 0) {
					pst.setString(4, null);
				} else {
					pst.setString(4, extraInfo);
				}
			} else if (type.equals(GAME_PATCH)) {
				pst = connection.prepareStatement(gamePatchCommand);
				if (extraInfo.length() == 0) {
					pst.setString(4, null);
				} else {
					pst.setString(4, extraInfo);
				}
				if (parentFileID.length() == 0) {
					pst.setString(5, null);
				} else {
					pst.setString(5, parentFileID);
				}
			} else {
				pst = connection.prepareStatement(command);
			}
			pst.setString(1, id);
			pst.setString(2, title);
			pst.setString(3, author);
			pst.executeUpdate();
			System.out.println("作品資料を保存できした。");
		} catch (SQLException e) {
			System.out.println("作品資料の保存は失敗しました。");
			e.printStackTrace();
		}

	}

	public static void EndProgramPrompt() {
		System.out.println("引き続きに他の作品資料を入力しますか？(Y/n)");
		String usrDecision = sc.nextLine().toLowerCase();
		if (usrDecision.equals("n")) {
			endProgram = true;
		}
	}

	public static void printDB() {
		try {
			File file = new File("./DLsite_branch.txt");
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			String[] tables = { ASMR, BOOK, MOVIE, GAME, GAME_PATCH, DOUJIN };
			System.out.println("データベースの資料を書き込み中...");
			for (String table : tables) {
				ResultSet branch = pst.executeQuery("SELECT * FROM " + table + " ORDER BY " + ID + ";");
				writer.println("[" + table + "]");
				while (branch.next()) {
					if (table.equals(GAME_PATCH)) {
						String parent = branch.getString(PARENT);
						if (!(parent == null)) {
							writer.print("{" + parent + "}");
						}
					}
					writer.print(branch.getString(ID) + "　" + branch.getString(TITLE) + "　[" + branch.getString(AUTHOR)
							+ "]\n");
					if (table.equals(GAME) || table.equals(GAME_PATCH)) {
						String info = branch.getString(EXTRA_INFO);
						if (!(info == null)) {
							writer.print("\t" + info + "\n");
						}
					}

				}
				writer.println();
			}
			writer.flush();
			writer.close();
			System.out.println("現在の所在フォルダにファイル\"DLsite_branch.txt\"を作成しました。");
		} catch (FileNotFoundException e) {
			System.out.println("ファイルの作成及び書き込みに問題が発生しました。");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("UTF-8をサポートされていません。");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("データベースへの接触に問題が発生しました。");
			e.printStackTrace();
		}
	}

	public static void endProgram() {
		System.out.println("プログラムを終了します...");
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("データベースへの接触に問題が発生しました。");
			e.printStackTrace();
		}
		sc.close();
		System.out.println("プログラムを終了しました。お使い頂きありがとうございました。");
	}

	public static void main(String[] args) {
		createMySQLConnection();

		while (!endProgram) {
			getInfo();
			if (type.equals(QUIT)) {
				break;
			}
			insertData();
			EndProgramPrompt();
		}

		printDB();
		endProgram();
	}

}