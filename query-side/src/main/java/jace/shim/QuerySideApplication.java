package jace.shim;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class QuerySideApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuerySideApplication.class, args);
	}

	@ProcessingGroup("amqpEvents")
	@RestController
	public static class QueryApi {

		private ConcurrentMap<String, AtomicInteger> animals = new ConcurrentHashMap<>();

		@EventHandler
		public void on(AnimalCreatedEvent event) {
			animals.computeIfAbsent(event.getName(), k -> new AtomicInteger()).incrementAndGet();
		}

		@GetMapping
		public Map<String, AtomicInteger> getAnimals() {
			return animals;
		}

	}

	@Bean
	public SpringAMQPMessageSource animalEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {
			@RabbitListener(queues = "AnimalCreatedEvents")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				super.onMessage(message, channel);
			}
		};
	}
}
