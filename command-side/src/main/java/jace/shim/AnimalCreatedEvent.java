package jace.shim;

/**
 * Created by jaceshim on 2017. 2. 18..
 */
public class AnimalCreatedEvent {
	private final String id;
	private final String name;
	private final String description;

	public AnimalCreatedEvent(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
