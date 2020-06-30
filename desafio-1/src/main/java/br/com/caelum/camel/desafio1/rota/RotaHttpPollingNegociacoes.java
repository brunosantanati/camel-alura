package br.com.caelum.camel.desafio1.rota;

import org.apache.camel.Main;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;

import com.thoughtworks.xstream.XStream;

import br.com.caelum.camel.desafio1.model.Negociacao;

public class RotaHttpPollingNegociacoes {
	
    private Main main;

    public static void main(String[] args) throws Exception {
    	RotaHttpPollingNegociacoes example = new RotaHttpPollingNegociacoes();
        example.boot();
    }

    public void boot() throws Exception {
        // create a Main instance
        main = new Main();
        // enable hangup support so you can press ctrl + c to terminate the JVM
        main.enableHangupSupport();
        // add routes
        main.addRouteBuilder(new MyRouteBuilder());

        // run until you terminate the JVM
        System.out.println("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

    private static class MyRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
        	
    		final XStream xStream = new XStream();
    		xStream.alias("negociacao", Negociacao.class);
        	
        	from("timer://negociacoes?fixedRate=true&delay=1s&period=5s").
            to("http4://argentumws-spring.herokuapp.com/negociacoes").
                convertBodyTo(String.class).
                unmarshal(new XStreamDataFormat(xStream)).
                split(body()).
                log("${body}").
            end();
	    	//setHeader(Exchange.FILE_NAME, constant("negociacoes.xml")).
	        //to("file:saida");
        }
    }

    //documentation: https://people.apache.org/~dkulp/camel/running-camel-standalone-and-have-it-keep-running.html

}
