package de.unisaarland.cs.se.sopra.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.unisaarland.cs.se.sopra.Communicator;
import de.unisaarland.cs.se.sopra.DiceShuffler;
import de.unisaarland.cs.se.sopra.GameBoard;
import de.unisaarland.cs.se.sopra.Injury;
import de.unisaarland.cs.se.sopra.Player;
import de.unisaarland.cs.se.sopra.Survivor;
import de.unisaarland.cs.se.sopra.activeability.BarricadeAbility;
import de.unisaarland.cs.se.sopra.activeability.FeedAbility;
import de.unisaarland.cs.se.sopra.activeability.HealAbility;
import de.unisaarland.cs.se.sopra.activeability.KillAbility;
import de.unisaarland.cs.se.sopra.cards.Card;
import de.unisaarland.cs.se.sopra.cards.Crisis;
import de.unisaarland.cs.se.sopra.locations.Colony;
import de.unisaarland.cs.se.sopra.locations.Entrance;
import de.unisaarland.cs.se.sopra.locations.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UseAbilityTest {


  DiceShuffler dice = mock(DiceShuffler.class);

  Communicator comm = mock(Communicator.class);

  Entrance entrance1 = new Entrance();
  Entrance entrance2 = new Entrance();
  Entrance entrance3 = new Entrance();


  final List<Entrance> locEntrances1 = new ArrayList<>();
  final List<Card> locCards1 = new ArrayList<>();
  final List<Entrance> locEntrances2 = new ArrayList<>();
  final List<Card> locCards2 = new ArrayList<>();

  Location loc1 = new Location(5, locEntrances1, "Police Station", locCards1, 5);

  Location loc2 = new Location(6, locEntrances2, "Fire Station", locCards2, 6);

  Player player1 = new Player(1, "Kai");

  BarricadeAbility bae = new BarricadeAbility(3, 3);

  FeedAbility fee = new FeedAbility(3);

  HealAbility heal = new HealAbility();

  KillAbility kill1 = new KillAbility(1, 2, loc1.getID(), true, true, 4);

  KillAbility kill2 = new KillAbility(1, 2, loc1.getID(), false, false);

  KillAbility kill3 = new KillAbility(1, 2, loc1.getID(), true, false);

  KillAbility kill4 = new KillAbility(1, 2, loc2.getID(), false, false);

  Survivor surv1 = new Survivor(10, "Zijie", 10, 10, 10,
      bae);

  Survivor surv2 = new Survivor(11, "Tim", 11, 11, 11,
      fee);

  Survivor surv3 = new Survivor(12, "Amanda", 12, 12, 12,
      heal);

  Survivor surv4 = new Survivor(13, "Ivo", 13, 13, 13,
      heal);

  Survivor surv5 = new Survivor(14, "Igor", 14, 14, 14, kill1);

  final List<Entrance> colonyEntrances = new ArrayList<>();
  final List<Card> colonyCards = new ArrayList<>();

  Colony colony = new Colony(1, colonyEntrances, colonyCards);

  final List<Crisis> crisisStack = new ArrayList<>();
  final List<Survivor> survivorStack = new ArrayList<>();
  final List<Location> locations = new ArrayList<>();

  GameBoard gb = new GameBoard(crisisStack, locations, colony, survivorStack, 5, 10,
      null, null, dice);

  UseAbility ua1 = new UseAbility(123, 10, Optional.of(0));
  UseAbility ua2 = new UseAbility(123, 11);
  UseAbility ua3 = new UseAbility(123, 12, Optional.of(13));
  UseAbility ua4 = new UseAbility(123, 12, Optional.of(12));
  UseAbility ua5 = new UseAbility(123, 14, Optional.of(1));
  UseAbility empty = new UseAbility(123, 10);
  UseAbility empty2 = new UseAbility(123, 14);


  @BeforeEach
  void initiate() {
    loc1.getEntrances().clear();
    loc1.getEntrances().add(entrance1);
    loc1.getEntrances().add(entrance2);
    loc2.getEntrances().add(entrance3);
    gb.getPlayers().add(player1);
    gb.getLocations().add(loc1);
    gb.addActiveSurvivor(surv1);
    gb.addActiveSurvivor(surv2);
    gb.addActiveSurvivor(surv3);
    gb.addActiveSurvivor(surv4);
    gb.addActiveSurvivor(surv5);
    gb.setActivePlayerID(1);
    gb.setRoundCounter(2);
    player1.addSurvivor(surv1);
    player1.addSurvivor(surv2);
    player1.addSurvivor(surv3);
    player1.addSurvivor(surv4);
    player1.addSurvivor(surv5);
    surv1.setPlayer(player1);
    surv2.setPlayer(player1);
    surv3.setPlayer(player1);
    surv4.setPlayer(player1);
    surv5.setPlayer(player1);
    player1.getDice().clear();
    player1.getDice().add(3);
    player1.getDice().add(5);
  }

  @BeforeEach
  void barricadeReset() {
    surv1.setActiveAbility(Optional.ofNullable(bae));
    surv1.setLocation(loc1);
    loc1.addSurvivor(surv1);
    bae.setCurrentActivations(0);
    entrance1.setBarricadeCounter(0);
    entrance1.setZombieCounter(0);
    bae.setCurrentActivations(1);
  }

  @BeforeEach
  void feedReset() {
    surv2.setLocation(colony);
    colony.enter(surv2);
    fee.reset();
    colony.setSuppliesTokens(4);
  }

  @BeforeEach
  void healReset() {
    surv3.setLocation(loc1);
    surv4.setLocation(loc1);
    loc1.addSurvivor(surv3);
    loc1.addSurvivor(surv4);
    heal.reset();
    surv3.getInjuries().add(Injury.WOUND);
    surv4.getInjuries().add(Injury.WOUND);
    surv4.getInjuries().add(Injury.WOUND);
    surv4.getInjuries().add(Injury.FROSTBITE);
  }

  @BeforeEach
  void killReset() {
    surv5.setActiveAbility(Optional.of(kill1));
    surv5.setLocation(loc1);
    surv5.getInjuries().clear();
    loc1.addSurvivor(surv5);
    kill1.reset();
    kill2.reset();
    kill3.reset();
    kill4.reset();
    entrance2.setBarricadeCounter(0);
    entrance2.setZombieCounter(2);
    colony.setChildren(5);
    colony.setMoral(14);
  }

  @Test
  void wrongPhaseTest() {
    gb.setRoundCounter(-1);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "The Game phase hasn't started yet");
  }

  @Test
  void notCurrentPlayerTest() {
    gb.setActivePlayerID(7);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "You're not the current player");
  }

  @Test
  void survivorDoesNotExistTest() {
    gb.getActiveSurvivors().clear();
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "No such survivor");
  }

  @Test
  void notYourSurvivorTest() {
    player1.getSurvivor().clear();
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "This is not your Survivor");
  }

  @Test
  void noActiveAbilityTest() {
    surv1.setActiveAbility(Optional.empty());
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "Survivor has no Active Ability");
  }

  @Test
  void maxActivationsBarricadeTest() {
    bae.setCurrentActivations(3);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "maxActivations reached，cant use BarricadeAbility");
  }

  @Test
  void noEntranceBarricadeTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    empty.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    verify(comm, times(1)).sendCommandFailed(123,
        "no such target Entrance,cant use BarricadeAbility");
  }

  @Test
  void noPlaceBarricadeTest() {
    entrance1.setZombieCounter(3);
    entrance1.setBarricadeCounter(3);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "no more place in Entrance,cant use BarricadeAbility");
  }

  @Test
  void highAmountofBarricadesBarricadeTest() {
    entrance1.setBarricadeCounter(2);
    entrance1.setZombieCounter(0);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(10, 0);
    verify(comm, times(1)).broadcastBarricaded(10, 5, 0);
    assertEquals(3, entrance1.getBarricadeCounter());
    assertEquals(0, entrance1.getZombieCounter());
    assertEquals(2, bae.getCurrentActivations());
  }

  @Test
  void highAmountofZombieAmountBarricadeTest() {
    entrance1.setBarricadeCounter(0);
    entrance1.setZombieCounter(2);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(10, 0);
    verify(comm, times(1)).broadcastBarricaded(10, 5, 0);
    assertEquals(1, entrance1.getBarricadeCounter());
    assertEquals(2, entrance1.getZombieCounter());
    assertEquals(2, bae.getCurrentActivations());
  }


  @Test
  void sameAmountofZombiesAndBarricadesBarricadeTest() {
    entrance1.setBarricadeCounter(1);
    entrance1.setZombieCounter(1);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(10, 0);
    verify(comm, times(1)).broadcastBarricaded(10, 5, 0);
    assertEquals(2, entrance1.getBarricadeCounter());
    assertEquals(1, entrance1.getZombieCounter());
    assertEquals(2, bae.getCurrentActivations());
  }


  @Test
  void highNumberofBarricadesBarricadeTest() {
    final BarricadeAbility bae2 = new BarricadeAbility(3, 5);
    surv1.setActiveAbility(Optional.of(bae2));
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(10, 0);
    verify(comm, times(1)).broadcastBarricaded(10, 5, 0);
    assertEquals(3, entrance1.getBarricadeCounter());
    assertEquals(0, entrance1.getZombieCounter());
    assertEquals(1, bae.getCurrentActivations());
  }


  @Test
  void smallNumberofBarricadesBarricade() {
    bae.setCurrentActivations(2);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua1.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(10, 0);
    verify(comm, times(1)).broadcastBarricaded(10, 5, 0);
    assertEquals(3, entrance1.getBarricadeCounter());
    assertEquals(0, entrance1.getZombieCounter());
    assertEquals(3, bae.getCurrentActivations());
  }


  @Test
  void notInColonyFeedTest() {
    colony.leave(surv2);
    loc1.addSurvivor(surv2);
    surv2.setLocation(loc1);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua2.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "survivor not in colony, cant use FeedAbility");
    loc1.leave(surv2);
  }

  @Test
  void alreadyActivatedFeedTest() {
    fee.setUsed(true);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua2.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123, "FeedAbility used");
  }

  @Test
  void feedTestFeedTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua2.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(11);
    verify(comm, times(1)).broadcastFoodChangedAdd(3);
    assertEquals(7, colony.getSuppliesTokens());
    assertTrue(fee.isUsed());
  }

  @Test
  void notSameLocationHealTest() {
    loc1.leave(surv3);
    colony.enter(surv3);
    surv3.setLocation(colony);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua3.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "cant use HealAbility to survivor in other location");
    colony.leave(surv3);
  }

  @Test
  void alreadyUsedHealTest() {
    heal.setUsed(true);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua3.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123, "HealAbility used");
  }

  @Test
  void noInjuriesHealTest() {
    surv4.getInjuries().clear();
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua3.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123, "target no injury, cant use HealAbility");
  }

  @Test
  void onlyFrostBiteHealTest() {
    surv4.getInjuries().remove(0);
    surv4.getInjuries().remove(0);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua3.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(12, 13);
    assertTrue(heal.isUsed());
    assertEquals(0, surv4.getInjuries().size());
    assertFalse(surv4.getInjuries().contains(Injury.FROSTBITE));
    surv4.getInjuries().clear();
  }

  @Test
  void onlyWoundHealTest() {
    surv4.getInjuries().remove(Injury.FROSTBITE);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua3.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(12, 13);
    assertTrue(heal.isUsed());
    assertEquals(1, surv4.getInjuries().size());
    assertTrue(surv4.getInjuries().contains(Injury.WOUND));
    surv4.getInjuries().clear();
  }

  @Test
  void bothFrostAndWoundHealTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua3.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(12, 13);
    assertTrue(heal.isUsed());
    assertEquals(2, surv4.getInjuries().size());
    assertTrue(surv4.getInjuries().contains(Injury.WOUND));
  }

  @Test
  void selfHealingHealTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua4.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(12, 12);
    assertTrue(heal.isUsed());
    assertEquals(0, surv3.getInjuries().size());
    assertFalse(surv3.getInjuries().contains(Injury.WOUND));
    assertEquals(4, colony.getSuppliesTokens());
  }

  @Test
  void wrongLocationKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    surv5.setActiveAbility(Optional.of(kill4));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "suvivor not in target location, cant use KillAbility");
  }

  @Test
  void diceTooLowKillTest() {
    player1.getDice().remove(1);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "no dice > dieValue, cant use this KillAbility");
  }

  @Test
  void noChildrenKillTest() {
    colony.setChildren(0);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    surv5.setActiveAbility(Optional.of(kill3));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "no child in colony, cant use this KillAbility");
  }

  @Test
  void alreadyActivatedKillTest() {
    kill1.setCurrentActivations(1);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "maxActivations reached，cant use KillAbility");
  }

  @Test
  void noEntranceKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    empty2.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "no such entranceID, cant use KillAbility");
  }

  @Test
  void noZombiesKillTest() {
    entrance2.setZombieCounter(0);
    surv5.setActiveAbility(Optional.of(kill2));
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).sendCommandFailed(123,
        "no zombies an entrance, cant use this KillAbility");
  }

  @Test
  void needNoChildrenNoDiceKilledAllZombiesKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    surv5.setActiveAbility(Optional.of(kill2));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    assertEquals(1, kill2.getCurrentActivations());
    assertEquals(0, entrance2.getZombieCounter());
  }

  @Test
  void needNoChildrenNoDiceKilledNotAllZombiesKillTest() {
    entrance2.setZombieCounter(3);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    surv5.setActiveAbility(Optional.of(kill2));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    assertEquals(1, kill2.getCurrentActivations());
    assertEquals(1, entrance2.getZombieCounter());
  }

  @Test
  void withChildrenNoDiceKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    surv5.setActiveAbility(Optional.of(kill3));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    assertEquals(1, kill3.getCurrentActivations());
    assertEquals(0, entrance2.getZombieCounter());
  }

  @Test
  void withoutInfectionDiceKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    when(dice.dice(12)).thenReturn(3);
    when(dice.infectionDice()).thenReturn(Optional.empty());
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    assertEquals(1, kill1.getCurrentActivations());
    assertEquals(0, entrance2.getZombieCounter());
    assertEquals(0, surv5.getInjuries().size());
  }

  @Test
  void withInfectionFrostBiteDiceKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    when(dice.infectionDice()).thenReturn(Optional.of(Injury.FROSTBITE));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    verify(comm, times(1)).broadcastFrostbitten(surv5.getID());
    assertEquals(1, kill1.getCurrentActivations());
    assertEquals(0, entrance2.getZombieCounter());
    assertEquals(1, surv5.getInjuries().size());
    assertTrue(surv5.getInjuries().contains(Injury.FROSTBITE));
  }

  @Test
  void withInfectionWoundDiceKillTest() {
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    when(dice.infectionDice()).thenReturn(Optional.of(Injury.WOUND));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    verify(comm, times(1)).broadcastWounded(surv5.getID());
    assertEquals(1, kill1.getCurrentActivations());
    assertEquals(0, entrance2.getZombieCounter());
    assertEquals(1, surv5.getInjuries().size());
    assertTrue(surv5.getInjuries().contains(Injury.WOUND));
  }

  @Test
  void withInfectionWoundDiceButDiesKillTest() {
    surv5.getInjuries().add(Injury.WOUND);
    surv5.getInjuries().add(Injury.WOUND);
    when(comm.getPIDFromCID(123)).thenReturn(1);
    when(comm.getCIDFromPID(1)).thenReturn(123);
    when(dice.infectionDice()).thenReturn(Optional.of(Injury.WOUND));
    ua5.execute(gb, comm);
    //verify(comm, times(1)).getPIDFromCID(123);
    //verify(comm, times(1)).getCIDFromPID(1);
    verify(comm, times(1)).broadcastAbilityUsed(surv5.getID(), 1);
    verify(comm, times(1)).broadcastZombieKilled(surv5.getID(), loc1.getID(), 1);
    verify(comm, times(1)).broadcastWounded(surv5.getID());
    verify(comm, times(1)).broadcastMoralChangedCharacterDied(-1);
    assertEquals(1, kill1.getCurrentActivations());
    assertEquals(0, entrance2.getZombieCounter());
    assertFalse(gb.getActiveSurvivors().containsValue(surv5));
    assertFalse((player1.getSurvivor().contains(surv5)));
    assertEquals(13, colony.getMoral());
  }

}
