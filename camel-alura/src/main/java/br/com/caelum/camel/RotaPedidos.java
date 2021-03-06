package br.com.caelum.camel;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		
		context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));
		
		context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
            	errorHandler(
           		    //deadLetterChannel("file:erro").
            		deadLetterChannel("activemq:queue:pedidos.DLQ"). //usando Dead Letter Queue (DLQ)
           		    useOriginalMessage(). //guardar mensagem original e nao a mensagem transformada
           		    logExhaustedMessageHistory(true).
           		    maximumRedeliveries(3).
           		    redeliveryDelay(1000).
           		    onRedelivery(new Processor() {            
						@Override
						public void process(Exchange exchange) throws Exception {
							int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
							int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
							System.out.println("Redelivery - " + counter + "/" + max );
						}
           		    })
            	);
//            	onException(Exception.class).
//                	handled(true).
//                	to("file:error-parsing").
//                    maximumRedeliveries(3).
//                    redeliveryDelay(4000).
//                    onRedelivery(new Processor() {
//
//                        @Override
//                        public void process(Exchange exchange) throws Exception {
//                                int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
//                                int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
//                                System.out.println("Redelivery - " + counter + "/" + max );
//                        }
//                });
            	//from("file:pedidos?delay=5s&noop=true").
            	from("activemq:queue:pedidos"). //usamos o componente activemq, consumindo da fila pedidos
            		log("${file:name}").
            		routeId("rota-pedidos").
            		delay(1000).
            		to("validator:pedido.xsd").
            		multicast().
            			parallelProcessing().
	            			to("direct:soap").
			            	to("direct:http");
            	
            	from("direct:soap").
                	routeId("rota-soap").
                to("xslt:pedido-para-soap.xslt").
	                log("Resultado do template: ${body}").
	                setHeader(Exchange.CONTENT_TYPE,constant("text/xml")).
	            to("http4://localhost:8080/webservices/financeiro");
            	
            	from("direct:http").
            		routeId("rota-http").
	            	setProperty("pedidoId", xpath("/pedido/id/text()")).
	                setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()")).
            		split().
                		xpath("/pedido/itens/item").
	            	filter().
	                	xpath("/item/formato[text()='EBOOK']").
                	setProperty("ebookId", xpath("/item/livro/codigo/text()")).
	            	marshal().
	                	xmljson().
	            	log("${exchange.pattern}").
	            	log("${id} - ${body}").
	            	//setHeader("CamelFileName", simple("${file:name.noext}.json")).
	            	//setHeader("CamelFileName", simple("${id}.json")).
	            	setHeader(Exchange.FILE_NAME, simple("${file:name.noext}-${header.CamelSplitIndex}.json")).
	            	//setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.POST)).
	            	setHeader(Exchange.HTTP_QUERY, 
	            		simple("clienteId=${property.clienteId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}")).
                to("http4://localhost:8080/webservices/ebook/item");
            	
            }
            
		});
		
		context.start(); //aqui camel realmente começa a trabalhar
        Thread.sleep(60000); //esperando um pouco para dar um tempo para camel
        context.stop();

	}
}

//link documentacao: http://camel.apache.org/file2.html

/*
 * Message Exchange Pattens
 * 
 * O fluxo pode ser bidirecional sim!
 * No mundo dos padrões de integração (EIP), o exemplo unidirecional é chamado de Event Message ou InOnly. 
 * O exemplo bidirecional é chamado de Request-Reply ou InOut. 
 * Event Message e Request-Reply são Message Exchange Pattens (MEP).
 * http://camel.apache.org/exchange-pattern.html
 * 
 * Link com conteúdo do livro "Enterprise Integration Patterns":
 * https://www.enterpriseintegrationpatterns.com/index.html
 */

/*
 * Marshal
 * 
 * O interessante é que todos os métodos adicionados nas opções desse exercício também existem. 
 * O método xstream() é responsável por gerar um XML, a partir do objeto Java. 
 * O método serialization() usa a serialização do Java IO e o método jacksonxml() 
 * transforma um JSON em um XML (usando a biblioteca de JSON Jackson). 
 */
