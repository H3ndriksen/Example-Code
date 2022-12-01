package de.unisaarland.cs.se.sopra.crossroad.consequence;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.model.Model;
import sopra.comm.FoodChange;

public class ConsequenceChangeFood extends Consequence {

    private final int amount;

    public ConsequenceChangeFood(final int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connectionWrapper) {
        if ((model.getColony().getFood() + amount) < 0) {
            model.getColony().increaseStarvationToken();
            connectionWrapper.sendStarvationTokenAdded();
        } else {
            if (amount < 0) {
                model.getColony().removeFood(-amount);
            }
            if (amount > 0) {
                model.getColony().addFood(amount);
            }
            connectionWrapper.sendFoodChanged(amount, FoodChange.CROSSROAD);
        }
    }

}
