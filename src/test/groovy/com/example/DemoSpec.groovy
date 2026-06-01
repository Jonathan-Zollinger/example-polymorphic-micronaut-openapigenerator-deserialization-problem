package com.example

import com.example.model.CustomField
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
    def "should deserialize polymorphic custom field type: #expectedClass.simpleName"() {
        given: "A raw JSON snippet representing a customized field payload"
        def json = """{"id": 42, "value": ${jsonValue}}"""

        when:
        CustomField result = objectMapper.readValue(json, CustomField)


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
}
