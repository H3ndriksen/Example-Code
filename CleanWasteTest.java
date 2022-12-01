package de.unisaarland.cs.se.sopra.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.unisaarland.cs.se.sopra.Communicator;
import de.unisaarland.cs.se.sopra.DiceShuffler;
import de.unisaarland.cs.se.sopra.GameBoard;
import de.unisaarland.cs.se.sopra.Player;
import de.unisaarland.cs.se.sopra.Survivor;
import de.unisaarland.cs.se.sopra.cards.Card;
import de.unisaarland.cs.se.sopra.cards.Crisis;
import de.unisaarland.cs.se.sopra.locations.Colony;
import de.unisaarland.cs.se.sopra.locations.Entrance;
import de.unisaarland.cs.se.sopra.locations.Location;
import de.unisaarland.cs.se.sopra.pae.EmptyAbility;
import de.unisaarland.cs.se.sopra.pae.PassiveAndEquipment;
import de.unisaarland.cs.se.sopra.pae.Swab;
import de.unisaarland.cs.se.sopra.pae.TrashAbility;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class CleanWasteTest {

  private Communicator comm;

  final List<Entrance> entrances1 = new ArrayList<>();
  final List<Card> cards1 = new ArrayList<>();


  Colony colony = new Colony(1, entrances1, cards1);


  final List<Entrance> entrances2 = new ArrayList<>();
  final List<Card> cards2 = new ArrayList<>();


  Location location = new Location(2, entrances2, "Police Station", cards2, 5);


  final Player player = new Player(10, "Amanda");

  PassiveAndEquipment pae = new EmptyAbility();


  final Survivor surv = new Survivor(110, "Zijie", 20, 7, 8,
      pae);

  final List<Crisis> crisisStack = new ArrayList<>();
  final List<Survivor> survivorStack = new ArrayList<>();
  final List<Location> locations = new ArrayList<>();



  public final GameBoard gb = new GameBoard(crisisStack, locations, colony, survivorStack,
      5, 10, null, null, new DiceShuffler(666));


  @BeforeEach
  public void reset() {
    comm = mock(Communicator.class);
    gb.addLocations(location);
    gb.getPlayers().add(player);
    gb.setColony(colony);
    gb.setRoundCounter(0);
    gb.setActivePlayerID(10);
    gb.addActiveSurvivor(surv);
    player.addSurvivor(surv);
    surv.setPlayer(player);
    surv.setLocation(colony);
    colony.enter(surv);
    player.getDice().add(3);
    player.getDice().add(2);
    colony.setTrashPile(7);
    surv.setPae(pae);
  }

  @Test
  void wrongPhaseTest() {
    gb.setRoundCounter(-1);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "The Game phase hasn't started yet");
  }

  @Test
  void notCurrentPlayerTest() {
    gb.setActivePlayerID(11);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "You're not the current player");
  }

  @Test
  void noActiveSurvivorTest() {
    gb.getActiveSurvivors().clear();
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "No such survivor");
  }

  @Test
  void notYourSurvivorTest() {
    player.getSurvivor().remove(surv);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "This is not your Survivor");
  }

  @Test
  void notInColony() {
    colony.leave(surv);
    location.enter(surv);
    surv.setLocation(location);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "Survivor is not in Colony");
  }

  @Test
  void trashIsEmpty() {
    colony.setTrashPile(0);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "Trash is already empty");

  }

  @Test
  void noDicesLeft() {
    player.getDice().clear();
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).sendCommandFailed(1223,
        "No dices left");
  }

  @Test
  void equipmentIsAlreadyUsed() {
    final PassiveAndEquipment pae2 = new EmptyAbility();
    final Swab sw = new Swab(pae2);
    surv.setPae(sw);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(2)).getPIDFromCID(1223);
    verify(comm, times(1)).broadcastWasteChange(2);
  }


  @Test
  void withoutAnything() {
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).broadcastWasteChange(4);
    assertEquals(4, colony.getTrashPile());
    assertEquals(1, player.getDice().size());
  }

  @Test
  void withEquipment() {
    final PassiveAndEquipment pae2 = new EmptyAbility();
    final Swab sw = new Swab(pae2);
    sw.reset();
    surv.setPae(sw);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).broadcastWasteChange(2);
    assertEquals(2, colony.getTrashPile());
    assertEquals(1, player.getDice().size());
  }

  @Test
  void withPassiveAbilty() {
    final PassiveAndEquipment pae3 = new TrashAbility(2);
    surv.setPae(pae3);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).broadcastWasteChange(5);
    assertEquals(5, colony.getTrashPile());
    assertEquals(1, player.getDice().size());
  }

  @Test
  void withPAE() {
    final PassiveAndEquipment pae4 = new TrashAbility(2);
    final Swab sw2 = new Swab(pae4);
    sw2.reset();
    surv.setPae(sw2);
    final CleanWaste cleanWaste = new CleanWaste(1223, 110);
    when(comm.getPIDFromCID(1223)).thenReturn(10);
    when(comm.getCIDFromPID(10)).thenReturn(1223);
    cleanWaste.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(1223);
    verify(comm, times(1)).broadcastWasteChange(2);
    assertEquals(2, colony.getTrashPile());
    assertEquals(1, player.getDice().size());
  }


}
