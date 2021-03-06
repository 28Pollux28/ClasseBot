package fr.pollux28.classbot.command;

import java.lang.reflect.Method;

import fr.pollux28.classbot.command.Command.ExecutorType;

public final class SimpleCommand {

	public final String name, description;
	public final ExecutorType type;
	private final Object object;
	private final Method method;

	public SimpleCommand(String name, String description, ExecutorType type, Object object, Method method) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
		this.object = object;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ExecutorType getExecutorType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}

}
