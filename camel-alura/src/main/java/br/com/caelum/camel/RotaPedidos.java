package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
            	from("file:pedidos?delay=5s&noop=true").
            		/*split().
                		xpath("/pedido/itens/item").
	            	filter().
	                	xpath("/item/formato[text()='EBOOK']").*/
            		split(xpath("/pedido/itens/item")).
                	filter(xpath("/item/formato[text()='EBOOK']")).
	            	marshal(). //queremos transformar a mensagem em outro formato
	                	xmljson(). //de xml para json
	            	log("${exchange.pattern}").
	            	log("${id} - ${body}").
	            	//setHeader("CamelFileName", simple("${file:name.noext}.json")).
	            	//setHeader("CamelFileName", simple("${id}.json")).
	            	setHeader(Exchange.FILE_NAME, simple("${file:name.noext}-${header.CamelSplitIndex}.json")).
	            	//setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.POST)).
	            	setHeader(Exchange.HTTP_QUERY, constant("clienteId=breno@abc.com&pedidoId=123&ebookId=ARQ")).
                to("http4://localhost:8080/webservices/ebook/item");
            	//link documentacao: http://camel.apache.org/file2.html
            }
            
		});
		
		context.start(); //aqui camel realmente começa a trabalhar
        Thread.sleep(2000); //esperando um pouco para dar um tempo para camel
        context.stop();

	}
}

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
