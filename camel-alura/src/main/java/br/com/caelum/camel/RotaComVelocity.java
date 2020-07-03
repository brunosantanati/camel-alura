package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaComVelocity {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
            	from("direct:entrada").
                	setHeader("data", constant("02/07/2020")).
                to("velocity:template.vm").
                	log("${body}");
            }
        });

        context.start();

        ProducerTemplate producer = context.createProducerTemplate();
        producer.sendBody("direct:entrada", "Apache Camel rocks!!!");

        Thread.sleep(10000);

        context.stop();
    }
	
}
