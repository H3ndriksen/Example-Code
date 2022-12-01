package de.unisaarland.cs.se.sopra;

import de.unisaarland.cs.se.sopra.cards.Crisis;
import de.unisaarland.cs.se.sopra.goals.Goal;
import de.unisaarland.cs.se.sopra.locations.Colony;
import de.unisaarland.cs.se.sopra.locations.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameBoard {


  private List<Player> players = new ArrayList<>();
  private int maxPlayers;
  private int activePlayerID;
  private List<Survivor> survivorStack;
  private Map<Integer, Survivor> activeSurvivors = new HashMap<>();
  private List<Location> locations;
  private Colony colony;
  private List<Crisis> crisisStack;
  private int roundCounter = -2;
  private final int maxRounds;
  private Goal goal;
  private String config;
  private boolean isPlayerPhase;
  private boolean playerChanged;
  private DiceShuffler diceShuffler;

  public GameBoard(final List<Crisis> crisisStack, final List<Location> locations,
      final Colony colony, final List<Survivor> survivorStack, final int maxRounds,
      final int maxPlayers, final Goal goal, final String config, final DiceShuffler diceShuffler) {
    this.crisisStack = crisisStack;
    this.locations = locations;
    this.colony = colony;
    this.survivorStack = survivorStack;
    this.maxRounds = maxRounds;
    this.maxPlayers = maxPlayers;
    this.goal = goal;
    this.config = config;
    this.diceShuffler = diceShuffler;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(final List<Player> players) {
    this.players = players;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(final int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public int getActivePlayerID() {
    return activePlayerID;
  }

  public void setActivePlayerID(final int activePlayerID) {
    this.activePlayerID = activePlayerID;
  }

  public boolean isActivePlayerID(final int playerID) {
    return (playerID == activePlayerID);
  }

  public Optional<Player> getPlayer(final int playerID) {
    for (final Player p : players) {
      if (p.getID() == playerID) {
        return Optional.of(p);
      }
    }
    return Optional.empty();
  }


  public List<Survivor> getSurvivorStack() {
    return survivorStack;
  }

  public void setSurvivorStack(final List<Survivor> survivorStack) {
    this.survivorStack = survivorStack;
  }

  public Survivor getSurvivorFromStack(final int survivorId) {
    return survivorStack.get(survivorId);
  }

  public void addToSurvivorStack(final Survivor newSurvivor) {
    survivorStack.add(newSurvivor);
  }

  public Map<Integer, Survivor> getActiveSurvivors() {
    return activeSurvivors;
  }

  public Optional<Survivor> getActiveSurvivor(final int surID) {
    if (getActiveSurvivors().containsKey(surID)) {
      return Optional.of(getActiveSurvivors().get(surID));
    }
    return Optional.empty();
  }

  public void setActiveSurvivors(final Map<Integer, Survivor> activeSurvivors) {
    this.activeSurvivors = activeSurvivors;
  }

  public void addActiveSurvivor(final Survivor newSurvivor) {
    activeSurvivors.put(newSurvivor.getID(), newSurvivor);
  }

  public List<Location> getLocations() {
    return locations;
  }

  public void setLocations(final List<Location> locations) {
    this.locations = locations;
  }

  public Optional<Location> getLocation(final int locationID) {
    for (final Location l : locations) {
      if (l.getID() == locationID) {
        return Optional.of(l);
      }
    }
    return Optional.empty();
  }

  public void addLocations(final Location newLocation) {
    locations.add(newLocation);
  }

  public Colony getColony() {
    return colony;
  }

  public void setColony(final Colony colony) {
    this.colony = colony;
  }

  public List<Crisis> getCrisisStack() {
    return crisisStack;
  }

  public void setCrisisStack(final List<Crisis> crisisStack) {
    this.crisisStack = crisisStack;
  }

  public Crisis getActiveCrisis() {
    return crisisStack.get(0);
  }

  public void nextCrisis() {
    crisisStack.remove(0);
  }

  public int getRoundCounter() {
    return roundCounter;
  }

  public void setRoundCounter(final int roundCounter) {
    this.roundCounter = roundCounter;
  }

  public int getMaxRounds() {
    return maxRounds;
  }

  public Goal getGoal() {
    return goal;
  }

  public void setGoal(final Goal goal) {
    this.goal = goal;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(final String config) {
    this.config = config;
  }

  public boolean isGamePhase() {
    final int preparationPhase = -1;
    return roundCounter > preparationPhase;
  }

  public void startPlayerPhase() {
    isPlayerPhase = true;
    playerChanged = true;
    activePlayerID = players.get(0).getID();
  }

  public boolean isPlayerPhase() {
    return isPlayerPhase;
  }

  public boolean isRegisterPhase() {
    final int registerPhase = -2;
    return roundCounter == registerPhase;
  }

  public boolean isRegisterAborted() {
    final int registerAborted = -3;
    return roundCounter == registerAborted;
  }

  public boolean isPreparationPhase() {
    final int preparationPhase = -1;
    return roundCounter == preparationPhase;
  }

  public void startPreparationPhase() {
    final int preparationPhase = -1;
    setRoundCounter(preparationPhase);
  }

  public void startRegistrationAborted() {
    final int registerPhase = -3;
    setRoundCounter(registerPhase);
  }

  public boolean hasPlayerChanged() {
    final boolean tmp = playerChanged;
    if (tmp) {
      playerChanged = false;
    }
    return tmp;
  }

  public boolean nextRound() {
    roundCounter--;
    final int finalRound = 0;
    return roundCounter != finalRound;
  }

  public void nextPlayer() {

    final int activePlayerIndex = players.indexOf(getPlayer(activePlayerID).get());

    if (players.size() > activePlayerIndex + 1) {
      setActivePlayerID(players.get(activePlayerIndex + 1).getID());
    } else {
      setActivePlayerID(-1);
      isPlayerPhase = false;
    }
    playerChanged = true;
  }

  public boolean hasLocation(final int locID) {
    for (final Location l : locations) {
      if (l.getID() == locID) {
        return true;
      }
    }
    return false;
  }

  public DiceShuffler getDiceShuffler() {
    return diceShuffler;
  }

  public void setDiceShuffler(final DiceShuffler diceShuffler) {
    this.diceShuffler = diceShuffler;
  }


}
