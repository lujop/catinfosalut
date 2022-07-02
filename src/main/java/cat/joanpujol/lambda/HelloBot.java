package cat.joanpujol.lambda;

import javax.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Telegram bot
 */
@ApplicationScoped
class HelloBot extends TelegramWebhookBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramResource.class);

    private final SecretsConfig secretsConfig;

    public HelloBot(SecretsConfig secretsConfig) {
        this.secretsConfig = secretsConfig;
    }

    @Override
    public String getBotToken() {
        return secretsConfig.getSsmTelegramToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                logger.info("Message received {}", message);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
                sendMessage.setText("Hello " + update.getMessage().getFrom().getUserName());
                return sendMessage;
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error", e);
            return null;
        }
    }

    @Override
    public String getBotPath() {
        return "/bot";
    }

    @Override
    public String getBotUsername() {
        return "catinfosalut";
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        // Nothing to prevent configuring webhook on each lambda function run
        // Current setup is done calling TelegramResource#setupWebhook on each terraform deployment
    }
}
