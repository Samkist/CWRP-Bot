package dev.samkist.renzhe.data;

public class DepartmentContext {
	private final String name;
	private final String command;
	private final String supervisor;
	private final String invterviewer;
	private final String department;
	private final String interview;


	public DepartmentContext(String name, String command, String supervisor, String invterviewer, String department, String interview) {
		this.name = name;
		this.command = command;
		this.supervisor = supervisor;
		this.invterviewer = invterviewer;
		this.department = department;
		this.interview = interview;
	}

	public String getName() {
		return name;
	}

	public String getCommand() {
		return command;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public String getInvterviewer() {
		return invterviewer;
	}

	public String getDepartment() {
		return department;
	}

	public String getInterview() {
		return interview;
	}
}
