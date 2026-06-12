package com.example

import com.example.model.TicketCustomField
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.Unroll
import io.micronaut.core.type.Argument

@MicronautTest
class DemoSpec extends Specification {

    @Inject
    ObjectMapper objectMapper

    @Unroll
    def "should deserialize polymorphic custom field type: #expectedClass.simpleName"() {
        given: "A raw JSON snippet representing a customized field payload"
        def json = """{"id": 42, "value": ${jsonValue}}"""

        when:
        TicketCustomField result = objectMapper.readValue(json, TicketCustomField)


        then:
        verifyAll {
            result.id == 42
            expectedClass.isInstance(result.value)
            result.getValue() == expectedValue
        }

        where:
        jsonValue             | expectedClass | expectedValue
        '"flibberty giblets"' | String        | "flibberty giblets"
        'true'                | Boolean       | true
        '10023'               | Long          | 10023L
        '45.29'               | Float         | 45.29f
        '["alpha","beta"]'    | List          | ["alpha", "beta"]
    }

    def "should deserialize with specific generic type"() {
        given:
        def json = '{"id": 1, "value": "hello"}'
        
        when:
        def result = objectMapper.readValue(json, Argument.of(TicketCustomField, String))
        
        then:
        result.id == 1L
        result.value == "hello"
        result.value instanceof String
    }

    def "should serialize polymorphic custom field"() {
        given:
        def field = new TicketCustomField<String>(id: 123L, value: "test-value")
        
        when:
        def json = objectMapper.writeValueAsString(field)
        
        then:
        json == '{"id":123,"value":"test-value"}'
    }

    def "should serialize with list value"() {
        given:
        def field = new TicketCustomField<List<String>>(id: 456L, value: ["one", "two"])
        
        when:
        def json = objectMapper.writeValueAsString(field)
        
        then:
        json == '{"id":456,"value":["one","two"]}'
    }
}
