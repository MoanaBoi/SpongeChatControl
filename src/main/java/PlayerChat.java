import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Collection;
import java.util.Collections;

public class PlayerChat {

    private Player player;
    private boolean timedout;
    private long epoch;
    private int time;
    public MessageChannel timeOutChannel = new MessageChannel() {
        @Override
        public Collection<MessageReceiver> getMembers() {
            return Collections.singleton(player);
        }
    };
    public MessageChannel originalChannel;

    public PlayerChat(Player player) {
        this.player = player;
        this.timedout = false;
        this.epoch = 0;
        this.time = 0;
        this.originalChannel = player.getMessageChannel();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isTimedout() {
        return timedout;
    }

    public void setTimedout(boolean timedout) {
        this.timedout = timedout;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
