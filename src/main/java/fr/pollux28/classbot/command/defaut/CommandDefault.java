package fr.pollux28.classbot.command.defaut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.pollux28.classbot.ClassBot;
import fr.pollux28.classbot.Classe;
import fr.pollux28.classbot.Question;
import fr.pollux28.classbot.command.Command;
import fr.pollux28.classbot.command.Command.ExecutorType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class CommandDefault {
	private final ClassBot classBot;
	final String imgLivre = "http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png";
	final String imgError = "https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png";


	public CommandDefault(ClassBot classBot) {
		this.classBot = classBot;
	}

	@Command(name = "stop", type = ExecutorType.CONSOLE)
	private void stop() {
		classBot.setRunning(false);
	}

	@Command(name="info", type =ExecutorType.USER)
	private void info(Guild guild,User user, MessageChannel channel) {
		channel.sendMessage(user.getAsMention() + "dans le channel " +channel.getName()).queue();
	}
	@Command(name="classe", type=ExecutorType.USER )
	private void classe(User user, TextChannel textChannel, Guild guild, String command) {
		String[] args = command.split(" ");
		if(args.length < 2)
			return;
		args[1] = args[1].toLowerCase();
		
		boolean hasPermission = false;
		Member member = guild.getMember(user);
		List<Role> roles = member.getRoles();
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase("prof")) {
				hasPermission = true;
			}
		}
		String[] fieldTitle = new String[]{""};
		String[] fieldContent = new String[]{""};
		
		switch(args[1]) {
			case "start":
				if(!hasPermission) {
					fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
					fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
					textChannel.sendMessage(messageBuilder("Erreur", "Vous devez Ãªtre un prof pour dÃ©marrer un cours "+ user.getAsMention(),
							1,fieldTitle,fieldContent,imgError)).queue();
					return;
				}

				if(args.length < 3) {
					textChannel.sendMessage(messageBuilder("Erreur Syntaxe", user.getAsMention() + " La comande est du format /classe start name", 0, null, null,
							imgError)).queue();
					return;
				}
				String name = args[2];

				VoiceChannel vc = member.getVoiceState().getChannel();
				if(vc == null) {
					fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
					fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
					textChannel.sendMessage(messageBuilder("Erreur", "Merci de vous connecter sur un channel vocal pour dÃ©marrer un cours "+ user.getAsMention(),
							1,fieldTitle,fieldContent,imgError)).queue();
					return;
				}
				Classe classe = new Classe(name, user, guild, vc, textChannel);
				classe.setUsers(new ArrayList<User>());
				classe.addUser(user, member);
				classe.setMuted(false);
				for (Classe cl : ClassBot.getClasses()) {
					if(cl.getGuild().getId().equals(classe.getGuild().getId())) {
						if(cl.getName().equalsIgnoreCase(classe.getName())) {
							fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
							fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", "Une classe nommÃ©e __**"+ classe.getName().toUpperCase()+
									"**__ existe dÃ©jÃ  ! \n Choississez un autre nom "+ classe.getProf().getAsMention()
									+" !", 1, fieldTitle, fieldContent,imgError)).queue();
							return;
						}
						if(cl.getProf().getName().equals(classe.getProf().getName())) {
							fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
							fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", classe.getProf().getAsMention() + " Vous Ãªtes actuellement professeur dans "+ cl.getName()+" sur le salon "
									+ cl.getTextChannel().getAsMention()+"\n Fermez cette classe avec /classe stop avant d'en lancer une nouvelle !", 1, fieldTitle, fieldContent,
									imgError)).queue();
							return;
						}
						if(cl.getVoiceChannel() == classe.getVoiceChannel()) {
							fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
							fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", classe.getProf().getAsMention()+" Une classe est dÃ©jÃ  prÃ©sente dans ce salon vocal "
									+classe.getVoiceChannel().getName()+ "\n Choisissez un autre salon vocal pour pouvoir lancer votre classe.",1,fieldTitle,fieldContent,
									imgError)).queue();
							return;
						}
						if(cl.getTextChannel() == classe.getTextChannel()) {
							fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
							fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", classe.getProf().getAsMention()+" Une classe est dÃ©jÃ  prÃ©sente dans ce salon textuel "
									+classe.getTextChannel().getAsMention()+ "\n Choisissez un autre salon textuel pour pouvoir lancer votre classe.",1,fieldTitle,fieldContent,
									imgError)).queue();
							return;
						}
					}
				}

				ClassBot.getClasses().add(classe);
				for(Member memberVoiceChannel : classe.getVoiceChannel().getMembers()) {
					if(guild.getSelfMember().canInteract(memberVoiceChannel)){
						if(!classe.getUsers().contains(memberVoiceChannel.getUser())) {
							guild.kickVoiceMember(memberVoiceChannel).queue();
							fieldTitle = new String[]{"/classe join @[nom du prof]"};
							fieldContent = new String[]{"Vous permet de rejoindre la classe de votre professeur."};
							sendPrivateMessage(memberVoiceChannel.getUser(), messageBuilder("Vous avez Ã©tÃ© dÃ©connectÃ©", "Une classe a Ã©tÃ© lancÃ©e dans ce salon "+classe.getVoiceChannel().getName()
									+ "\nPour rejoindre le cours utilisez la commande :",1, fieldTitle, fieldContent,
									imgError));
						}
					}else {
						fieldTitle = new String[]{"/classe join @[nom du prof]"};
						fieldContent = new String[]{"Vous permet de rejoindre la classe de votre professeur."};
						if(!classe.getUsers().contains(memberVoiceChannel.getUser())) sendPrivateMessage(memberVoiceChannel.getUser(), messageBuilder("Merci de vous dÃ©connecter "+memberVoiceChannel.getUser().getAsTag(),
								"Un cours a dÃ©butÃ© dans le salon "+
						classe.getVoiceChannel().getName()+"\nPour ne pas le perturber, merci de vous dÃ©connecter. \n"
								+ "Pour rejoindre le cours utilisez la commande :", 1, fieldTitle, fieldContent
								,imgError));
					}
				}
				fieldTitle = new String[]{"Pour arrÃªter la classe, "+ classe.getProf().getName()+" faites : "};
				fieldContent = new String[]{"/classe stop"};
				classe.getTextChannel().sendMessage(guild.getRolesByName("@everyone", true).get(0).getAsMention()).queue();
				classe.getTextChannel().sendMessage(messageBuilder("Classe crÃ©Ã©e ! ", "Tous les Ã©lÃ¨ves souhaiant rejoindre la classe __**" 
						+classe.getName().toUpperCase()+"**__ de "+classe.getProf().getAsMention() +" dans le salon vocal __**"+classe.getVoiceChannel().getName() +
						"**__ doivent Ã©crire /classe join "+classe.getProf().getAsMention()+
						" dans " + classe.getTextChannel().getAsMention() + "et se connecter dans le salon vocal " +classe.getVoiceChannel().getName(),1,fieldTitle,fieldContent,
						imgLivre)).queue();
				return;
			case "stop":
				if(!hasPermission) {
					fieldTitle = new String[]{"/classe [help/start/stop/join/quit] [name]"};
					fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
					textChannel.sendMessage(messageBuilder("Erreur", "Vous devez Ãªtre le prof pour arrÃªter un cours "+ user.getAsMention(),
							1,fieldTitle,fieldContent,imgError)).queue();
					return;
				}
				for(Classe cl : ClassBot.getClasses()) {
					if(cl.getGuild().getId().equals(guild.getId()) && cl.getProf().getId().equals(user.getId()) && cl.getTextChannel() == textChannel) {
						for (User usert : cl.getUsers()) {
							ClassBot.getMemberClasses().remove(guild.getMember(usert));
							if(guild.getSelfMember().canInteract(guild.getMember(usert))&&guild.getMember(usert).getVoiceState().inVoiceChannel()){
								guild.getMember(usert).mute(false).queue();
								guild.kickVoiceMember(guild.getMember(usert)).queue();
							}
						}
						for(Question q :cl.getQuestions()) {
							cl.getQuestions().remove(q);
						}

						ClassBot.getClasses().remove(cl);
						textChannel.sendMessage(messageBuilder("Classe terminÃ©e !", "La classe __**"+cl.getName().toUpperCase()+"**__ de"+ user.getAsMention()+" est maintenant terminÃ©e ! "
								+ "\nN'oubliez pas de faire vos devoirs !",
								0,null,null,imgLivre)).queue();
						return;
					}
				}
				break;
			case "join":
				if(args.length < 3) {
					sendPrivateMessage(member.getUser(), messageBuilder("Vous devez prÃ©ciser le nom du prof !", 
							"/classe join @[nomduprof]",0, null, null,
							imgError));
					return;
				}
				if(ClassBot.getMemberClasses().containsKey(member)) {
					fieldTitle = new String[]{"/classe quit"};
					fieldContent = new String[]{"Vous permet de quitter la classe de votre professeur."};
					textChannel.sendMessage(messageBuilder("Vous faÃ®tes dÃ©jÃ  parti d'une classe", "Vous devez quitter votre ancienne classe avant de pouvoir en rejoindre une autre "
					+ "\nPour quitter le cours utilisez la commande :",1, fieldTitle, fieldContent,
						imgError)).queue();
					return;
				}
				for (Classe cl : ClassBot.getClasses()) {
					if(cl.getGuild().getId().equals(guild.getId()) && cl.getProf().getAsMention().equals(args[2])) {
						cl.addUser(user, member);
						textChannel.sendMessage(messageBuilder(user.getName()+" a rejoint la classe!", "La classe __**"+cl.getName().toUpperCase()+"**__ de"+ cl.getProf().getAsMention()
								+" peut maintenant commencer ! \n __**N'oublie pas de te connecter dans le salon vocal "+cl.getVoiceChannel().getName()
								+ "**__\nAller, au travail !",
								0,null,null,imgLivre)).queue();
						return;
					}
				}
				textChannel.sendMessage(messageBuilder("Erreur", "Le nom prÃ©cisÃ© est incorrect. VÃ©rifiez le nom et recommencez."
						+ "\n Courage vous pourrez bientÃ´t travailler :wink:",0, null,null,
						imgError)).queue();
				return;

			case "quit":
				if(!ClassBot.getMemberClasses().containsKey(member)) {
					fieldTitle = new String[]{"/classe join @[nom_du_prof]"};
					fieldContent = new String[]{"Vous permet de poser une question."};
					sendPrivateMessage(member.getUser(), messageBuilder("Vous devez faire parti d'une classe !", 
							"Afin de pouvoir quitter une classe, vous devez faire parti d'une classe !",1, fieldTitle, fieldContent,
							imgError));
					return;
				}
				Classe cl = ClassBot.getMemberClasses().get(member);
				if(!ClassBot.getMemberClasses().get(member).getProf().getId().equals(user.getId())) {
					if(member.getVoiceState().inVoiceChannel() && member.getVoiceState().getChannel().getId().equals(cl.getVoiceChannel().getId())) {
						member.mute(false).queue();
						guild.kickVoiceMember(member).queue();
					}
					if(ClassBot.getMemberClasses().get(member).getQuestionByMember(member) != null) {
						ClassBot.getMemberClasses().get(member).removeQuestion(ClassBot.getMemberClasses().get(member).getQuestionByMember(member));	
					}
					ClassBot.getMemberClasses().get(member).removeUser(member.getUser());
					ClassBot.getMemberClasses().remove(member);
					textChannel.sendMessage(messageBuilder(user.getName()+" a quittÃ© la classe!","Est-ce un dÃ©serteur ? Il n'aimait plus le doux son de la voix de "+cl.getProf().getAsMention()+" ?"
					+"\n Les autres, retournez au travail !",0,null,null,imgLivre)).queue();
					return;
				} else {
					sendPrivateMessage(member.getUser(), messageBuilder("Vous ne devez pas Ãªtre le prof !", 
							"Vous ne pouvez pas quitter la classe si vous Ãªtes le prof. Vous pouvez cependant la terminer avec /classe stop!",0, null, null,
							imgError));
					return;
				}
			case "mute":
				if(!hasPermission) {
					fieldTitle = new String[]{"/classe [help/start [name]/stop/join @[nomduProf]/quit/mute/info {nom}]"};
					fieldContent = new String[]{"Permet d'effectuer des actions sur la classe"};
					textChannel.sendMessage(messageBuilder("Erreur", "Vous devez Ãªtre le prof pour rendre muet la classe "+ user.getAsMention(),
							1,fieldTitle,fieldContent,imgError)).queue();
					return;
				}
				for(Classe cls : ClassBot.getClasses()) {
					if(cls.getGuild().getId().equals(guild.getId()) && cls.getProf().getId().equals(user.getId()) && cls.getTextChannel() == textChannel) {
						boolean mute = cls.isMuted();
						for(User usr : cls.getUsers()) {
							if(guild.getSelfMember().canInteract(guild.getMember(usr)) && !cls.getProf().getId().equals(usr.getId())){
								guild.getMember(usr).mute(!mute).queue();
							}
						}
						cls.setMuted(!mute);
						if(!mute) {
						String[] fieldTitle1 = {"/question [sujet]"};
						String[] fieldContent1 = {"Vous permet de demander Ã  prendre la parole."};
						textChannel.sendMessage(messageBuilder("La classe Ã  Ã©tÃ© rendue muette", "Pour participer vocalement, demandez l'autorisation Ã  "
						+cls.getProf().getAsMention()+" avec la commande"
								,1,fieldTitle1,fieldContent1,imgLivre)).queue();
						return;
						}else {
							textChannel.sendMessage(messageBuilder("La parole Ã  Ã©tÃ© rendue Ã  la plÃ¨be", cls.getProf().getAsMention() +" vous autorise, Ãªtres insignifiants, Ã  parler sans modÃ©ration."
									+ "\n Vous Ã©coutera-t-il ? C'est une autre question.."
									,0,null,null,imgLivre)).queue();
							return;
						}
					}
				}
				return;
			
			
			default:
				fieldTitle = new String[]{"/classe help","/classe start [name]","/classe join @[nom du prof]","/classe stop","/classe quit","/classe mute","/question [question]"};
				fieldContent = new String[]{"Ouvre cette interface","Permet de crÃ©er une classe si vous Ãªtes prof. Vous devez Ãªtre connectÃ© dans un salon vocal et textuel oÃ¹ aucune classe n'est lancÃ©e,"
						+ " et prÃ©ciser un nom de classe unique.", "Permet aux Ã©lÃ¨ves de rejoindre la classe de leur professeur. Ils doivent se connecter dans le salon vocal oÃ¹ la classe est lancÃ©e.",
						"Permet aux professeurs de terminer leur classe. NÃ©cÃ©ssaire pour en dÃ©marrer une nouvelle.","Permet aux Ã©lÃ¨ves de quitter la classe","Rend muet ou rend la parole aux Ã©lÃ¨ves" ,
						"Permet aux Ã©lÃ¨ves d'une classe de demander la parole au professeur pour pouvoir poser une question."};
				textChannel.sendMessage(messageBuilder("Aide", "Liste des diffÃ©rentes commandes :",7,fieldTitle,fieldContent,
						imgLivre)).queue();
				return;
		}
	}
	@Command(name = "question", type = ExecutorType.USER)
	private void question(User user, TextChannel textChannel, Guild guild, String command) {
		String[] args = command.split(" ",2);
		Member member = guild.getMember(user);
		if(ClassBot.getMemberClasses().containsKey(member) && ClassBot.getMemberClasses().get(member).getUsers().contains(user)){
			if(!ClassBot.getMemberClasses().get(member).getProf().getId().equals(user.getId())) {
				Classe cl = ClassBot.getMemberClasses().get(member);
				if(member.getVoiceState().inVoiceChannel() && cl.getVoiceChannel().getId().equals(member.getVoiceState().getChannel().getId())) {
					if(textChannel.getId().equals(cl.getTextChannel().getId())) {
						if(args.length >1) {
							String subject = args[1];
							Question question = new Question(user, subject, guild);
							if(cl.getQuestionByMember(member) == null) {
								cl.addQuestion(question);
								textChannel.sendMessage(messageBuilder(user.getName()+" a une question !",cl.getProf().getAsMention()+" "+subject+" "
										+ "\n Pour l'autoriser Ã  parler, cliquez sur : :white_check_mark: sinon, cliquez sur : :x: \n Pour supprimer votre question "+user.getAsMention()+" cliquez sur :x:"
										+ "\n si vous souhaitez le faire taire cliquez sur la :x:",
										0,null,null,imgLivre)).queue(message -> {
											message.addReaction("âœ…").queue();
											message.addReaction("â�Œ").queue();
											cl.getQuestionByMember(member).setMessage(message);
										});
							}else {
								String[] fieldTitle = new String[]{"/question [question]"};
								String[] fieldContent = new String[]{"Vous permet de poser une question."};
								sendPrivateMessage(member.getUser(), messageBuilder("Vous ne pouvez posez qu'une seule question", 
										"Afin de pouvoir poser une nouvelle question, merci de supprimer l'ancienne en cliquant sur la :x: situÃ© sous le message de question",1, fieldTitle, fieldContent, imgError));
							}
						}else {
							String[] fieldTitle = new String[]{"/question [question]"};
							String[] fieldContent = new String[]{"Vous permet de poser une question."};
							sendPrivateMessage(member.getUser(), messageBuilder("Merci de prÃ©ciser le sujet de votre question", 
									"Afin de pouvoir poser une question, vous devez prÃ©ciser son sujet !",1, fieldTitle, fieldContent,
									imgError));
						}
					}else {
						String[] fieldTitle = new String[]{"/question [question]"};
						String[] fieldContent = new String[]{"Vous permet de poser une question."};
						sendPrivateMessage(member.getUser(), messageBuilder("Merci de poser la question dans le bon salon !", 
								"Afin de pouvoir poser une question, vous devez la poser dans le salon de votre classe, "+cl.getTextChannel().getAsMention()+" !",1, fieldTitle, fieldContent,
								imgError));
					}
				}else {
					String[] fieldTitle = new String[]{"/question [question]"};
					String[] fieldContent = new String[]{"Vous permet de poser une question."};
					sendPrivateMessage(member.getUser(), messageBuilder("Veuillez vous connecter dans un salon vocal", 
							"Afin de pouvoir poser une question, vous devez vous connecter dans le salon vocal : "+cl.getVoiceChannel().getName()+" !",1, fieldTitle, fieldContent,
							imgError));
				}
			}else {
				String[] fieldTitle = new String[]{"/question [question]"};
				String[] fieldContent = new String[]{"Vous permet de poser une question."};
				sendPrivateMessage(member.getUser(), messageBuilder("Vous ne devez pas Ãªtre le prof", 
						"Afin de pouvoir poser une question, vous ne devez pas Ãªtre le prof !",1, fieldTitle, fieldContent,
						imgError));
			}
		}else {
			String[] fieldTitle = new String[]{"/question [question]"};
			String[] fieldContent = new String[]{"Vous permet de poser une question."};
			sendPrivateMessage(member.getUser(), messageBuilder("Vous devez faire parti d'une classe !", 
					"Afin de pouvoir poser une question, vous devez faire parti d'une classe !",1, fieldTitle, fieldContent,
					imgError));
		}
	}

	private MessageEmbed messageBuilder(String title, String description,int field, String[] fieldTitle, String[] fieldContent, String thumb) {


		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(title, null);
		eb.setColor(Color.cyan);
		eb.setDescription(description);



		for(int i = 0 ; i< field;i++) {
			if(fieldTitle[i] != null && fieldContent[i] != null) {
				eb.addField(fieldTitle[i], fieldContent[i], false);
			}
		}

		eb.setFooter("ClasseBot -- Â©Valentin Lemaire -- Credits: JÃ©rÃ´me LÃ©cuyer");
		eb.setThumbnail(thumb);
		MessageEmbed message = eb.build();
		return message;
	}
	public void sendPrivateMessage(User user, MessageEmbed messageEmbed)
	{
	    user.openPrivateChannel().queue( (channel) -> channel.sendMessage(messageEmbed).queue() );
	}
}
