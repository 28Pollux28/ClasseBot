package main.java.command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import main.java.ClassBotTest;
import main.java.command.Command.ExecutorType;
import main.java.command.defaut.CommandDefault;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public final class CommandMapTest {

	private final ClassBotTest classBot;
	private final Map<String, SimpleCommandTest> commands = new HashMap<>();
	private final String tag = "/";
	
	public CommandMapTest(ClassBotTest classBot) {
		this.classBot = classBot;
		registerCommand(new CommandDefaultTest(classBot));
	}
	
	public String getTag() {
		return tag;
	}
	
	public Collection<SimpleCommandTest> getCommands(){
		return commands.values();
	}
	
	public void registerCommands(Object...objects){
		for(Object object : objects) registerCommand(object);
	}
	
	public void registerCommand(Object object){
		for(Method method : object.getClass().getDeclaredMethods()){
			if(method.isAnnotationPresent(CommandTest.class)){
				CommandTest command = method.getAnnotation(CommandTest.class);
				method.setAccessible(true);
				SimpleCommandTest simpleCommand = new SimpleCommandTest(command.name(), command.description(), command.type(), object, method);
				commands.put(command.name(), simpleCommand);
			}
		}
	}
	
	public void commandConsole(String command){
		Object[] object = getCommand(command);
		if(object[0] == null || ((SimpleCommandTest)object[0]).getExecutorType() == ExecutorType.USER){
			System.out.println("Commande inconnue.");
			return;
		}
		try{
			execute(((SimpleCommandTest)object[0]), command, (String[])object[1], null);
		}catch(Exception exception){
			System.out.println("La methode "+((SimpleCommandTest)object[0]).getMethod().getName()+" n'est pas correctement initialisée.");
		}
	}
	
	public boolean commandUser(User user, String command, Message message){
		Object[] object = getCommand(command);
		if(object[0] == null || ((SimpleCommandTest)object[0]).getExecutorType() == ExecutorType.CONSOLE) return false;
		try{
			execute(((SimpleCommandTest)object[0]), command,(String[])object[1], message);
		}catch(Exception exception){
			System.out.println("La methode "+((SimpleCommandTest)object[0]).getMethod().getName()+" n'est pas correctement initialisée.");
		}
		return true;
	}
	
	private Object[] getCommand(String command){
		String[] commandSplit = command.split(" ");
		String[] args = new String[commandSplit.length-1];
		for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];
		SimpleCommandTest simpleCommand = commands.get(commandSplit[0]);
		return new Object[]{simpleCommand, args};
	}
	
	private void execute(SimpleCommandTest simpleCommand, String command, String[] args, Message message) throws Exception{
		Parameter[] parameters = simpleCommand.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			if(parameters[i].getType() == String[].class) objects[i] = args;
			else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
			else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
			else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
			else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
			else if(parameters[i].getType() == String.class) objects[i] = command;
			else if(parameters[i].getType() == Message.class) objects[i] = message;
			else if(parameters[i].getType() == JDA.class) objects[i] = classBot.getJda();
			else if(parameters[i].getType() == MessageChannel.class) objects[i] = message.getChannel();
		}
		simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
	}
}