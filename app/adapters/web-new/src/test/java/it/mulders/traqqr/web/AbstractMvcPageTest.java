package it.mulders.traqqr.web;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import jakarta.mvc.Models;
import jakarta.ws.rs.core.Response;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.WithAssertions;
import org.eclipse.krazo.core.ModelsImpl;

public abstract class AbstractMvcPageTest implements WithAssertions {
    protected final Models models = new ModelsImpl();
    protected final Owner owner = createOwner();

    private Owner createOwner() {
        return new Owner() {
            private final String code = RandomStringUtils.generateRandomAlphaString(18);

            @Override
            public String code() {
                return code;
            }

            @Override
            public String displayName() {
                return "";
            }

            @Override
            public String profilePictureUrl() {
                return "";
            }
        };
    }

    protected ResponseAssert assertThat(Response response) {
        return new ResponseAssert(response);
    }

    public static class ResponseAssert extends ObjectAssert<Response> {
        public ResponseAssert(Response actual) {
            super(actual);
        }

        public ResponseAssert hasStatus(int expectedStatus) {
            isNotNull();
            var actualStatus = actual.getStatus();
            if (actualStatus != expectedStatus) {
                failWithMessage("Expected response status to be <%d> but was <%d>", expectedStatus, actual.getStatus());
            }
            return this;
        }

        public ResponseAssert hasViewName(String expectedViewName) {
            isNotNull();
            var actualEntity = actual.readEntity(String.class);
            if (!expectedViewName.equals(actualEntity)) {
                failWithMessage("Expected response entity to be <%s> but was <%s>", expectedViewName, actualEntity);
            }
            return this;
        }

        public ResponseAssert hasHeader(String name, String expectedValue) {
            isNotNull();
            var headers = actual.getHeaders();
            var values = headers.get(name).stream().map(Object::toString).toList();
            if (!values.contains(expectedValue)) {
                failWithMessage(
                        "Expected response <%s> header to contain <%s> but was <%s>", name, expectedValue, values);
            }
            return this;
        }
    }
}
