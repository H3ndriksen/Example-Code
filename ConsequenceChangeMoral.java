package de.unisaarland.cs.se.sopra.crossroad.consequence;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.EndGameState;
import de.unisaarland.cs.se.sopra.model.Model;
import sopra.comm.MoralChange;

public class ConsequenceChangeMoral extends Consequence {

    private final int amount;

    public ConsequenceChangeMoral(final int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connectionWrapper) {
        if (model.getMoral() + amount <= 0) {
            model.removeTrash(model.getTrashPile());
            connectionWrapper.sendMoralChanged(amount, MoralChange.CROSSROAD);
            connectionWrapper.sendGameEnd(false);
            model.setGameState(EndGameState.getInstance());
        } else {
            if (amount < 0) {
                model.decreaseMoral(-amount);
            }
            if (amount > 0) {
                model.addMoral(amount);
            }
            connectionWrapper.sendMoralChanged(amount, MoralChange.CROSSROAD);
        }
    }

}
