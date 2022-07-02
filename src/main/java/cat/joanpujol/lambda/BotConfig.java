package cat.joanpujol.lambda;

import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.ServerlessWebhook;

/**
 * Setup telegrambot api client
 */
public class BotConfig {
    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);

    @ApplicationScoped
    @Produces
    ServerlessWebhook produceServerlessWebhook() {
        return new ServerlessWebhook();
    }

    public void onStart(
            @Observes StartupEvent startupEvent,
            SecretsConfig secretsConfig,
            ServerlessWebhook serverlessWebhook,
            HelloBot bot)
            throws TelegramApiException {
        logger.info("Setup telegrambot");

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class, serverlessWebhook);
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(secretsConfig.getApiURL() + "telegram/webhook");
        telegramBotsApi.registerBot(bot, setWebhook);
    }
}
