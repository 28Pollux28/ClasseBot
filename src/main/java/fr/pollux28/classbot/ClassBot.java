package fr.pollux28.classbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import fr.pollux28.classbot.command.CommandMap;
import fr.pollux28.classbot.event.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class ClassBot implements Runnable{
	static public ArrayList<Classe> classes = new ArrayList<Classe>();
	private final JDA jda;
	//private final JDA jdaB;
	private final CommandMap commandMap = new CommandMap(this);
	private boolean running;
	private final Scanner scanner = new Scanner(System.in);
	static public HashMap<Member,Classe> memberClasses = new HashMap<Member,Classe>();
	public ClassBot() throws LoginException {
		/*jda = new JDABuilder(AccountType.BOT)
			.setToken(System.getenv("TOKEN"))
			.setActivity(Activity.watching("/classe help"))
			.build();*/
		jda = JDABuilder.create(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.setToken(System.getenv("TOKEN"))
				.setActivity(Activity.watching("/classe help pour les commandes"))
				.build();
		jda.addEventListener(new BotListener(commandMap));
	}
	
	public static ArrayList<Classe> getClasses(){
		return classes;
		
	}
	public static HashMap<Member,Classe> getMemberClasses(){
		return memberClasses;
	}
	
	@Override
	public void run() {
		running = true;
		while(running) {
			if(scanner.hasNextLine()) {
				commandMap.commandConsole(scanner.nextLine());
			}
		}
		scanner.close();
		System.out.println("Bot Stopped");
		jda.shutdown();
		System.exit(0);
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public JDA getJda() {
		return jda;
	}
	
	public static void main(String[] args) {
		 
		try {
			ClassBot classBot = new ClassBot();
			System.out.println("Hello World !");
			new Thread(classBot, "bot").start();
		} catch (Exception e) {
		}
		
			
	}
}
