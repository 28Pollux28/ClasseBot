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
		if(args[1].equalsIgnoreCase("start")) {
			boolean hasPermission = false;
			List<Role> roles = guild.getMember(user).getRoles();
			for (Role role : roles) {
				if(role.getName().equalsIgnoreCase("prof")) {
					hasPermission = true;
				}
			}
			if(!hasPermission) {
				String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
				String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
				textChannel.sendMessage(messageBuilder("Erreur", "Vous devez être un prof pour démarrer un cours "+ user.getAsMention(),
						1,fieldTitle,fieldContent,"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
				return;
			}
			
			if(args.length == 3) {
				String name = args[2];
				
				VoiceChannel vc = guild.getMember(user).getVoiceState().getChannel();
				if(vc== null) {
					String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
					String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
					textChannel.sendMessage(messageBuilder("Erreur", "Merci de vous connecter sur un channel vocal pour démarrer un cours "+ user.getAsMention(),
							1,fieldTitle,fieldContent,"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
					return;
				}
				Classe classe = new Classe(name, user, guild, vc, textChannel);
				classe.setUsers(new ArrayList<User>());
				classe.addUser(user, guild.getMember(user));
				for (Classe cl : ClassBot.getClasses()) {
					if(cl.getGuild().getId().equals(classe.getGuild().getId())) {
						if(cl.getName().equalsIgnoreCase(classe.getName())) {
							String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
							String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", "Une classe nommée __**"+ classe.getName().toUpperCase()+
									"**__ existe déjà ! \n Choississez un autre nom "+ classe.getProf().getAsMention()
									+" !", 1, fieldTitle, fieldContent,"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
							return;
						}
						if(cl.getProf().getName().equals(classe.getProf().getName())) {
							String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
							String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", classe.getProf().getAsMention() + " Vous êtes actuellement professeur dans "+ cl.getName()+" sur le salon "
									+ cl.getTextChannel().getAsMention()+"\n Fermez cette classe avec /classe stop avant d'en lancer une nouvelle !", 1, fieldTitle, fieldContent,
									"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
							return;
						}
						if(cl.getVoiceChannel() == classe.getVoiceChannel()) {
							String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
							String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", classe.getProf().getAsMention()+" Une classe est déjà présente dans ce salon vocal "
									+classe.getVoiceChannel().getName()+ "\n Choisissez un autre salon vocal pour pouvoir lancer votre classe.",1,fieldTitle,fieldContent,
									"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
							return;
						}
						if(cl.getTextChannel() == classe.getTextChannel()) {
							String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
							String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
							classe.getTextChannel().sendMessage(messageBuilder("Erreur", classe.getProf().getAsMention()+" Une classe est déjà présente dans ce salon textuel "
									+classe.getTextChannel().getAsMention()+ "\n Choisissez un autre salon textuel pour pouvoir lancer votre classe.",1,fieldTitle,fieldContent,
									"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
							return;
						}
					}

				}

				ClassBot.getClasses().add(classe);
				for(Member member : classe.getVoiceChannel().getMembers()) {
					if(guild.getSelfMember().canInteract(member)){
						if(!classe.getUsers().contains(member.getUser())) {
							guild.kickVoiceMember(member).queue();
							String[] fieldTitle = {"/classe join @[nom du prof]"};
							String[] fieldContent = {"Vous permet de rejoindre la classe de votre professeur."};
							sendPrivateMessage(member.getUser(), messageBuilder("Vous avez été déconnecté", "Une classe a été lancée dans ce salon "+classe.getVoiceChannel().getName()
									+ "\nPour rejoindre le cours utilisez la commande :",1, fieldTitle, fieldContent,
									"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
						}
					}else {
						String[] fieldTitle = {"/classe join @[nom du prof]"};
						String[] fieldContent = {"Vous permet de rejoindre la classe de votre professeur."};
						if(!classe.getUsers().contains(member.getUser())) sendPrivateMessage(member.getUser(), messageBuilder("Merci de vous déconnecter "+member.getUser().getAsTag(),
								"Un cours a débuté dans le salon "+
						classe.getVoiceChannel().getName()+"\nPour ne pas le perturber, merci de vous déconnecter. \n"
								+ "Pour rejoindre le cours utilisez la commande :", 1, fieldTitle, fieldContent
								,"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
					}
				}
				String[] fieldTitle = {"Pour arrêter la classe, "+ classe.getProf().getName()+" faites : "};
				String[] fieldContent = {"/classe stop"};
				classe.getTextChannel().sendMessage(guild.getRolesByName("@everyone", true).get(0).getAsMention()).queue();
				classe.getTextChannel().sendMessage(messageBuilder("Classe créée ! ", "Tous les élèves souhaiant rejoindre la classe __**" 
						+classe.getName().toUpperCase()+"**__ de "+classe.getProf().getAsMention() +" dans le salon vocal __**"+classe.getVoiceChannel().getName() +
						"**__ doivent écrire /classe join "+classe.getProf().getAsMention()+
						" dans " + classe.getTextChannel().getAsMention() + "et se connecter dans le salon vocal " +classe.getVoiceChannel().getName(),1,fieldTitle,fieldContent,
						"http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png")).queue();
				return;
			}else {
				textChannel.sendMessage(messageBuilder("Erreur Syntaxe", user.getAsMention() + " La comande est du format /classe start name", 0, null, null,
						"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
				return;

			}
		}else if(args[1].equalsIgnoreCase("stop")) {
			boolean hasPermission = false;
			List<Role> roles = guild.getMember(user).getRoles();
			for (Role role : roles) {
				if(role.getName().equalsIgnoreCase("prof")) {
					hasPermission = true;
				}
			}
			if(!hasPermission) {
				String[] fieldTitle = {"/classe [help/start/stop/join/quit] [name]"};
				String[] fieldContent = {"Permet d'effectuer des actions sur la classe"};
				textChannel.sendMessage(messageBuilder("Erreur", "Vous devez être un prof pour arrêter un cours "+ user.getAsMention(),
						1,fieldTitle,fieldContent,"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
				return;
			}
			for(Classe cl : ClassBot.getClasses()) {
				if(cl.getGuild().getId().equals(guild.getId()) && cl.getProf().getId().equals(user.getId()) && cl.getTextChannel() == textChannel) {
					for (User usert : cl.getUsers()) {
						ClassBot.getMemberClasses().remove(guild.getMember(usert));
						if(guild.getSelfMember().canInteract(guild.getMember(usert))&&guild.getMember(usert).getVoiceState().inVoiceChannel()){
							guild.kickVoiceMember(guild.getMember(usert));
						}
					}
					for(Question q :cl.getQuestions()) {
						q.getMessage().delete().queue();
						cl.getQuestions().remove(q);
					}
					
					ClassBot.getClasses().remove(cl);
					textChannel.sendMessage(messageBuilder("Classe terminée !", "La classe __**"+cl.getName().toUpperCase()+"**__ de"+ user.getAsMention()+" est maintenant terminée ! "
							+ "\nN'oubliez pas de faire vos devoirs !",
							0,null,null,"http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png")).queue();
					return;
				}
			}

		}else if(args[1].equalsIgnoreCase("join")) {
			if(args[2] != null) {
				if(!ClassBot.getMemberClasses().containsKey(guild.getMember(user))) {
						for (Classe cl : ClassBot.getClasses()) {
							if(cl.getGuild().getId().equals(guild.getId()) && cl.getProf().getAsMention().equals(args[2])) {
								cl.addUser(user, guild.getMember(user));
								textChannel.sendMessage(messageBuilder(user.getName()+" a rejoint la classe!", "La classe __**"+cl.getName().toUpperCase()+"**__ de"+ cl.getProf().getAsMention()
										+" peut maintenant commencer ! \n __**N'oublie pas de te connecter dans le salon vocal "+cl.getVoiceChannel().getName()
										+ "**__\nAller, au travail !",
										0,null,null,"http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png")).queue();
								return;
							}
						}
						textChannel.sendMessage(messageBuilder("Erreur", "Le nom précisé est incorrect. Vérifiez le nom et recommencez."
								+ "\n Courage vous pourrez bientôt travailler :wink:",0, null,null,
								"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
						return;
					
				}else {
					String[] fieldTitle = {"/classe quit"};
					String[] fieldContent = {"Vous permet de quitter la classe de votre professeur."};
					textChannel.sendMessage(messageBuilder("Vous faîtes déjà parti d'une classe", "Vous devez quitter votre ancienne classe avant de pouvoir en rejoindre une autre "
					+ "\nPour quitter le cours utilisez la commande :",1, fieldTitle, fieldContent,
					"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png")).queue();
					return;
				}
			}
		}else if(args[1].equalsIgnoreCase("quit")) {
			Member member =guild.getMember(user);
			if(ClassBot.getMemberClasses().containsKey(member)) {
				Classe cl = ClassBot.getMemberClasses().get(member);
				if(!ClassBot.getMemberClasses().get(member).getProf().getId().equals(user.getId())) {
					if(member.getVoiceState().inVoiceChannel() && member.getVoiceState().getChannel().getId().equals(cl.getVoiceChannel().getId())) {
						member.mute(false).queue();
						guild.kickVoiceMember(member).queue();
					}
					ClassBot.getMemberClasses().get(member).removeUser(member.getUser());
					ClassBot.memberClasses.remove(member);
					textChannel.sendMessage(messageBuilder(user.getName()+" a quitté la classe!","Est-ce un déserteur ? Il n'aimait plus le doux son de la voix de "+cl.getProf()+" ?"
					+"\n Les autres, retournez au travail !",0,null,null,"http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png")).queue();
					return;
				}else {
					sendPrivateMessage(member.getUser(), messageBuilder("Vous ne devez pas être le prof !", 
							"Vous ne pouvez pas quitter la classe si vous êtes le prof. Vous pouvez cependant la terminer avec /classe stop!",0, null, null,
							"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
					return;
				}
			}else {
				String[] fieldTitle = {"/classe join @[nom_du_prof]"};
				String[] fieldContent = {"Vous permet de poser une question."};
				sendPrivateMessage(member.getUser(), messageBuilder("Vous devez faire parti d'une classe !", 
						"Afin de pouvoir quitter une classe, vous devez faire parti d'une classe !",1, fieldTitle, fieldContent,
						"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
				return;
			}
			
		}else if(args[1].equalsIgnoreCase("help")) {
			String[] fieldTitle = {"/classe help","/classe start [name]","/classe join @[nom du prof]","/classe stop","/classe quit","/question [question]"};
			String[] fieldContent = {"Ouvre cette interface","Permet de créer une classe si vous êtes prof. Vous devez être connecté dans un salon vocal et textuel où aucune classe n'est lancée,"
					+ " et préciser un nom de classe unique.", "Permet aux élèves de rejoindre la classe de leur professeur. Ils doivent se connecter dans le salon vocal où la classe est lancée.",
					"Permet aux professeurs de terminer leur classe. Nécéssaire pour en démarrer une nouvelle.","Permet aux élèves de quitter la classe", "Permet aux élèves d'une classe de "
							+ "demander la parole au professeur pour pouvoir poser une question."};
			textChannel.sendMessage(messageBuilder("Aide", "Liste des différentes commandes :",6,fieldTitle,fieldContent,
					"http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png")).queue();
		}
	}
	@Command(name = "question", type = ExecutorType.USER)
	private void question(User user, TextChannel textChannel, Guild guild, String command) {
		String[] args = command.split(" ",2);
		Member member = guild.getMember(user);
		if(ClassBot.getMemberClasses().containsKey(guild.getMember(user)) && ClassBot.getMemberClasses().get(guild.getMember(user)).getUsers().contains(user)){
			if(!ClassBot.getMemberClasses().get(guild.getMember(user)).getProf().getId().equals(user.getId())) {
				Classe cl = ClassBot.getMemberClasses().get(member);
				if(member.getVoiceState().inVoiceChannel() && cl.getVoiceChannel().getId().equals(member.getVoiceState().getChannel().getId())) {
					if(textChannel.getId().equals(cl.getTextChannel().getId())) {
						if(args.length >1) {
							String subject = args[1];
							Question question = new Question(user, subject, guild);
							if(cl.getQuestionByMember(member) == null) {
								cl.addQuestion(question);
								textChannel.sendMessage(messageBuilder(user.getName()+" a une question !",cl.getProf().getAsMention()+" "+subject+" "
										+ "\n Pour l'autoriser à parler, cliquez sur : :white_check_mark: sinon, cliquez sur : :x: \n Pour supprimer votre question "+user.getAsMention()+" cliquez sur :x:"
										+ "\n si vous souhaitez le faire taire cliquez sur la :x:",
										0,null,null,"http://img.over-blog-kiwi.com/1/67/67/20/20150628/ob_891119_livres.png")).queue(message -> {
											message.addReaction("✅").queue();
											message.addReaction("❌").queue();
											cl.getQuestionByMember(member).setMessage(message);
										});
							}else {
								String[] fieldTitle = {"/question [question]"};
								String[] fieldContent = {"Vous permet de poser une question."};
								sendPrivateMessage(member.getUser(), messageBuilder("Vous ne pouvez posez qu'une seule question", 
										"Afin de pouvoir poser une nouvelle question, merci de supprimer l'ancienne en cliquant sur la :x: situé sous le message de question",1, fieldTitle, fieldContent,
										"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
							}
						}else {
							String[] fieldTitle = {"/question [question]"};
							String[] fieldContent = {"Vous permet de poser une question."};
							sendPrivateMessage(member.getUser(), messageBuilder("Merci de préciser le sujet de votre question", 
									"Afin de pouvoir poser une question, vous devez préciser son sujet !",1, fieldTitle, fieldContent,
									"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
						}
					}else {
						String[] fieldTitle = {"/question [question]"};
						String[] fieldContent = {"Vous permet de poser une question."};
						sendPrivateMessage(member.getUser(), messageBuilder("Merci de poser la question dans le bon salon !", 
								"Afin de pouvoir poser une question, vous devez la poser dans le salon de votre classe, "+cl.getTextChannel().getAsMention()+" !",1, fieldTitle, fieldContent,
								"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
					}
				}else {
					String[] fieldTitle = {"/question [question]"};
					String[] fieldContent = {"Vous permet de poser une question."};
					sendPrivateMessage(member.getUser(), messageBuilder("Veuillez vous connecter dans un salon vocal", 
							"Afin de pouvoir poser une question, vous devez vous connecter dans le salon vocal : "+cl.getVoiceChannel().getName()+" !",1, fieldTitle, fieldContent,
							"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
				}
			}else {
				String[] fieldTitle = {"/question [question]"};
				String[] fieldContent = {"Vous permet de poser une question."};
				sendPrivateMessage(member.getUser(), messageBuilder("Vous ne devez pas être le prof", 
						"Afin de pouvoir poser une question, vous ne devez pas être le prof !",1, fieldTitle, fieldContent,
						"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
			}
		}else {
			String[] fieldTitle = {"/question [question]"};
			String[] fieldContent = {"Vous permet de poser une question."};
			sendPrivateMessage(member.getUser(), messageBuilder("Vous devez faire parti d'une classe !", 
					"Afin de pouvoir poser une question, vous devez faire parti d'une classe !",1, fieldTitle, fieldContent,
					"https://dab1nmslvvntp.cloudfront.net/wp-content/uploads/2015/12/1450973046wordpress-errors.png"));
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

		eb.setFooter("ClasseBot -- ©Valentin Lemaire");
		eb.setThumbnail(thumb);
		MessageEmbed message = eb.build();
		return message;
	}
	public void sendPrivateMessage(User user, MessageEmbed messageEmbed)
	{
	    user.openPrivateChannel().queue( (channel) -> channel.sendMessage(messageEmbed).queue() );
	}
}