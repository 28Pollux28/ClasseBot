package fr.pollux28.classbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Question {
	protected User user;
	protected String subject;
	protected boolean isAnswering;
	protected Message message;
	protected Guild guild;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Question(User user, String subject, Guild guild) {
		this.user = user;
		this.subject = subject;
		this.guild = guild;
		this.isAnswering = false;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public boolean isAnswering() {
		return isAnswering;
	}

	public void setAnswering(boolean isAnswering) {
		this.isAnswering = isAnswering;
	}

}
