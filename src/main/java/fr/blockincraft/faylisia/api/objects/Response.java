package fr.blockincraft.faylisia.api.objects;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.blockincraft.faylisia.api.serializer.ResponseSerializer;

@JsonSerialize(using = ResponseSerializer.class)
public class Response {
    private final String elementName;
    private final Object element;
    private final String error;
    private final boolean success;

    public Response(String error) {
        this.elementName = null;
        this.element = null;
        this.error = error;
        this.success = false;
    }

    public Response(String elementName, Object element) {
        this.elementName = elementName;
        this.element = element;
        this.error = null;
        this.success = true;
    }

    public String getElementName() {
        return elementName;
    }

    public Object getElement() {
        return element;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}
