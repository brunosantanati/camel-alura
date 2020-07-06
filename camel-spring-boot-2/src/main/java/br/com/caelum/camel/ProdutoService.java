package br.com.caelum.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:pedidos?noop=true").
        	log("${body}").
        to("activemq:queue:pedidos");
    }
    
}