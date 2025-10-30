package boarding;

import comum.DaoBase;

public class BoardingPassDao extends DaoBase<BoardingPass> {
    public BoardingPassDao() {
        super(10);
    }

    @Override
    public BoardingPass[] createArray(int size) {
        return new BoardingPass[size];
    }

    @Override
    public BoardingPass cloneEntity(BoardingPass b) {
        if (b == null)
            return null;
        return new BoardingPass(b.getId(), b.getTicketId(), b.getConteudo());
    }
}