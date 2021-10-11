import java.util.List;
import java.util.ArrayList;

public class Go {
	private List<Integer> piecesToDelete;
	private String[][] board;
	private String[][] copyBoard;
	private String[][] copyBoardKo;
	private boolean pass;
	private boolean consecPass;
	private boolean hitsEmpty;
	private boolean hitsBlack;
	private boolean hitsWhite;
	private int points;
	private int whitePoints;
	private int blackPoints;
	private boolean komi;
	private int handicap;
	private boolean alreadyOccupied;
	private List<Integer> prevBlack;
	private List<Integer> prevWhite;
	private List<Integer> currentBoard;
	private boolean ko;
	private String rules;
	private int blackCaptured;
	private int whiteCaptured;
	private String turn;
	private boolean suicide;

	//places denoted by x and y values (1 ~ 19), 0's are border squares
	public Go() {
		this.board = new String[21][21];
		for(int i = 0; i < 21; i++){
			board[0][i] = "border";
			board[20][i] = "border";
			board[i][0] = "border";
			board[i][20] = "border";
		}
		for(int i = 1; i < 20; i++){
			for(int j = 1; j < 20; j++){
				board[i][j] = "empty";
			}
		}

		this.copyBoard = new String[21][21];
		this.copyBoardKo = new String[21][21];
		this.piecesToDelete = new ArrayList<Integer>();
		this.hitsEmpty = false;
		this.pass = false;
		this.consecPass = false;
		this.points = 0;
		this.hitsBlack = false;
		this.hitsWhite = false;
		this.whitePoints = 0;
		this.blackPoints = 0;
		this.komi = false;
		this.handicap = 0;
		this.alreadyOccupied = false;
		this.prevBlack = new ArrayList<Integer>();
		this.prevWhite = new ArrayList<Integer>();
		this.currentBoard = new ArrayList<Integer>();
		this.ko = false;

	}
	
	public void setRules(String rules) {
		this.rules = rules;
	}


	public void komi(boolean bool){
		this.komi = bool;
	}

	public void handicap(int n){
		this.handicap = n - 1;
	}

	public int returnHandicap(){
		return this.handicap;
	}

	public boolean alreadyOccupied(){
		return alreadyOccupied;
	}

	public void resetOccupied(){
		alreadyOccupied = false;
	}

	public void pass(){
		if(!pass){
			pass = true;
		} else {
			consecPass = true;
		}
	}

	public boolean consecPasses(){
		return this.consecPass;
	}

	public void clearPasses(){
		this.pass = false;
		this.consecPass = false;
	}

	public void placePoint(String type, int xvalue, int yvalue){
		copyBoardKo[yvalue][xvalue] = type;
	}

	public void removePieces(int xvalue, int yvalue){
		board[yvalue][xvalue] = "empty";
	}

	public void copyBoardKo(){
		for(int row = 0; row < 21; row++){
			for(int col = 0; col < 21; col++){
				copyBoardKo[row][col] = board[row][col];
			}
		}
	}

	public void copyBoardBack(){
		for(int row = 0; row < 21; row++){
			for(int col = 0; col < 21; col++){
				board[row][col] = copyBoardKo[row][col];
			}
		}
	}

	public boolean ko(){
		return this.ko;
	}

	public void storeKo(String type){
		if(type.equals("black")){
			prevBlack.clear();
			for(int row = 1; row < 20; row++){
				for(int col = 1; col < 20; col++){
					if(board[row][col].equals("black")){
						prevBlack.add(row*100 + col);
					}
				}
			}
		} else if(type.equals("white")){
			prevWhite.clear();
			for(int row = 1; row < 20; row++){
				for(int col = 1; col < 20; col++){
					if(board[row][col].equals("white")){
						prevWhite.add(row*100 + col);
					}
				}
			}
		}
	}
	
