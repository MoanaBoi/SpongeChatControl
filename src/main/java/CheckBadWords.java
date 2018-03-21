import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

@Plugin(id = "spongechatcontrol",
        name = "SpongeChatControl",
        version = "1.0.0",
        description = "Simple Sponge plugin for chat reglementations")

public class CheckBadWords {

    private static String badWords[] = {"fuck", "shit", "merde", "putain", "enculé",
            "encule", "pd", "batard", "bâtard", "fuk", "bastard", "bâstard"};
    private static SpamControl spam = new SpamControl();
    private static CmdSlowMode slowMode = new CmdSlowMode();

    public CheckBadWords() { }


    private void setReportLink(MessageChannelEvent.Chat event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        String msg = event.getRawMessage().toPlainSingle();

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            String name = player.getName();
            msg = "<" + name + "> " + msg;
            Text clickableText = Text.builder(msg).onClick(TextActions.runCommand("/report " + name)).build();
            event.setMessage(clickableText);
        }
    }

    @Listener
    public void playerMessage(MessageChannelEvent.Chat event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            String msg = event.getRawMessage().toPlainSingle();
            for (String bad : badWords) {
                if (msg.contains(bad)) {
                    msg = msg.replace(bad, "****");
                    Text newMsg = Text.builder("<" + optionalPlayer.get().getName() + "> " + msg).build();
                    event.setMessage(newMsg);
                }
            }
        }
        System.out.println("before: " + event.getRawMessage().toPlainSingle());
        spam.playerMessagePreventSpam(event);
        slowMode.checkSlowModeBeforeMessage(event);
        setReportLink(event);
        System.out.println("after: " + event.getRawMessage().toPlainSingle());
    }

    @Listener
    public void addPlayerToList(ClientConnectionEvent.Join event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        optionalPlayer.ifPresent(player -> player.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "chatcontrol.report", Tristate.TRUE));
        slowMode.addPlayerToList(event);
    }

    @Listener
    public void delPlayerFromList(ClientConnectionEvent.Disconnect event) {
        slowMode.delPlayerFromList(event);
    }

    @Listener
    public void registerCmd(GameAboutToStartServerEvent event) {
        Sponge.getCommandManager().register(this, CmdSlowMode.cmdSlowMode, "slowmode");
        Sponge.getCommandManager().register(this, CmdReport.cmdReport, "report");
    }

}
