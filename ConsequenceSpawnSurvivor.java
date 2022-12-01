package de.unisaarland.cs.se.sopra.crossroad.consequence;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import de.unisaarland.cs.se.sopra.model.Survivor;

public class ConsequenceSpawnSurvivor extends Consequence {

    private final int amount;

    private final boolean children;

    public ConsequenceSpawnSurvivor(final int amount, final boolean children) {
        this.amount = amount;
        this.children = children;
    }

    public boolean needChildren() {
        return children;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connectionWrapper) {

        for (int i = 0; i < amount; i++) {
            if (model.hasNewSurvivorsLeft()) {
                final Survivor newSurvivor = model.drawSurvivor();
                newSurvivor.setLocation(model.getColony());
                final Player player = model.getCurrentPlayer();
                player.addSurvivor(newSurvivor);
                model.getColony().addSurvivor(newSurvivor);
                connectionWrapper.sendCharacterSpawned(player.getId(), newSurvivor.getId());

            }
        }
        if (children) {
            for (int i = 0; i < amount; i++) {
                model.getColony().addChild();
                connectionWrapper.sendChildSpawned();
            }
        }
    }

}
