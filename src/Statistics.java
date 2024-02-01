import java.util.*;


public class Statistics {
    static class PieceStatistics {
        // Store all Statistics
        private Stack<Position> positionHistory;
        private int _kills;
        private int _dist;
        public PieceStatistics() {
            positionHistory = new Stack<>();
            _kills = 0;
            _dist = 0;
        }
        public int getDist() {
            return _dist;
        }
        public void addDist(int squaresWalked) {
            this._dist += squaresWalked;
        }
        public int getKills() {
            return _kills;
        }
        public void addKill() {
            this._kills++;
        }
        public Stack<Position> getPositionHistory() {
            return positionHistory;
        }
        public List<Position> getPositionHistorySet(){
            List<Position> positionsSet = new ArrayList<Position>();
            for(int i = 0;i<positionHistory.size();i++) {
                boolean toAdd = true;
                Position current = positionHistory.get(i);
                for (int j = 0; j < positionsSet.size(); j++) {
                    if (!positionsSet.isEmpty() && current.equals(positionsSet.get(j))) {
                        toAdd = false;
                    }
                }
                if (toAdd) {
                    positionsSet.add(current);
                }
            }
            return positionsSet;
        }
        static class PositionHistoryComp implements Comparator<ConcretePiece> {
            private Player _winner;
            public PositionHistoryComp(Player winner) {
                _winner = winner;
            }
            @Override
            public int compare(ConcretePiece o1, ConcretePiece o2) {
                if (o1.isSameOwner(o2)) {
                    if(o1.getPieceStatistics().getPositionHistory().size() == o2.getPieceStatistics().getPositionHistory().size()){
                        return o1.compareTo(o2);
                    }
                    return Integer.compare(o1.getPieceStatistics().getPositionHistory().size(),
                            o2.getPieceStatistics().getPositionHistory().size());
                } else {
                    if (o1.getOwner().isPlayerOne() == _winner.isPlayerOne()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        }
        static class KillsComp implements Comparator<ConcretePiece> {
            private Player _winner;
            public KillsComp(Player winner) {
                _winner = winner;
            }
            @Override
            public int compare(ConcretePiece o1, ConcretePiece o2) {
                if (o1.getPieceStatistics().getKills() == o2.getPieceStatistics().getKills()) {
                    if (o1.compareTo(o2) == 0) {
                        if (o1.getOwner().isPlayerOne() == _winner.isPlayerOne()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1 * (o1.compareTo(o2));
                    }
                }
                return Integer.compare(o1.getPieceStatistics().getKills(), o2.getPieceStatistics().getKills());
            }
        }
        static class DistComp implements Comparator<ConcretePiece> {
            private Player _winner;
            public DistComp(Player winner) {
                _winner = winner;
            }
            @Override
            public int compare(ConcretePiece o1, ConcretePiece o2) {
                if (o1.getPieceStatistics().getDist() == o2.getPieceStatistics().getDist()) {
                    if (o1.compareTo(o2) == 0) {
                        if (o1.getOwner().isPlayerOne() == _winner.isPlayerOne()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1 * (o1.compareTo(o2));
                    }
                }
                return Integer.compare(o1.getPieceStatistics().getDist(), o2.getPieceStatistics().getDist());
            }
        }
    }
    static class PositionStatistics {
        private int _counter = 0;
        public PositionStatistics() {}
        public int getCounter() {
            return _counter;
        }
        public void addCounter() {
            this._counter++;
        }
        static class PositionComp implements Comparator<Position> {
            @Override
            public int compare(Position o1, Position o2) {
                if (o1.getPositionStatistics().getCounter() == o2.getPositionStatistics().getCounter()) {
                    if (o1.get_x() == o2.get_x()) {
                        return Integer.compare(o1.get_y(), o2.get_y());
                    } else {
                        return Integer.compare(o1.get_x(), o2.get_x());
                    }
                }

                return -1*Integer.compare(o1.getPositionStatistics().getCounter(), o2.getPositionStatistics().getCounter());
            }
        }
    }
}










