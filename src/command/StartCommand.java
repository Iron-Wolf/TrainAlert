package command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import ressources.ReplyMessage;

/**
 * Basic startup command
 */
public class StartCommand extends BotCommand {

    private static final String LOGTAG = "START-COMMAND";

    public StartCommand() {
        super("start", " - Commence la conversation");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage answer = ReplyMessage
                .getSendMessage(chat.getId(), "Choisisez une option", ReplyMessage.getMainMenuKeyboard());

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

}
