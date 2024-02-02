public class Position {
    private int _x,_y;
    private Statistics.PositionStatistics statistics = new Statistics.PositionStatistics();
    public Position(int x, int y) {_x=x;_y=y;}
    public int get_x(){
        return _x;
    }
    public int get_y(){
        return _y;
    }
    /**
     * return the position above to my current position
     * @return upper position
     */
    public Position getUpperPos(){
        return new Position(_x,_y-1);
    }
    /**
     * return the position below to my current position
     * @return lower position
     */
    public Position getLowerPos(){
        return new Position(_x,_y+1);
    }
    /**
     * return the position right to my current position
     * @return right position
     */
    public Position getRightPos(){
        return new Position(_x+1,_y);
    }
    /**
     * return the position left to my current position
     * @return left position
     */
    public Position getLeftPos(){
        return new Position(_x-1,_y);
    }
    /**
     * is my position is completely outsize (not near the edge) of the board
     * @param boardSize
     * @return true or false
     */
    public boolean isOutsizeOfBoard(int boardSize){
        if (_x < -1 || _x > boardSize || _y < -1 || _y > boardSize){return true;}
        return false;
    }
    /**
     * is my position is outsize near the edge of the board
     * @param boardSize
     * @return true or false
     */
    public boolean isOutsizeEdgeOfBoard(int boardSize){
        if (_x == -1 || _x == boardSize || _y == -1 || _y == boardSize){return true;}
        return false;
    }
    /**
     * is my position is at the corner of the board
     * @param boardSize
     * @return
     */
    public boolean isCorner(int boardSize){
        if((_x==0 && _y==0)
                || (_x==boardSize-1 && _y==0)
                || (_x==0 && _y==boardSize-1)
                || (_x==boardSize-1 && _y==boardSize-1)){
            return true;
        }
        return false;
    }
    /**
     * what is the distance between me and other position
     * @param p - other position
     * @return distance
     */
    public int distanceTo(Position p) {
        if (p.get_x() == _x) {
            return Math.abs(p.get_y() - _y);
        }
        return Math.abs(p.get_x() - _x);
    }
    public boolean equals(Position p){
        return p.get_x() == _x && p.get_y()==_y;
    }
    @Override
    public String toString(){
        return "(" + _x + ", " + _y + ")";
    }
    public Statistics.PositionStatistics getPositionStatistics(){
        return statistics;
    }
}