	//to check for ko, save recent copy of each color's piece positions, and then compare
	public boolean ko(String type){
		currentBoard.clear();
		if(type.equals("black")){
			for(int row = 1; row < 20; row++){
				for(int col = 1; col < 20; col++){
					if(copyBoardKo[row][col].equals("black")){
						currentBoard.add(row*100 + col);
					}
				}
			}
			if(currentBoard.equals(prevBlack)){
				return true;
			} else {
				return false;
			}
		} else if (type.equals("white")){
			for(int row = 1; row < 20; row++){
				for(int col = 1; col < 20; col++){
					if(copyBoardKo[row][col].equals("white")){
						currentBoard.add(row*100 + col);
					}
				}
			}
			if(currentBoard.equals(prevWhite)){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}


	/* 
	  check opponents' pieces first before your own
	  copy the board so flood fill doesn't affect what it shows
	  ex. checking white:
	  pick a white piece
	  use the flood fill algorithm to check if it hits an empty square- so:
	  if it does, end the method there
	  if it hits another white piece
	  add the white pieces to an arraylist of indices to delete (instance field probably)
	  recurse
	  if any of the white pieces hit an empty square, clear the list and end the method
	  if not, wait until the recursion stops (no more white pieces in that cluster left that haven't already been checked) and then delete the pieces in the arraylist

	  repeat with all white pieces, and then with all black
	 */

	public void blackTurn(int xvalue, int yvalue){
		pass = false;
		ko = false;
		alreadyOccupied = false;
		suicide = false;
		if (board[yvalue][xvalue].equals("empty")){
			//make move on copied board first, and check for ko. Then move on the real board if applicable
			copyBoardKo();
			placePoint("black", xvalue, yvalue);
			this.turn = "white";
			check("white");
			this.turn = "black";
			check("black");
			if(ko("black")){
				this.ko = true;
			} else if (hasLiberties(xvalue, yvalue) && rules.equals("Chinese")){
				this.suicide = true;
			} else {
				copyBoardBack();
				storeKo("black");
			}
		} else {
			alreadyOccupied = true;
		}
	}

	public void whiteTurn(int xvalue, int yvalue){
		pass = false;
		ko = false;
		alreadyOccupied = false;
		suicide = false;
		if (board[yvalue][xvalue].equals("empty")){
			copyBoardKo();
			placePoint("white", xvalue, yvalue);
			this.turn = "black";
			check("black");
			this.turn = "white";
			check("white");
			if(ko("white")){
				this.ko = true;
			} else if (hasLiberties(xvalue, yvalue) && rules.equals("Chinese")){
				this.suicide = true;
			} else {
				copyBoardBack();
				storeKo("white");
			}
		} else {
			alreadyOccupied = true;
		}
	}

	public void copyBoard(){
		for(int row = 0; row < 21; row++){
			for(int col = 0; col < 21; col++){
				copyBoard[row][col] = copyBoardKo[row][col];
			}
		}
	}
	
	//returns false if the piece just put down has no liberties by the time all the dead stones are removed
	public boolean hasLiberties(int x, int y) {
		if(copyBoardKo[y + 1][x].equals("empty")) {
			return true;
		} else if(copyBoardKo[y - 1][x].equals("empty")) {
			return true;
		} else if(copyBoardKo[y][x + 1].equals("empty")) {
			return true;
		} else if(copyBoardKo[y][x - 1].equals("empty")) {
			return true;
		}
		copyBoardKo[y][x] = "empty";
		return false;
	}

	public void check(String type){
		copyBoard();
		for(int x = 1; x < 20; x++){
			for(int y = 1; y < 20; y++){
				if (copyBoard[y][x].equals(type)){
					floodFill(y, x, type, "checked");
					if (hitsEmpty){
						piecesToDelete.clear();
					} else{
						//delete those pieces
						for(int i = 0; i < piecesToDelete.size(); i++){
							copyBoardKo[piecesToDelete.get(i) / 100][piecesToDelete.get(i) % 100] = "empty";
						}
						if(rules.equals("Chinese")) {
							if(turn.equals("black")) {
								blackCaptured += piecesToDelete.size();
							} else if(turn.equals("white")) {
								whiteCaptured += piecesToDelete.size();
							}
						}
						piecesToDelete.clear();
					}
					hitsEmpty = false;
				}
			}
		}
	}

	public void floodFill(int y, int x, String type, String checked){
		if (copyBoard[y][x].equals("empty")){
			hitsEmpty = true;
			return;
		} else if (!copyBoard[y][x].equals(type)){
			return;
		} else {
			copyBoard[y][x] = checked;
			piecesToDelete.add(y*100 + x);
		}
		floodFill(y + 1, x, type, checked);
		floodFill(y, x + 1, type, checked);
		floodFill(y - 1, x, type, checked);
		floodFill(y, x - 1, type, checked);
	}
	
	//display total score
	/*
	Make a copy of the board
	Search for an empty point
	Use flood fill algorithm on the empty points to turn them "marked"
	Count the number of black/white/border spaces it bumps up against
	If the count at the end is all black + border or all white + border, those are that player's points
	If the flood fill runs up against both black and white, point count resets to zero
	Repeat until all empty points on the board are marked

	If komi +7 to white count
	*/

	public void countPoints(){
		copyBoardKo();
		if(komi){
			whitePoints += 7;
		}
		for (int row = 1; row < 20; row++){
			for (int col = 1; col < 20; col++){
				if (copyBoardKo[row][col].equals("empty")){
					findPoints(row, col);
					if(hitsBlack && !hitsWhite){
						blackPoints += points;
					} else if (!hitsBlack && hitsWhite){
						whitePoints += points;
					}
					hitsBlack = false;
					hitsWhite = false;
					points = 0;
				}
			}
		}
		if(rules.equals("Chinese")) {
			blackPoints += whiteCaptured;
			whitePoints += blackCaptured;
		}
	}

	public void findPoints(int row, int col){
		if (copyBoardKo[row][col].equals("border")){
			return;
		} else if (copyBoardKo[row][col].equals("black")){
			hitsBlack = true;
			return;
		} else if (copyBoardKo[row][col].equals("white")){
			hitsWhite = true;
			return;
		} else if (!copyBoardKo[row][col].equals("empty")){
			return;
		} else {
			copyBoardKo[row][col] = "checked";
			points++;
		}
		findPoints(row + 1, col);
		findPoints(row, col + 1);
		findPoints(row - 1, col);
		findPoints(row, col - 1);
	}

	public int blackPoints(){
		return blackPoints;
	}

	public int whitePoints(){
		return whitePoints;
	}

	public String[][] board(){
		return board;
	}
}
