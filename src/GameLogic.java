import java.util.*;

public class GameLogic implements PlayableLogic {
    private ConcretePiece[][] _board;
    private Position[][] _mapPositions;
    private List<ConcretePiece> _allPieces;
    private List<Position> _allPositions;
    private ConcretePlayer _player1, _player2;
    private boolean _isKingSaved = false;
    private boolean _isKingCaptured = false;
    private Position _kingPosition;
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
        //_mapPositions[b.get_x()][b.get_y()].getPositionStatistics().addCounter();
        // Switch play of turns
        if (movingPiece.getOwner().isPlayerOne()) {
            _isSecondPlayerTurn = true;
        } else {
            _isSecondPlayerTurn = false;
        }

        // If Pawns -> Capture Enemy , If King and its corner -> King is saved
        if (movingPiece instanceof Pawn){
            capturePawn((Pawn) movingPiece,b);
        }
        else {
            _kingPosition = new Position(b.get_x(), b.get_y());
            if (_kingPosition.isCorner(getBoardSize())) {
                _isKingSaved = true;
                _player1.addWin();
                printStatistics(_player1);
            }
        }

        King king = (King) getPieceAtPosition(_kingPosition);
        if (captureKing(king, _kingPosition)) {
            _isKingCaptured = true;
            _player2.addWin();
            printStatistics(_player2);
        }
        return true;
    }
    public void capturePawn(Pawn piece,Position p){
        // Map pieces around the piece
        Position[] positionsArr = {
                p.getUpperPos(),p.getUpperPos().getUpperPos(),
                p.getLowerPos(),p.getLowerPos().getLowerPos(),
                p.getLeftPos(),p.getLeftPos().getLeftPos(),
                p.getRightPos(),p.getRightPos().getRightPos()};

        for(int i=0;i<positionsArr.length;i=i+2){

            ConcretePiece checkPiece = (ConcretePiece) getPieceAtPosition(positionsArr[i]);
            ConcretePiece checkNextPiece = (ConcretePiece) getPieceAtPosition(positionsArr[i+1]);

            if(checkPiece == null || checkPiece instanceof King || checkNextPiece instanceof King){
                continue;
            }

            if(!checkPiece.isSameOwner(piece)
                    && positionsArr[i+1].isCorner(getBoardSize())){
                _board[positionsArr[i].get_x()][positionsArr[i].get_y()] = null;
                piece.getPieceStatistics().addKill();
            }

            else if(!checkPiece.isSameOwner(piece)
                    && positionsArr[i+1].isOnEdgeOfBoard(getBoardSize())){
                _board[positionsArr[i].get_x()][positionsArr[i].get_y()] = null;
                piece.getPieceStatistics().addKill();
            }

            else if(!checkPiece.isSameOwner(piece)
                    && checkNextPiece != null
                    && checkNextPiece.isSameOwner(piece)){
                _board[positionsArr[i].get_x()][positionsArr[i].get_y()] = null;
                piece.getPieceStatistics().addKill();
            }
        }

    }
    public boolean captureKing(King king,Position p){
        Position[] positionsArr = {
                p.getUpperPos(), p.getLowerPos(),
                p.getLeftPos(), p.getRightPos()};

        for(int i=0;i<positionsArr.length;i++){

            ConcretePiece checkPiece = (ConcretePiece) getPieceAtPosition(positionsArr[i]);

            if(checkPiece == null && !positionsArr[i].isOnEdgeOfBoard(getBoardSize())){
                return false;
            }
            if(checkPiece != null && checkPiece.isSameOwner(king)){return false;}
        }

        return true;
    }
    @Override
    public Piece getPieceAtPosition(Position position) {
        // If Piece outsize of bounds return null
        int boardSize = getBoardSize();
        if(position.isOutsizeOfBoard(boardSize) || position.isOnEdgeOfBoard(boardSize)) {
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
        _board = new ConcretePiece[getBoardSize()][getBoardSize()];
        _mapPositions = new Position[getBoardSize()][getBoardSize()];
        _allPieces = new ArrayList<ConcretePiece>(13 + 24);
        _allPositions = new ArrayList<>();
        _isSecondPlayerTurn = true;
        _isKingSaved = false;
        _isKingCaptured = false;


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
        _kingPosition = new Position(5,5);
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
                _mapPositions[i][j] = new Position(i,j);
                if(_board[i][j] != null){
                    //_mapPositions[i][j].getPositionStatistics().addCounter();
                    _board[i][j].getPieceStatistics().getPositionHistory().push(new Position(i,j));
                }
            }
        }
    }
    @Override
    public void undoLastMove() {

        return;
    }
    @Override
    public int getBoardSize() {
        return 11;
    }
    public void printStatistics(Player winner){
        Collections.sort(_allPieces,new Statistics.PieceStatistics.PositionHistoryComp(winner));
        for (ConcretePiece piece : _allPieces) {
            if(piece.getPieceStatistics().getPositionHistory().size() > 1) {
                System.out.println(piece.getName() + ": " + piece.getPieceStatistics().getPositionHistory().toString());
                List<Position> positionsSet = piece.getPieceStatistics().getPositionHistorySet();
                for(int i=0 ;i<positionsSet.size();i++){
                    Position current = positionsSet.get(i);
                    _mapPositions[current.get_x()][current.get_y()].getPositionStatistics().addCounter();
                }
            }
            else {
                Position singlePos = piece.getPieceStatistics().getPositionHistory().get(0);
                _mapPositions[singlePos.get_x()][singlePos.get_y()].getPositionStatistics().addCounter();
            }

        }

        System.out.println(("***************************************************************************"));

        Collections.sort(_allPieces,new Statistics.PieceStatistics.KillsComp(winner).reversed());
        for (ConcretePiece piece : _allPieces) {
            if(piece.getPieceStatistics().getKills()>0) {
                System.out.println(piece.getName() + ": " + piece.getPieceStatistics().getKills() + " kills");
            }
        }

        System.out.println(("***************************************************************************"));

        Collections.sort(_allPieces, new Statistics.PieceStatistics.DistComp(winner).reversed());
        for (ConcretePiece piece : _allPieces) {
            if(piece.getPieceStatistics().getDist() > 0) {
                System.out.println(piece.getName() + ": " + piece.getPieceStatistics().getDist() + " squares");
            }
        }

        System.out.println(("***************************************************************************"));

        for(int i=0;i<_mapPositions.length;i++){
            for(int j=0;j<_mapPositions[i].length;j++){
                if(_mapPositions[i][j].getPositionStatistics().getCounter()>1){
                    _allPositions.add(_mapPositions[i][j]);
                }
            }
        }
        Collections.sort(_allPositions,new Statistics.PositionStatistics.PositionComp());
        for (Position pos : _allPositions) {
            System.out.println(pos.toString() + pos.getPositionStatistics().getCounter() + " pieces");
        }

        System.out.println(("***************************************************************************"));


    }
}




