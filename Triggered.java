package de.unisaarland.cs.se.sopra.crossroad.trigger;


import de.unisaarland.cs.se.sopra.cards.Card;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;
import java.util.Optional;

public class Triggered {

    //toDo are the return Statemets right?
    private  int oldTrash;
    boolean triggered;

    public Triggered() {
        this.triggered = false;
        this.oldTrash = 0;
    }


    public void triggeredBarricaded(final Trigger trigger, final int locationID) {
        if (trigger.getTriggerType() == TriggerType.BARRICADED) {
            if (trigger.getOptionalInclusion().isEmpty()) {
                triggered = true;
            } else {
                triggered = locationID == trigger.getOptionalInclusion().get();
            }
        }
    }

    public void triggeredBarricadedWithAbility(final Trigger trigger, final int amount,
            final Survivor survivor) {
        if (amount > 0) {
            if (trigger.getTriggerType() == TriggerType.BARRICADED) {
                if (trigger.getOptionalInclusion().isEmpty()) {
                    triggered = true;
                } else {
                    if (trigger.getOptionalInclusion().get() == survivor.getLocation().getId()) {
                        triggered = true;
                    }
                }
            }
        }
    }

    public void triggeredMoved(final Trigger trigger, final int locationID) {
        if (trigger.getTriggerType() == TriggerType.MOVED) {
            if (trigger.getOptionalInclusion().isEmpty()) {
                triggered = true;
            } else {
                triggered = locationID == trigger.getOptionalInclusion().get();
            }
        }
    }



    public void triggeredEquip(final Trigger trigger, final Optional<Survivor> optionalSurvivor,
            final Optional<Card> optionalCard, final int target,
            final int survivorID, final Model model) {
        if (trigger.getTriggerType() == TriggerType.EQUIP) {
            if (optionalCard.get().isEquip()) {
                if (survivorID == target) {
                    triggered = true;
                } else {
                    final Optional<Survivor> optionalSurvivorTarget = model.getSurvivor(target);
                    if (optionalSurvivorTarget.isPresent()) {
                        if (optionalSurvivor.get().getLocation().getId()
                                == optionalSurvivorTarget.get().getLocation().getId()) {
                            triggered = true;
                        }
                    }
                }
            }
        }
    }


    public void triggeredSearched(final Trigger trigger, final int locationID) {
        if (trigger.getTriggerType() == TriggerType.SEARCHED) {
            if (trigger.getOptionalInclusion().isEmpty()) {
                triggered = true;
            } else {
                triggered = locationID == trigger.getOptionalInclusion().get();
            }
        }
    }

    public void triggeredAddWasteChanged(final Trigger trigger, final int trashPile) {
        if (trigger.getTriggerType() == TriggerType.WASTECHANGED) {
            if (trigger.getOptionalInclusion().isPresent()) {
                final int amount = trigger.getOptionalInclusion().get();
                if (amount > 0) {
                    if (oldTrash + amount <= trashPile) {
                        triggered = true;
                    }
                } else {
                    if (amount == 0) {
                        triggered = true;
                    }
                }
            } else {
                triggered = true;
            }
        }
    }

    public void triggeredRemoveWasteChanged(final Trigger trigger, final int trashPile) {
        if (trigger.getTriggerType() == TriggerType.WASTECHANGED) {
            if (trigger.getOptionalInclusion().isPresent()) {
                final int amount = trigger.getOptionalInclusion().get();
                if (amount < 0) {
                    if (oldTrash + amount >= trashPile) {
                        triggered = true;
                    }
                } else {
                    if (amount == 0) {
                        triggered = true;
                    }
                }
            } else {
                triggered = true;
            }
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setOldTrash(final int oldTrash) {
        this.oldTrash = oldTrash;
    }

    public void reset() {
        triggered = false;
    }



}
