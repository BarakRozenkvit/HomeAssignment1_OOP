public class ConcretePlayer implements Player{
    private boolean _isPlayerOne=false;
    private int _wins=0;
    public ConcretePlayer(boolean isPlayerOne){
        _isPlayerOne=isPlayerOne;
    }
    @Override
    public boolean isPlayerOne() {
        return _isPlayerOne;
    }

    @Override
    public int getWins() {
        return _wins;
    }
    public void addWin(){_wins++;}
}
