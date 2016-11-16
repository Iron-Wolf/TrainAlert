package command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import ressources.ReplyMessage;

/**
 * List all command
 */
public class HelpCommand extends BotCommand {
    private static final String LOGTAG = "HELP-COMMAND";

    private final ICommandRegistry commandRegistry;

    public HelpCommand(ICommandRegistry commandRegistry) {
        super("help", "Liste les commandes");
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder messageBuilder = new StringBuilder("<b>Commandes disponibles</b>\n");

        if (arguments.length <= 0) {
            // no argument given, return custom list of command
            for (BotCommand botCommand : commandRegistry.getRegisteredCommands()) {
                if (!botCommand.toString().contains("/help"))
                    messageBuilder.append("/" + botCommand.getCommandIdentifier()
                                        + botCommand.getDescription()
                                        + "\n");
            }

        } else {
            // return help of the given command

        }

        SendMessage answer = ReplyMessage.getSendMessage(chat.getId(), messageBuilder.toString());

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}