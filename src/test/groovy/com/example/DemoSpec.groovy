package com.example

import com.example.model.TicketCustomField
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.Unroll

@MicronautTest
class DemoSpec extends Specification {

    @Inject
    ObjectMapper objectMapper

    @Unroll
    def "should deserialize polymorphic custom field type: #expectedRecord.simpleName"() {
        given: "A raw JSON snippet representing a customized field payload"
        def json = """{"id": 42, "value": ${jsonValue}}"""

        when:
        TicketCustomField result = objectMapper.readValue(json, TicketCustomField)


        then:
        verifyAll {
            result.id() == 42
            expectedRecord.isInstance(result)
            result.value() == expectedValue
        }

        where:
        jsonValue             | expectedRecord               | expectedValue
        '"flibberty giblets"' | TicketCustomField.Text       | "flibberty giblets"
        'true'                | TicketCustomField.Checkbox   | true
        '10023'               | TicketCustomField.Numeric    | 10023L
        '45.29'               | TicketCustomField.Decimal    | 45.29f
        '["alpha","beta"]'    | TicketCustomField.TagList    | ["alpha", "beta"]
    }

    def "should serialize sealed interface implementation"() {
        given:
        def field = new TicketCustomField.Text(123L, "hello")

        when:
        def json = objectMapper.writeValueAsString(field)

        then:
        json == '{"id":123,"value":"hello"}'
    }

    def "should serialize tag list"() {
        given:
        def field = new TicketCustomField.TagList(456L, ["a", "b"])

        when:
        def json = objectMapper.writeValueAsString(field)

        then:
        json == '{"id":456,"value":["a","b"]}'
    }
}
