import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class CmdSlowMode implements CommandExecutor {

    public static CommandSpec cmdSlowMode = CommandSpec.builder()
            .description(Text.of("Slow mode command"))
            .permission("sm-perm")
            .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("time"))))
            .executor(new CmdSlowMode())
            .build();

    private static Collection<PlayerChat> connectedPlayers = new ArrayList<PlayerChat>();
    private static int time;

    public CmdSlowMode() {
        Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        for (Player player : players) {
            connectedPlayers.add(new PlayerChat(player));
        }
    }

    public void addPlayerToList(ClientConnectionEvent.Join event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        optionalPlayer.ifPresent(player -> connectedPlayers.add(new PlayerChat(player)));
    }

    public void delPlayerFromList(ClientConnectionEvent.Disconnect event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        PlayerChat playerSave = null;
        for (PlayerChat player : connectedPlayers) {
            if (optionalPlayer.isPresent()) {
                if (player.getPlayer().getName().equals(optionalPlayer.get().getName()))
                    playerSave = player;
            }
        }
        if (playerSave != null)
            connectedPlayers.remove(playerSave);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int time = args.<Integer>getOne("time").get();
        CmdSlowMode.time = time;
        for (PlayerChat player : connectedPlayers) {
            player.applySlowMode(time, "Slow mode activé");
        }
        return CommandResult.success();
    }

    public void checkSlowModeBeforeMessage(MessageChannelEvent.Chat event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            for (PlayerChat player : connectedPlayers) {
                if (player.getPlayer().getName().equals(optionalPlayer.get().getName())) {
                    if (player.isTimedout()) {
                        if (!player.undoSlowMode()) {
                            event.setCancelled(true);
                        } else {
                            player.applySlowMode(time, "");
                        }
                    } else {
                        player.applySlowMode(time, "");
                    }
                }
            }
        }
    }
}
