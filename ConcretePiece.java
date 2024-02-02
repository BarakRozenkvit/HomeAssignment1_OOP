import java.util.Comparator;
import java.util.Objects;
import java.util.Stack;

public abstract class ConcretePiece implements Piece,Comparable<ConcretePiece> {
    private String _name;
    // Class of statistics is instantiated when creating Pawn or King
    private Statistics.PieceStatistics statistics = new Statistics.PieceStatistics();
    public ConcretePiece() {}
    @Override
    public abstract Player getOwner();
    public boolean isSameOwner(Piece c) {
        return getOwner().isPlayerOne() == c.getOwner().isPlayerOne();
    }
    @Override
    public abstract String getType();
    public void setName(String name) {
        _name = name;
    }
    public String getName() {
        return _name;
    }
    public int compareTo(ConcretePiece o) {
        if(_name.length() == o.getName().length()){
            int name1 = Integer.parseInt(_name.substring(1,_name.length()));
            int name2 = Integer.parseInt(o.getName().substring(1,_name.length()));
            return Integer.compare(name1,name2);
        }
        else{
            return Integer.compare(_name.length(),o.getName().length());
        }
    }
    public Statistics.PieceStatistics getPieceStatistics() {
        return statistics;
    }
}



