package org.sovaj.plugin.mongo;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.xml.sax.SAXException;

public class XMLValidatorFilter implements Filter {

    private Schema schema;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schema = factory.newSchema(new StreamSource("connector/connector.xsd"));
        } catch (SAXException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(request.getInputStream()));
            
            chain.doFilter(request, response);
        } catch (SAXException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {

    }
    
     static boolean validateAgainstXSD(File xml) {
        try {

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource("connector/connector.xsd"));

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (Exception exe) {
            exe.printStackTrace();
            return false;
        }
    }

}
