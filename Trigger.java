package de.unisaarland.cs.se.sopra.crossroad.trigger;

import java.util.Optional;

public class Trigger {

    final TriggerType triggerType;
    final Optional<Integer> optionalInclusion;

    public Trigger(final TriggerType triggerType, final Optional<Integer> optionalInclusion) {
        this.triggerType = triggerType;
        this.optionalInclusion = optionalInclusion;
    }

    public Optional<Integer> getOptionalInclusion() {
        return optionalInclusion;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

}
