package io.github.dankunis.xml_serializer;

import io.github.dankunis.xml_serializer.annotations.XMLAttribute;
import io.github.dankunis.xml_serializer.annotations.XMLObject;
import io.github.dankunis.xml_serializer.annotations.XMLTag;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;

public class XMLConverter {
    private static final String DEFAULT_VALUE = "UNDEFINED";

    public static Document serialized(Object obj) throws Exception {
        Class clazz = obj.getClass();
        if (!clazz.isAnnotationPresent(XMLObject.class)) {
            throw new Exception("No annotation");
        }

        Document result = DocumentHelper.createDocument();

        String rootTag = ((XMLObject) clazz.getAnnotation(XMLObject.class)).name();
        if (rootTag.equals(XMLConverter.DEFAULT_VALUE)) {
            rootTag = clazz.getSimpleName();
        }
        Element root = result.addElement(rootTag);

        for (var field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XMLTag.class)) {
                if (field.get(obj) == null) {
                    continue;
                }

                if (field.get(obj).getClass().isAnnotationPresent(XMLObject.class)) {
                    Element newElement = serialized(field.get(obj)).getRootElement();
                    root.add(newElement);
                    continue;
                }
                var tagName = field.getAnnotation(XMLTag.class).name();
                if (tagName.equals(XMLConverter.DEFAULT_VALUE)) {
                    tagName = field.getName();
                }
                root.addElement(tagName).addText(field.get(obj).toString());
            }
        }

        for (var method: clazz.getDeclaredMethods()) {
            if (method.getParameterCount() > 0) {
                throw new Exception("Method has parameters");
            }
            if (method.getReturnType() == Void.class) {
                throw new Exception("Unacceptable return type");
            }

            method.setAccessible(true);
            if (method.isAnnotationPresent(XMLTag.class)) {
                var tagName = (method.getAnnotation(XMLTag.class)).name();
                if (tagName.equals(XMLConverter.DEFAULT_VALUE)) {
                    tagName = method.getName();
                }
                root.addElement(tagName).addText(method.invoke(obj).toString());
            }
        }

        for (var field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XMLAttribute.class)) {
                if (field.get(obj) == null) {
                    continue;
                }

                var attributeName = field.getAnnotation(XMLAttribute.class).name();
                if (attributeName.equals(XMLConverter.DEFAULT_VALUE)) {
                    attributeName = field.getName();
                }

                String tagName = field.getAnnotation(XMLAttribute.class).tag();
                if (tagName.equals(XMLConverter.DEFAULT_VALUE)) {
                    root.addAttribute(attributeName, field.get(obj).toString());
                    continue;
                }
                var foundedTags = (ArrayList) result.selectNodes("//" + tagName);
                if (foundedTags.size() > 1) {
                    throw new Exception("Ambigious tag declaration");
                } else if (foundedTags.isEmpty()) {
                    throw new Exception("Adding attribute for undeclared tag");
                } else {
                    ((Element) foundedTags.get(0)).addAttribute(attributeName, field.get(obj).toString());
                }
            }
        }

        for (var method: clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(XMLAttribute.class)) {
                method.setAccessible(true);

                var attributeName = method.getAnnotation(XMLAttribute.class).name();
                if (attributeName.equals(XMLConverter.DEFAULT_VALUE)) {
                    attributeName = method.getName();
                }

                String tagName = method.getAnnotation(XMLAttribute.class).tag();
                if (tagName.equals(XMLConverter.DEFAULT_VALUE)) {
                    root.addAttribute(attributeName, method.invoke(obj).toString());
                    continue;
                }
                var foundedTags = (ArrayList) result.selectNodes("//" + tagName);
                if (foundedTags.size() > 1) {
                    throw new Exception("Ambigious tag declaration");
                } else if (foundedTags.isEmpty()) {
                    throw new Exception("Adding attribute for undeclared tag");
                } else {
                    ((Element) foundedTags.get(0)).addAttribute(attributeName, method.invoke(obj).toString());
                }
            }
        }
        return result;
    }
}
