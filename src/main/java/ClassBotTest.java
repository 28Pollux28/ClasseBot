package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import main.java.command.CommandMapTest;
import main.java.event.BotListenerTest;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;

public class ClassBotTest implements Runnable {
	static public ArrayList<ClasseTest> classes = new ArrayList<ClasseTest>();
	//private final JDA jda;
	private final CommandMapTest commandMap = new CommandMapTest(this);
	private boolean running;
	private final Scanner scanner = new Scanner(System.in);
	static public HashMap<Member,ClasseTest> memberClasses = new HashMap<Member,ClasseTest>();
	
	public ClassBotTest() throws LoginException {
		System.out.println("Bot is Starting...");
		//jda = new JDABuilder(AccountType.BOT).setToken(process.env.TOKEN).build();
		//jda.addEventListener(new BotListenerTest(commandMap));
		System.out.println("Started");
	}
	
	public static ArrayList<ClasseTest> getClasses(){
		return classes;
		
	}
	public static HashMap<Member,ClasseTest> getMemberClasses(){
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
		//jda.shutdown();
		System.exit(0);
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public JDA getJda() {
		//return jda;
	}
	
	public static void main(String[] args) {
		System.out.println("Bot is Starting... (main)");
		 
		try {
			ClassBotTest classBot = new ClassBotTest();
			new Thread(classBot, "bot").start();
		} catch (Exception e) {}
		
		System.out.println("Hello World !");	
	}
}
