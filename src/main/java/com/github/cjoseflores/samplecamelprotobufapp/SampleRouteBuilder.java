package com.github.cjoseflores.samplecamelprotobufapp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.protobuf.AddressBookProtos;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class SampleRouteBuilder extends RouteBuilder {
    private final static String TOPIC = "jms:topic:foo";

    @Override
    public void configure() {
        from("timer:generateAddressBook")
                .id("generateAddressBookRoute")
                .process(exchange -> {
                    AddressBookProtos.Person person = AddressBookProtos.Person
                            .newBuilder()
                            .setName("Joe M.")
                            .setId(1)
                            .setEmail("joem@foo.com")
                            .build();
                    exchange.getIn().setBody(person);
                })
                .marshal().protobuf()
                .log("body: ${body}")
                .to(TOPIC);

        from(TOPIC)
                .id("ingestAndLogJson")
                .unmarshal().protobuf("org.apache.camel.component.protobuf.AddressBookProtos$Person")
                .marshal().json(JsonLibrary.Fastjson)
                .log("${body}");
    }
}
