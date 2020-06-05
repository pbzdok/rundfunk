package de.pbz.unitbot.commands.audio;

import de.pbz.unitbot.commands.Command;
import de.pbz.unitbot.audio.MusicManager;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class SkipCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(SkipCommand.class);

    private final MusicManager musicManager;

    public SkipCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        LOG.info("Handling !skip message.");
        return Mono.just(event)
                .map(MessageCreateEvent::getMessage)
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (!musicManager.getScheduler().isQueueEmpty()) {
                        musicManager.getScheduler().nextTrack();
                        return channel.createMessage(getTrackInfo());
                    } else {
                        return channel.createMessage("No tracks in queue.");
                    }
                })
                .then();
    }

    private String getTrackInfo() {
        String author = musicManager.getPlayer().getPlayingTrack().getInfo().author;
        String title = musicManager.getPlayer().getPlayingTrack().getInfo().title;
        String uri = musicManager.getPlayer().getPlayingTrack().getInfo().uri;
        return "Skipped track to: " + title + " by " + author + "\n" + uri;
    }
}