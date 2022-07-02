package cat.joanpujol.lambda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.updatesreceivers.ServerlessWebhook;
import org.telegram.telegrambots.util.WebhookUtils;

/**
 * Application API
 */
@Path("/telegram")
public class TelegramResource {
    private static final String PRIVATE_API_SECRET_HEADER = "PrivateApiSecret";
    private static Logger logger = LoggerFactory.getLogger(TelegramResource.class);

    @Inject
    ServerlessWebhook serverlessWebhook;

    @Inject
    HelloBot bot;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    SecretsConfig secretsConfig;

    /**
     * Telegram webhook endpoint receiving messages from telegram
     */
    @POST
    @Path("/webhook/callback/bot")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonNode webhook(Update update) throws TelegramApiValidationException {
        logger.info("Received {}", update);
        var response = serverlessWebhook.updateReceived("/bot", update);
        logger.info("Return response {}", response);

        return objectMapper.valueToTree(response);
    }

    /**
     * Private method to set up TelegramResource#webhook url as Telegram webhook to receive messages
     */
    @POST
    @Path("/setupWebhook")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setupWebhook(
            @FormParam("baseUrl") String baseURL, @HeaderParam(PRIVATE_API_SECRET_HEADER) String privateApiHeader)
            throws TelegramApiException {
        if (Strings.isNullOrEmpty(privateApiHeader)
                || !Objects.equals(privateApiHeader, secretsConfig.getSsmPrivateApiSecretToken()))
            return Response.status(Response.Status.FORBIDDEN).build();

        SetWebhook setWebhook = new SetWebhook();
        String url = baseURL + "telegram/webhook";
        setWebhook.setUrl(url);

        logger.debug("Webhook URL", url);
        WebhookUtils.setWebhook(bot, bot, setWebhook);

        logger.info("Webhook correctly setup to {}", url);
        return Response.ok().build();
    }
}
