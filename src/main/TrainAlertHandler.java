package main;

import command.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import ressources.BotConfig;
import ressources.CronJob;
import ressources.ReplyMessage;

import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

/**
 * Main class containing bot behavior
 */
public class TrainAlertHandler extends TelegramLongPollingCommandBot {

    private static final String LOGTAG = "HANDLER";

    public TrainAlertHandler() {
        register(new MorningCommand());
        register(new EveningCommand());
        register(new StartCommand());
        register(new SubwayCommand());
        register(new RersCommand());
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);
    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        //check if the update has a message
        if (update.hasMessage()) {
            Message message = update.getMessage();

            //check if the message has text
            if (message.hasText()) {
                SendMessage answer = ReplyMessage
                        .getSendMessage(message.getChatId(), "Commande inconnue : " + message.getText());

                try {
                    sendMessage(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }

    public void startMonitoring() {
        try {
            // getting an instance of the scheduler
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            // setting jobs and cron triggers
            JobDetail jobMorning = newJob(CronJob.class)
                    .withIdentity("jobMorning", "group1")
                    .build();
            CronTrigger triggerMorning = newTrigger()
                    .withIdentity("triggerMorning", "group1")
                    .withSchedule(cronSchedule("0 0/30 7-8 * * ?")) // between 7am & 8am every 30min
                    .build();

            JobDetail jobEvening = newJob(CronJob.class)
                    .withIdentity("jobEvening", "group2")
                    .build();
            CronTrigger triggerEvening = newTrigger()
                    .withIdentity("triggerEvening", "group2")
                    .withSchedule(cronSchedule("0 0/30 16-17 * * ?")) // between 4pm & 5pm every 30min
                    .build();

            // add the jobs to the scheduler and start it
            sched.scheduleJob(jobMorning, triggerMorning);
            sched.scheduleJob(jobEvening, triggerEvening);
            sched.start();
        } catch (SchedulerException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}