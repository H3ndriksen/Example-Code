package de.unisaarland.cs.se.sopra.crossroad.consequence;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.EndGameState;
import de.unisaarland.cs.se.sopra.commands.KillSurvivorCommand;
import de.unisaarland.cs.se.sopra.model.Colony;
import de.unisaarland.cs.se.sopra.model.Entrance;
import de.unisaarland.cs.se.sopra.model.Location;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;
import java.util.Optional;
import sopra.comm.MoralChange;

public class ConsequenceSpawnZombies extends Consequence {
    //toDo Fix the SpawnZombie Methode with the new Colonyphase implementation
    private final int amount;

    private final Optional<Integer> locID;

    public ConsequenceSpawnZombies(final int amount, final Optional<Integer> locID) {
        this.amount = amount;
        this.locID = locID;
    }

    public int getAmount() {
        return amount;
    }

    public Optional<Integer> getLocID() {
        return locID;
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connectionWrapper) {

        if (locID.isEmpty()) {
            spawnZombiesAtColony(model, connectionWrapper);
            for (final Location loc : model.getLocations()) {
                spawnZombiesAtLocation(loc, amount, model, connectionWrapper);
            }
        }

        if (locID.isPresent()) {
            if (locID.get() == model.getColony().getId()) {
                spawnZombiesAtColony(model, connectionWrapper);
            } else {
                spawnZombiesAtLocation(model.getLocation(locID.get()).get(), amount, model,
                        connectionWrapper);
            }
        }
    }

    private void spawnZombiesAtColony(final Model model, final ConnectionWrapper connection) {
        final Colony colonyForConsequence = model.getColony();
        spawnZombiesAtLocation(colonyForConsequence, amount, model, connection);
    }

    private void spawnZombiesAtLocation(
            final Location loc,
            final int numberOfZombies,
            final Model currentModel,
            final ConnectionWrapper connectionWrapper) {
        if (!currentModel.getGameState().gameRunning()) {
            return;
        }
        int zombiesToSpawn = numberOfZombies;
        while (zombiesToSpawn != 0) {
            for (final Entrance e : loc.getEntrances()) {
                spawnInFrontOfEntrance(loc, currentModel, connectionWrapper, e);
                checkOurMoral(currentModel, connectionWrapper);
                if (--zombiesToSpawn == 0) {
                    break;
                }
            }
        }
    }

    private void spawnInFrontOfEntrance(
            final Location loc,
            final Model currentModel,
            final ConnectionWrapper connectionWrapper,
            final Entrance entrance) {
        if (entrance.getCapacityLeft() > 0) {
            entrance.addZombie();
            connectionWrapper.sendZombieSpawned(loc.getId(), entrance.getId());
        } else if (entrance.getBarricadeCount() > 0) {
            entrance.removeBarricade();
            connectionWrapper.sendBarricadeDestroyed(loc.getId(), entrance.getId());
        } else {
            killSurvivorOrChild(loc, currentModel, connectionWrapper);
        }
    }

    private void killSurvivorOrChild(final Location loc, final Model currentModel,
            final ConnectionWrapper connectionWrapper) {
        if (loc.getNumChildren() > 0) {
            loc.killChild();
            connectionWrapper.sendChildKilled();
            connectionWrapper.sendMoralChanged(-1, MoralChange.CHARACTER_DIED);
            currentModel.decreaseMoral();
            if (currentModel.getMoral() <= 0) {
                currentModel.setGameState(EndGameState.getInstance());
                connectionWrapper.sendGameEnd(false);
            }
        } else {
            final Optional<Survivor> optionalSurvivor = loc.getSurvivorSmallestStatus();
            if (optionalSurvivor.isPresent()) {
                final Survivor surv = optionalSurvivor.get();
                final KillSurvivorCommand killCommand =
                        new KillSurvivorCommand(
                        currentModel.getCommId(currentModel.getPlayer(surv).getId()),
                        currentModel.getPlayer(surv),
                        surv);
                killCommand.execute(currentModel, connectionWrapper);
            }
        }
    }

    private void checkOurMoral(final Model model, final ConnectionWrapper connection) {
        if (model.getMoral() <= 0) {
            connection.sendGameEnd(false);
            model.setGameState(EndGameState.getInstance());
        }
    }
}
