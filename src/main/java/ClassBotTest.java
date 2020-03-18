//package fr.pollux28.classbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

//import fr.pollux28.classbot.command.CommandMap;
//import fr.pollux28.classbot.event.BotListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;

public class ClassBotTest implements Runnable {
	//static public ArrayList<Classe> classes = new ArrayList<Classe>();
	/*private final JDA jda;
	private final CommandMap commandMap = new CommandMap(this);*/
	private boolean running;
	/*private final Scanner scanner = new Scanner(System.in);
	//static public HashMap<Member,Classe> memberClasses = new HashMap<Member,Classe>();
	
	public ClassBotTest() throws LoginException {
		System.out.println("Bot is Starting...");
		jda = new JDABuilder(AccountType.BOT).setToken(process.env.TOKEN).build();
		//jda.addEventListener(new BotListener(commandMap));
		System.out.println("Started");
	}*/
	
	/*public static ArrayList<Classe> getClasses(){
		return classes;
	}
	public static HashMap<Member,Classe> getMemberClasses(){
		return memberClasses;
	}*/
	
	@Override
	public void run() {
		running = true;
		/*while(running) {
			if(scanner.hasNextLine()) {
				//commandMap.commandConsole(scanner.nextLine());
        			scanner.nextLine();//temporaire, retirer quant commandMap sera dispo
			}
		}
		scanner.close();*/
		System.out.println("Bot Stopped");
		//jda.shutdown();
		System.exit(0);
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/*public JDA getJda() {
		return jda;
	}*/
	
	public static void main(String[] args) {
		System.out.println("Bot is Starting... (main)");
		 
		try {
			ClassBotTest classBot = new ClassBotTest();
			new Thread(classBotTest, "botTest").start();
		} catch (Exception e) {}
		
		System.out.println("Hello World !");	
	}
}
