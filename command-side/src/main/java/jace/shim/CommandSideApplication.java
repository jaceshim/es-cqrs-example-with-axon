package jace.shim;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@SpringBootApplication
public class CommandSideApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommandSideApplication.class, args);
	}

	@RestController
	public static class AnimalApi {

		@Autowired
		AnimalRepository animalRepository;

		@Autowired
		CommandGateway commandGateway;

		@PostMapping
		public CompletableFuture<String> addAnimal(@RequestBody Map<String, String> request) {
			String id = UUID.randomUUID().toString();
			return commandGateway.send(new AnimalCreatedCommand(id, request.get("name"), request.get("description")));
		}

		@GetMapping("/{id}")
		public AnimalQueryObject find(@PathVariable String id) {
			return animalRepository.findOne(id);
		}

		@GetMapping
		public List<AnimalQueryObject> findAll() {
			return animalRepository.findAll();
		}


		@Aggregate
		public static class Animal {

			@AggregateIdentifier
			private String id;

			public Animal() {
			}

			@CommandHandler
			public Animal(AnimalCreatedCommand cmd) {
				Assert.hasLength(cmd.getDescription());

				apply(new AnimalCreatedEvent(cmd.getId(), cmd.getName(), cmd.getDescription()));
			}

			@EventSourcingHandler
			public void on(AnimalCreatedEvent event) {
				this.id = event.getId();
			}
		}

		@Component
		public static class AnimalQueryObjectUpdater {

			@Autowired
			private AnimalRepository animalRepository;

			@EventHandler
			public void on(AnimalCreatedEvent event) {
				animalRepository.save(new AnimalQueryObject(event.getId(), event.getName(), event.getDescription()));
			}
		}

		public static class AnimalCreatedCommand {

			private final String id;
			private final String name;
			private final String description;

			public AnimalCreatedCommand(String id, String name, String description) {
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

		@Bean
		public Exchange exchange() {
			return ExchangeBuilder.fanoutExchange("AnimalCreatedEvents").build();
		}

		@Bean
		public Queue queue() {
			return QueueBuilder.durable("AnimalCreatedEvents").build();
		}

		@Bean
		public Binding binding() {
			return BindingBuilder.bind(queue()).to(exchange()).with("*").noargs();
		}

		@Autowired
		public void configure(AmqpAdmin admin) {
			admin.declareExchange(exchange());
			admin.declareQueue(queue());
			admin.declareBinding(binding());
		}
	}
}
