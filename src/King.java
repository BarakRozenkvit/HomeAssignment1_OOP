public class King extends ConcretePiece{
    private Player _owner;
    public King(Player owner){
        super();
        _owner=owner;
    }
    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public String getType() {
        return Character.toString('\u2654');
    }

}
