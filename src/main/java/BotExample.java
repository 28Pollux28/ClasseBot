import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


import javax.security.auth.login.LoginException;

public class BotExample extends ListenerAdapter
{
    public static void main(String[] args) throws LoginException
    {
        new JDABuilder(AccountType.BOT)
            .setToken(System.getenv("TOKEN"))
            .addEventListeners(new BotExample())
            .setActivity(Activity.playing("Type !ping"))
            .build();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!ping"))
        {
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("Pong!").queue();
        }
    }
}