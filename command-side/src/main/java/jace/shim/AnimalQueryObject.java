package jace.shim;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by jaceshim on 2017. 2. 18..
 */
@Entity
public class AnimalQueryObject {

	@Id
	private String id;

	private String name;

	private String description;

	public AnimalQueryObject() {
	}

	public AnimalQueryObject(String id, String name, String description) {
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
