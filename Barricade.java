package de.unisaarland.cs.se.sopra.commands;

import de.unisaarland.cs.se.sopra.Communicator;
import de.unisaarland.cs.se.sopra.GameBoard;
import de.unisaarland.cs.se.sopra.Player;
import de.unisaarland.cs.se.sopra.Survivor;
import de.unisaarland.cs.se.sopra.locations.AbstractLocation;
import de.unisaarland.cs.se.sopra.locations.Entrance;
import de.unisaarland.cs.se.sopra.pae.PassiveAndEquipment;
import java.util.List;
import java.util.Optional;

public class Barricade extends CharacterCommand {

  private final int entrance;

  public Barricade(final int commID, final int characterID, final int entrance) {
    super(commID, characterID);
    this.entrance = entrance;
  }

  @Override
  public void execute(final GameBoard gameBoard, final Communicator comm) {
    final int playerID = comm.getPIDFromCID(commID);
    if (this.legalGameCommand(comm, gameBoard, playerID)) {
      final Survivor surv = gameBoard.getActiveSurvivor(characterID).get();
      final AbstractLocation loc = surv.getLocation();
      final List<Entrance> el = loc.getEntrances();
      if (entrance >= el.size()) {
        comm.sendCommandFailed(commID, "The entrance is nonexistent");
        return;
      }
      final Entrance e = el.get(entrance);
      if (!e.enoughPlace(1)) {
        comm.sendCommandFailed(commID, "There are too many zombies");
        return;
      }
      final PassiveAndEquipment pae = surv.getPae();
      if (!pae.hasUnusedHammer()) {
        final Optional<Player> playerOptional = gameBoard.getPlayer(playerID);
        if (!playerOptional.get().useDice(0)) {
          comm.sendCommandFailed(commID, "No dice left");
          return;
        }
      }
      final int newCounter = e.getBarricadeCounter() + 1;
      e.setBarricadeCounter(newCounter);
      comm.broadcastBarricaded(characterID, loc.getID(), entrance);
    }
  }
}

