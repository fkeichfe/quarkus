package io.quarkus.hibernate.validator.runtime.jaxrs;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;

@Provider
public class ResteasyViolationExceptionMapper implements ExceptionMapper<ResteasyViolationException> {

    @Override
    public Response toResponse(ResteasyViolationException exception) {
        Exception e = exception.getException();
        if (e != null || exception.getReturnValueViolations().size() != 0) {
            // Internal error: handle through the QuarkusErrorHandler,
            // which will return HTTP status 500 and log the exception
            throw exception;
        }
        return buildViolationReportResponse(exception);
    }

    protected Response buildViolationReportResponse(ResteasyViolationException exception) {
        ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
        builder.header(Validation.VALIDATION_HEADER, "true");

        // Check standard media types.
        MediaType mediaType = getAcceptMediaType(exception.getAccept());
        if (mediaType != null) {
            builder.type(mediaType);
            builder.entity(new ViolationReport(exception));
            return builder.build();
        }

        // Default media type.
        builder.type(MediaType.TEXT_PLAIN);
        builder.entity(exception.toString());
        return builder.build();
    }

    private MediaType getAcceptMediaType(List<MediaType> accept) {
        Iterator<MediaType> it = accept.iterator();
        while (it.hasNext()) {
            MediaType mt = it.next();
            if (MediaType.APPLICATION_XML_TYPE.getType().equals(mt.getType())
                    && MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mt.getSubtype())) {
                return MediaType.APPLICATION_XML_TYPE;
            }
            if (MediaType.APPLICATION_JSON_TYPE.getType().equals(mt.getType())
                    && MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mt.getSubtype())) {
                return MediaType.APPLICATION_JSON_TYPE;
            }
        }
        return null;
    }
}
