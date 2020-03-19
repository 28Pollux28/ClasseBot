package fr.pollux28.classbot.event;

import java.awt.Color;

import fr.pollux28.classbot.ClassBot;
import fr.pollux28.classbot.Classe;
import fr.pollux28.classbot.Question;
import fr.pollux28.classbot.command.CommandMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class BotListener implements EventListener {

	private final CommandMap commandMap;
	public BotListener(CommandMap commandMap) {
		this.commandMap = commandMap;
	}
	
	@Override
	public void onEvent(GenericEvent event) {
		//System.out.println(event.getClass().getSimpleName());
		if(event instanceof MessageReceivedEvent) {
			onMessage((MessageReceivedEvent)event);
		}else if(event instanceof GuildVoiceJoinEvent) {
			onVoiceJoin((GuildVoiceJoinEvent)event);
		}else if(event instanceof GuildVoiceMoveEvent) {
			onVoiceMove((GuildVoiceMoveEvent)event);
		}else if(event instanceof GuildMessageReactionAddEvent) {
			onAddReact((GuildMessageReactionAddEvent) event);
		}
	}
	
	private void onAddReact(GuildMessageReactionAddEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) {
			return;
		}else {
			Member member = event.getMember();
			String emoji =event.getReaction().getReactionEmote().getEmoji();
			if(emoji.equals("❌") || emoji.equals("✅")) {
				for (Classe cl : ClassBot.getClasses()) {
					if(cl.getTextChannel().getId().equals(event.getChannel().getId())) {
						if(cl.getQuestionByMessageID(event.getMessageId()) != null) {
							Question q = cl.getQuestionByMessageID(event.getMessageId());
							if(emoji.equals("✅")) {
								if(event.getMember().equals(cl.getGuild().getMember(cl.getProf()))) {
									if(q.getGuild().getMember(q.getUser()).getVoiceState().inVoiceChannel()&& 
											q.getGuild().getMember(q.getUser()).getVoiceState().getChannel().getId().equals(cl.getVoiceChannel().getId())){
										q.setAnswering(true);
										q.getGuild().mute(q.getGuild().getMember(q.getUser()), false).queue();
									}else {
										event.getReaction().removeReaction(member.getUser()).queue();;
									}
								}else {
									event.getReaction().removeReaction(member.getUser()).queue();
								}
							}else if(emoji.equals("❌")) {
								if(event.getMember().equals(cl.getGuild().getMember(cl.getProf()))) {
									if(q.isAnswering()) {
										q.getGuild().mute(q.getGuild().getMember(q.getUser()), true).queue();
										cl.getQuestions().remove(q);
										
									}else {
										event.getReaction().removeReaction(event.getUser()).queue();
									}
								}else if(event.getMember().equals(q.getGuild().getMember(q.getUser()))) {
									q.getGuild().mute(q.getGuild().getMember(q.getUser()), true).queue();
									cl.getQuestions().remove(q);
									
								}else {
									event.getReaction().removeReaction(member.getUser()).queue();
								}
							}
						}
					}
				} 
			}
		}
	}

	private void onMessage(MessageReceivedEvent event) {
		if(event.getAuthor().equals(event.getJDA().getSelfUser())) {
			return;
		}
		String message = event.getMessage().getContentRaw();
		if(message.startsWith(commandMap.getTag())) {
			message = message.replaceFirst(commandMap.getTag(), "");
			if(commandMap.commandUser(event.getAuthor(), message, event.getMessage())) {
				if(event.getChannel() != null && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
					event.getMessage().delete().queue();
				}
			}
		}
	}
	private void onVoiceJoin(GuildVoiceJoinEvent event) {
		if(event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
			return;
		}
		User user = event.getMember().getUser();
		Member member = event.getMember(); 
		Guild guild = event.getGuild();
		VoiceChannel vc = event.getChannelJoined();		
		for(Classe cl: ClassBot.getClasses()) {
			if(guild.getId().equals(cl.getGuild().getId()) && cl.getVoiceChannel().getId().equals(vc.getId()) && !cl.getUsers().contains(user)) {
				if(guild.getSelfMember().canInteract(member)) {
					guild.kickVoiceMember(member).queue();
					String[] fieldTitle = {"/classe join @[nom du prof]"};
					String[] fieldContent = {"Vous permet de rejoindre la classe de votre professeur."};
					sendPrivateMessage(user, messageBuilder("Vous ne pouvez pas vous connecter ici", "Un cours est en cours merci de ne pas le déranger. \n"
							+ "Si vous souhaitez le rejoindre :",1,fieldTitle,fieldContent));
					return;
				}else {
					String[] fieldTitle = {"/classe join @[nom du prof]"};
					String[] fieldContent = {"Vous permet de rejoindre la classe de votre professeur."};
					sendPrivateMessage(user, messageBuilder("Merci de vous déconnecter", "Un cours est en cours merci de ne pas le déranger. \n"
							+ "Si vous souhaitez le rejoindre :",1,fieldTitle,fieldContent));
					return;
				}
			}else if (guild.getId().equals(cl.getGuild().getId()) && cl.getVoiceChannel().getId().equals(vc.getId()) && cl.getUsers().contains(user)) {
				if(guild.getSelfMember().canInteract(member)) {
					guild.mute(member, true).queue();
					String[] fieldTitle = {"/question [sujet]"};
					String[] fieldContent = {"Vous permet de demander à prendre la parole."};
					sendPrivateMessage(user, messageBuilder("Vous avez automatiquement été rendu muet", "Pour participer vocalement, demander l'autorisation à "+cl.getProf().getName()+" avec la commande"
							,1,fieldTitle,fieldContent));
					return;
				}else {
					String[] fieldTitle = {"/question [sujet]"};
					String[] fieldContent = {"Vous permet de demander à prendre la parole."};
					sendPrivateMessage(user, messageBuilder("Vous n'avez pas automatiquement été rendu muet", "Merci de vous muter pour ne pas déranger le cours de la classe"
							+ "\nPour participer vocalement, demander l'autorisation à "+cl.getProf().getName()+" puis démutez vous quand vous recevrez un message après avoir utilisé la commande"
							,1,fieldTitle,fieldContent));
					return;
				}
			}
		}
		member.mute(false).queue();
	}
	public void onVoiceMove(GuildVoiceMoveEvent event) {
		if(event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
			return;
		}
		User user = event.getMember().getUser();
		Member member = event.getMember(); 
		Guild guild = event.getGuild();
		VoiceChannel vc = event.getChannelJoined();
		for(Classe cl: ClassBot.getClasses()) {
			if(guild.getId().equals(cl.getGuild().getId()) && cl.getVoiceChannel().getId().equals(vc.getId()) && !cl.getUsers().contains(user)) {
				if(guild.getSelfMember().canInteract(member)) {
					guild.kickVoiceMember(member).queue();
					String[] fieldTitle = {"/classe join @[nom du prof]"};
					String[] fieldContent = {"Vous permet de rejoindre la classe de votre professeur."};
					sendPrivateMessage(user, messageBuilder("Vous ne pouvez pas vous connecter ici", "Un cours est en cours merci de ne pas le déranger. \n"
							+ "Si vous souhaitez le rejoindre :",1,fieldTitle,fieldContent));
					return;
				}else {
					String[] fieldTitle = {"/classe join @[nom du prof]"};
					String[] fieldContent = {"Vous permet de rejoindre la classe de votre professeur."};
					sendPrivateMessage(user, messageBuilder("Merci de vous déconnecter", "Un cours est en cours merci de ne pas le déranger. \n"
							+ "Si vous souhaitez le rejoindre :",1,fieldTitle,fieldContent));
					return;
				}
			}else if(guild.getId().equals(cl.getGuild().getId()) && cl.getVoiceChannel().getId().equals(vc.getId()) && cl.getUsers().contains(user)){
				if(!cl.getProf().getId().equals(event.getMember().getUser().getId())) {				
					if(guild.getSelfMember().canInteract(member)) {
						guild.mute(member, true).queue();
						String[] fieldTitle = {"/question [sujet]"};
						String[] fieldContent = {"Vous permet de demander à prendre la parole."};
						sendPrivateMessage(user, messageBuilder("Vous avez automatiquement été rendu muet", "Pour participer vocalement, demander l'autorisation à "+cl.getProf().getAsMention()+" avec la commande"
								,1,fieldTitle,fieldContent));
						return;
					}else {
						String[] fieldTitle = {"/question [sujet]"};
						String[] fieldContent = {"Vous permet de demander à prendre la parole."};
						sendPrivateMessage(user, messageBuilder("Vous n'avez pas automatiquement été rendu muet", "Merci de vous muter pour ne pas déranger le cours de la classe"
								+ "\nPour participer vocalement, demander l'autorisation à "+cl.getProf().getAsMention()+" puis démutez vous quand vous recevrez un message après avoir utilisé la commande"
								,1,fieldTitle,fieldContent));
						return;
					}
				}else {
					member.mute(false).queue();
				}
			}
		}
		member.mute(false).queue();
	}
	public void sendPrivateMessage(User user, MessageEmbed messageEmbed)
	{
	    user.openPrivateChannel().queue( (channel) -> channel.sendMessage(messageEmbed).queue() );
	}
	private MessageEmbed messageBuilder(String title, String description,int field, String[] fieldTitle, String[] fieldContent) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(title, null);
		eb.setColor(Color.cyan);
		eb.setDescription(description);
		for(int i = 0 ; i< field;i++) {
			if(fieldTitle[i] != null && fieldContent[i] != null) {
				eb.addField(fieldTitle[i], fieldContent[i], false);
			}
		}
		eb.setFooter("ClasseBot -- ©Valentin Lemaire -- Credits: Jérôme Lécuyer");
		eb.setThumbnail("http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png");
		MessageEmbed message = eb.build();
		return message;
	}
}
