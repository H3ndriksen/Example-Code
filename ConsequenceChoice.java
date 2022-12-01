package de.unisaarland.cs.se.sopra.crossroad.consequence;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.commands.Command;
import de.unisaarland.cs.se.sopra.commands.LeaveCommand;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import sopra.comm.TimeoutException;

public class ConsequenceChoice extends Consequence {

    private final Consequence consequence1;

    private final Consequence consequence2;

    public ConsequenceChoice(final Consequence consequence1, final Consequence consequence2) {
        this.consequence1 = consequence1;
        this.consequence2 = consequence2;
    }


    public Consequence getConsequence1() {
        return consequence1;
    }

    public Consequence getConsequence2() {
        return consequence2;
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connectionWrapper) {

        for (final Player p : model.getPlayers()) {
            if (!p.hasLeft()) {
                connectionWrapper.sendVoteNow(model.getCommId(p.getId()));
                handleVote(model, p, connectionWrapper);
            }
        }
        connectionWrapper.sendVoteResult(model.getVotesForConsequence1()
                >= model.getVotesForConsequence2());
        if (model.getVotesForConsequence1() < model.getVotesForConsequence2()) {
            consequence2.run(model, connectionWrapper);
        } else {
            consequence1.run(model, connectionWrapper);
        }
        model.resetConsequence();
    }

    private void handleVote(
            final Model model, final Player player, final ConnectionWrapper connection) {
        Command command = nextVote(model, player, connection);
        if (model.getCommId(player.getId()) == command.getCommId()) {
            while (!command.isVote() || model.getCommId(player.getId()) != command.getCommId()) {
                if (!command.isVote()) {
                    connection.sendCommandFailed(command.getCommId(),
                            "This is not a Vote Command, please vote");
                    connection.sendVoteNow(model.getCommId(player.getId()));
                    command = nextVote(model, player, connection);
                } else {
                    if (model.getCommId(player.getId()) != command.getCommId()) {
                        connection.sendCommandFailed(command.getCommId(),
                                "You're not the current Voter");
                        command = nextVote(model, player, connection);
                    }
                }
            }
            if (command.getCommId() == model.getCommId(player.getId())) {
                command.execute(model, connection);
            }
        } else {
            connection.sendCommandFailed(command.getCommId(),
                    "It's not your turn to Vote");
            handleVote(model, model.getCurrentPlayer(), connection);
        }
    }

    private Command nextVote(
            final Model model, final Player player, final ConnectionWrapper connection) {
        try {
            return connection.nextCommand();
        } catch (final TimeoutException e) {
            return new LeaveCommand(model.getCommId(player.getId()));
        }
    }

}
