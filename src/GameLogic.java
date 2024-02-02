import java.util.*;

public class GameLogic implements PlayableLogic {
    private ConcretePiece[][] _board;
    private Position[][] _mapPositions;
    private List<ConcretePiece> _allPieces;
    private List<Position> _allPositions;
    private ConcretePlayer _player1, _player2;
    private boolean _isKingSaved = false;
    private boolean _isKingCaptured = false;
    private Position _kingsPosition;
    private boolean _isSecondPlayerTurn;
    public GameLogic() {
        _player1 = new ConcretePlayer(true);
        _player2 = new ConcretePlayer(false);
        reset();
    }
    @Override
    public boolean move(Position a, Position b) {
        ConcretePiece movingPiece = (ConcretePiece)getPieceAtPosition(a);
        //is Move Valid?
        // If Piece Valid of player
        if (movingPiece.getOwner().isPlayerOne() == _isSecondPlayerTurn) {
            return false;
        }
        // If move same spot or diagonal
        if (a.get_x() == b.get_x() && a.get_y() == b.get_y()
                ||
                a.get_x() != b.get_x() && a.get_y() != b.get_y()) {
            return false;
        }
        // if moves to corners
        if(movingPiece instanceof Pawn
                && b.isCorner(getBoardSize())){
            return false;
        }

        // Chek if piece hops other pieces
        if (a.get_x() == b.get_x()) {
            int i = a.get_x();
            int max = Math.max(a.get_y(), b.get_y());
            int min = Math.min(a.get_y(), b.get_y());
            for (int j = min; j <= max; j++) {
                if (_board[i][j] != null && j != a.get_y()) {
                    return false;
                }
            }
        } else if (a.get_y() == b.get_y()) {
            int j = a.get_y();
            int max = Math.max(a.get_x(), b.get_x());
            int min = Math.min(a.get_x(), b.get_x());
            for (int i = min; i <= max; i++) {
                if (_board[i][j] != null && i != a.get_x()) {
                    return false;
                }
            }
        }

        // Valid!!
        //Moving the piece
        _board[a.get_x()][a.get_y()] = null;
        _board[b.get_x()][b.get_y()] = movingPiece;
        //Add Statistics: New Position and distance
        movingPiece.getPieceStatistics().addDist((b.distanceTo(a)));
        movingPiece.getPieceStatistics().getPositionHistory().push(b);

        // Switch play of turns
        if (movingPiece.getOwner().isPlayerOne()) {
            _isSecondPlayerTurn = true;
        } else {
            _isSecondPlayerTurn = false;
        }

        // If Pawns -> Check if Capture Enemy
        if (movingPiece instanceof Pawn){
            capturePawn((Pawn) movingPiece,b);
        }
        // If King -> Update Position
        else {
            _kingsPosition = new Position(b.get_x(), b.get_y());
        }

        // Check if Game is finished -> print Statistics
        if (_kingsPosition.isCorner(getBoardSize())) {
            _isKingSaved = true;
            _player1.addWin();
            printStatistics(_player1);
        }

        King king = (King) getPieceAtPosition(_kingsPosition);
        if (captureKing(king, _kingsPosition)) {
            _isKingCaptured = true;
            _player2.addWin();
            printStatistics(_player2);
        }
        return true;
    }
    /**
     * Function gets a piece on board and its Position and check if it captures and kills enemy
     * @param piece - Current Piece
     * @param p - Piece Position
     */
    public void capturePawn(Pawn piece,Position p){
        // Map pieces around the piece
        //                ?
        //
        //                ?
        //      ?   ?   piece   ?   ?
        //                ?
        //
        //                ?
        //
        Position[] positionsArr = {
                p.getUpperPos(),p.getUpperPos().getUpperPos(),
                p.getLowerPos(),p.getLowerPos().getLowerPos(),
                p.getLeftPos(),p.getLeftPos().getLeftPos(),
                p.getRightPos(),p.getRightPos().getRightPos()};

        for(int i=0;i<positionsArr.length;i=i+2){
            // Get Pieces at positions
            ConcretePiece checkPiece = (ConcretePiece) getPieceAtPosition(positionsArr[i]);
            ConcretePiece checkNextPiece = (ConcretePiece) getPieceAtPosition(positionsArr[i+1]);
            // if empty spot or the piece is king or next to it is king Continue
            if(checkPiece == null || checkPiece instanceof King || checkNextPiece instanceof King){
                continue;
            }
            // If piece next to me is enemy and next to him is corner, Kill and remove enemy from board
            if(!checkPiece.isSameOwner(piece)
                    && positionsArr[i+1].isCorner(getBoardSize())){
                _board[positionsArr[i].get_x()][positionsArr[i].get_y()] = null;
                piece.getPieceStatistics().addKill();
            }
            // If piece next me is enemy and next to him is Outsize of the board, kill and remove enemy from board
            else if(!checkPiece.isSameOwner(piece)
                    && positionsArr[i+1].isOutsizeEdgeOfBoard(getBoardSize())){
                _board[positionsArr[i].get_x()][positionsArr[i].get_y()] = null;
                piece.getPieceStatistics().addKill();
            }
            // If piece next to me is enemy and next to him is my friend kill him and remove enemy from board
            else if(!checkPiece.isSameOwner(piece)
                    && checkNextPiece != null
                    && checkNextPiece.isSameOwner(piece)){
                _board[positionsArr[i].get_x()][positionsArr[i].get_y()] = null;
                piece.getPieceStatistics().addKill();
            }
        }

    }
    /**
     * Function get the king piece and its position and check if enemy has captured it
     * @param king
     * @param p - King's Position
     * @return true if king is captured or false
     */
    public boolean captureKing(King king,Position p){
        // Map pieces around the piece
        //                ?
        //          ?   piece   ?
        //                ?
        //
        Position[] positionsArr = {
                p.getUpperPos(), p.getLowerPos(),
                p.getLeftPos(), p.getRightPos()};

        for(int i=0;i<positionsArr.length;i++){

            ConcretePiece checkPiece = (ConcretePiece) getPieceAtPosition(positionsArr[i]);
            // if the is no piece at one spot and not one spot is not outside the board return false
            if(checkPiece == null && !positionsArr[i].isOutsizeEdgeOfBoard(getBoardSize())){
                return false;
            }
            // if there is one piece that is my friend return false
            if(checkPiece != null && checkPiece.isSameOwner(king)){return false;}
        }

        return true;
    }
    @Override
    public Piece getPieceAtPosition(Position position) {
        // If Piece outsize of bounds return null
        int boardSize = getBoardSize();
        if(position.isOutsizeOfBoard(boardSize) || position.isOutsizeEdgeOfBoard(boardSize)) {
            return null;
        }
        return _board[position.get_x()][position.get_y()];
    }
    @Override
    public Player getFirstPlayer() {
        return _player1;
    }
    @Override
    public Player getSecondPlayer() {
        return _player2;
    }
    @Override
    public boolean isGameFinished() {return _isKingCaptured || _isKingSaved;
    }
    @Override
    public boolean isSecondPlayerTurn() {
        return _isSecondPlayerTurn;
    }
    @Override
    public void reset() {
        // Create new board matrix
        _board = new ConcretePiece[getBoardSize()][getBoardSize()];
        // Create map of Positions to save all the Positions Pointers
        _mapPositions = new Position[getBoardSize()][getBoardSize()];
        // Create List to save all Pieces Pointers
        _allPieces = new ArrayList<ConcretePiece>(13 + 24);
        // Declare list of positions that will hold all positions that more than one pieces stepped on
        _allPositions = new ArrayList<>();
        _isSecondPlayerTurn = true;
        _isKingSaved = false;
        _isKingCaptured = false;

        // Create board and add to list of all pieces
        _board[5][3] = new Pawn(_player1);
        _allPieces.add(0, _board[5][3]);
        _board[4][4] = new Pawn(_player1);
        _allPieces.add(1, _board[4][4]);
        _board[5][4] = new Pawn(_player1);
        _allPieces.add(2, _board[5][4]);
        _board[6][4] = new Pawn(_player1);
        _allPieces.add(3, _board[6][4]);
        _board[3][5] = new Pawn(_player1);
        _allPieces.add(4, _board[3][5]);
        _board[4][5] = new Pawn(_player1);
        _allPieces.add(5, _board[4][5]);
        _board[5][5] = new King(_player1);
        _allPieces.add(6, _board[5][5]);
        _kingsPosition = new Position(5,5);
        _board[6][5] = new Pawn(_player1);
        _allPieces.add(7, _board[6][5]);
        _board[7][5] = new Pawn(_player1);
        _allPieces.add(8, _board[7][5]);
        _board[4][6] = new Pawn(_player1);
        _allPieces.add(9, _board[4][6]);
        _board[5][6] = new Pawn(_player1);
        _allPieces.add(10, _board[5][6]);
        _board[6][6] = new Pawn(_player1);
        _allPieces.add(11, _board[6][6]);
        _board[5][7] = new Pawn(_player1);
        _allPieces.add(12, _board[5][7]);

        _board[3][0] = new Pawn(_player2);
        _allPieces.add(13, _board[3][0]);
        _board[4][0] = new Pawn(_player2);
        _allPieces.add(14, _board[4][0]);
        _board[5][0] = new Pawn(_player2);
        _allPieces.add(15, _board[5][0]);
        _board[6][0] = new Pawn(_player2);
        _allPieces.add(16, _board[6][0]);
        _board[7][0] = new Pawn(_player2);
        _allPieces.add(17, _board[7][0]);
        _board[5][1] = new Pawn(_player2);
        _allPieces.add(18, _board[5][1]);
        _board[0][3] = new Pawn(_player2);
        _allPieces.add(19, _board[0][3]);
        _board[10][3] = new Pawn(_player2);
        _allPieces.add(20, _board[10][3]);
        _board[0][4] = new Pawn(_player2);
        _allPieces.add(21, _board[0][4]);
        _board[10][4] = new Pawn(_player2);
        _allPieces.add(22, _board[10][4]);
        _board[0][5] = new Pawn(_player2);
        _allPieces.add(23, _board[0][5]);
        _board[1][5] = new Pawn(_player2);
        _allPieces.add(24, _board[1][5]);
        _board[9][5] = new Pawn(_player2);
        _allPieces.add(25, _board[9][5]);
        _board[10][5] = new Pawn(_player2);
        _allPieces.add(26, _board[10][5]);
        _board[0][6] = new Pawn(_player2);
        _allPieces.add(27, _board[0][6]);
        _board[10][6] = new Pawn(_player2);
        _allPieces.add(28, _board[10][6]);
        _board[0][7] = new Pawn(_player2);
        _allPieces.add(29, _board[0][7]);
        _board[10][7] = new Pawn(_player2);
        _allPieces.add(30, _board[10][7]);
        _board[5][9] = new Pawn(_player2);
        _allPieces.add(31, _board[5][9]);
        _board[3][10] = new Pawn(_player2);
        _allPieces.add(32, _board[3][10]);
        _board[4][10] = new Pawn(_player2);
        _allPieces.add(33, _board[4][10]);
        _board[5][10] = new Pawn(_player2);
        _allPieces.add(34, _board[5][10]);
        _board[6][10] = new Pawn(_player2);
        _allPieces.add(35, _board[6][10]);
        _board[7][10] = new Pawn(_player2);
        _allPieces.add(36, _board[7][10]);

        // Set names to pieces for statistics
        for(int i=0;i<13;i++){
            if(i==6){_allPieces.get(i).setName("K" + (i+1));}
            else {_allPieces.get(i).setName("D" + (i + 1));}
        }

        for(int i=13;i<37;i++){
            _allPieces.get(i).setName("A" + (i-12));
        }
        int m=0;
        for(int i=0;i<_board.length;i++){
            for (int j=0;j<_board[i].length;j++){
                // add all positions to map
                _mapPositions[i][j] = new Position(i,j);
                if(_board[i][j] != null){
                    // add to position history of each piece his starting position
                    _board[i][j].getPieceStatistics().getPositionHistory().push(new Position(i,j));
                }
            }
        }
    }
    @Override
    public void undoLastMove() {// Not Implemented
        return;}
    @Override
    public int getBoardSize() {
        return 11;
    }
    /**
     * Function gets a winner and prints the right order of statistics of the game according to the winner
     * @param winner
     */
    public void printStatistics(Player winner){
        // Sort the pieces by comperator
        Collections.sort(_allPieces,new Statistics.PieceStatistics.PositionHistoryComp(winner));
        for (ConcretePiece piece : _allPieces) {
            Stack<Position> piecePositionHistory = piece.getPieceStatistics().getPositionHistory();
            // if PositionHistory size is smaller the 2 don't print
            if(piecePositionHistory.size() > 1) {
                System.out.println(piece.getName() + ": " + piecePositionHistory);
                // Get a set of all positions piece has stepped on
                List<Position> positionsSet = piece.getPieceStatistics().getPositionHistorySet();
                // for each position add to counter
                for(int i=0 ;i<positionsSet.size();i++){
                    Position current = positionsSet.get(i);
                    _mapPositions[current.get_x()][current.get_y()].getPositionStatistics().addCounter();
                }
            }
            else {
                // if not moved add to counter also
                Position singlePos = piecePositionHistory.get(0);
                _mapPositions[singlePos.get_x()][singlePos.get_y()].getPositionStatistics().addCounter();
            }

        }

        System.out.println(("***************************************************************************"));
        // Sort all Pieces list by Kill comperator
        Collections.sort(_allPieces,new Statistics.PieceStatistics.KillsComp(winner).reversed());
        for (ConcretePiece piece : _allPieces) {
            // print if number of kills is gearter then 0
            if(piece.getPieceStatistics().getKills()>0) {
                System.out.println(piece.getName() + ": " + piece.getPieceStatistics().getKills() + " kills");
            }
        }

        System.out.println(("***************************************************************************"));
        // Sort all Pieces list by Distance comperator
        Collections.sort(_allPieces, new Statistics.PieceStatistics.DistComp(winner).reversed());
        for (ConcretePiece piece : _allPieces) {
            // print if piece moved
            if(piece.getPieceStatistics().getDist() > 0) {
                System.out.println(piece.getName() + ": " + piece.getPieceStatistics().getDist() + " squares");
            }
        }

        System.out.println(("***************************************************************************"));

        for(int i=0;i<_mapPositions.length;i++){
            for(int j=0;j<_mapPositions[i].length;j++){
                // Add position from map positions to all positions if more then 1 piece stepped on position
                if(_mapPositions[i][j].getPositionStatistics().getCounter()>1){
                    _allPositions.add(_mapPositions[i][j]);
                }
            }
        }
        // Sort all Positions list by number of pieces stepped comperator
        Collections.sort(_allPositions,new Statistics.PositionStatistics.PositionComp());
        for (Position pos : _allPositions) {
            System.out.println(pos.toString() + pos.getPositionStatistics().getCounter() + " pieces");
        }

        System.out.println(("***************************************************************************"));


    }
}




