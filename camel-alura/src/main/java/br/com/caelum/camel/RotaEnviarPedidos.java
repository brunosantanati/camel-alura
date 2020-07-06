package br.com.caelum.camel;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaEnviarPedidos {
	
	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();
		context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("file:pedidos?noop=true").
					log("${body}").
				to("activemq:queue:pedidos");
			}
		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
