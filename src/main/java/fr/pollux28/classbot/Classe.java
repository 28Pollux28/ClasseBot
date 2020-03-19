package fr.pollux28.classbot;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Classe {
	
	protected String name;
	protected User prof;
	protected ArrayList<User> users;
	protected VoiceChannel voiceChannel;
	protected TextChannel textChannel;
	protected Guild guild;
	protected ArrayList<Question> questions; 
	
	public Classe(String name, User prof, Guild guild, VoiceChannel voiceChannel, TextChannel textChannel) {
		this.name = name;
		this.prof = prof;
		this.guild = guild;
		this.voiceChannel = voiceChannel;
		this.textChannel = textChannel;
		this.questions = new ArrayList<Question>();
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
	public void addQuestion(Question question) {
			this.questions.add(question);
	}
	public Question getQuestionByMember(Member member) {
		for(Question q : questions) {
			if (q.getGuild().getMember(q.getUser()).getId().equals(member.getId())) {
				return q;
			}
		}
	return null;
	}
	public Question getQuestionByMessageID(String messageID) {
		for(Question q : questions) {
			if (q.getMessage().getId().equals(messageID)) {
				return q;
			}
		}
	return null;
	}
	public void removeQuestion(Question question) {
		if(this.questions.contains(question)) {
			question.getMessage().delete().queue();
			this.questions.remove(question);
		}
	}
	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getProf() {
		return prof;
	}

	public void setProf(User prof) {
		this.prof = prof;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	public boolean addUser(User user,Member member) {
		if(!this.users.contains(user)) {
			this.users.add(user);
		}else return false;
		if(!ClassBot.getMemberClasses().containsKey(member)) {
		ClassBot.getMemberClasses().put(member, this);
		return true;
		}else return false;
	}
	public boolean removeUser(User user) {
		if(this.users.contains(user)) {
			this.users.remove(user);
			return true;
		}
		return false;
	}

	public VoiceChannel getVoiceChannel() {
		return voiceChannel;
	}

	public void setVoiceChannel(VoiceChannel voiceChannel) {
		this.voiceChannel = voiceChannel;
	}

	public TextChannel getTextChannel() {
		return textChannel;
	}

	public void setTextChannel(TextChannel textChannel) {
		this.textChannel = textChannel;
	}

	public User getUsers(User user) {
		for (User user1 : users) {
			if(user1.getId().equals(user.getId())){
				return user1;
			}
		} 
		return null;
	}
	
	
}
