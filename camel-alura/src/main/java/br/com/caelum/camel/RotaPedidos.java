package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
            	from("file:pedidos?delay=5s&noop=true").
            	marshal(). //queremos transformar a mensagem em outro formato
                	xmljson(). //de xml para json
            	log("${exchange.pattern}").
            	log("${id} - ${body}").
                to("file:saida");
            	//link documentacao: http://camel.apache.org/file2.html
            }
            
		});
		
		context.start(); //aqui camel realmente começa a trabalhar
        Thread.sleep(2000); //esperando um pouco para dar um tempo para camel
        context.stop();

	}
}

/*
 * O fluxo pode ser bidirecional sim!
 * No mundo dos padrões de integração (EIP), o exemplo unidirecional é chamado de Event Message ou InOnly. 
 * O exemplo bidirecional é chamado de Request-Reply ou InOut. 
 * Event Message e Request-Reply são Message Exchange Pattens (MEP).
 * http://camel.apache.org/exchange-pattern.html
 * 
 * Link com conteúdo do livro "Enterprise Integration Patterns":
 * https://www.enterpriseintegrationpatterns.com/index.html
 */
