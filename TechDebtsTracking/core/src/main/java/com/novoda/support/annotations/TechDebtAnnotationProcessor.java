package com.novoda.support.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Locale;
import java.util.Set;

@SupportedAnnotationTypes("com.novoda.support.annotations.TechDebt")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TechDebtAnnotationProcessor extends AbstractProcessor {

    private static final String WARNING_TEMPLATE = "TechDebt detected!!!"
            + "\nDescription: %s"
            + "\nSize: %s"
            + "\nLink: %s";

    @Override
    public boolean process(Set<? extends TypeElement> annotationElements, RoundEnvironment roundEnv) {
        for (TypeElement annotationElement : annotationElements) {
            final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotationElement);
            for (Element annotadedElement : annotatedElements) {
                printWarning(annotadedElement);
            }
        }
        return true;
    }

    private void printWarning(Element annotatedElement) {
        TechDebt techDebt = annotatedElement.getAnnotation(TechDebt.class);
        String message = String.format(Locale.UK, WARNING_TEMPLATE, techDebt.description(), techDebt.size(), techDebt.link());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message, annotatedElement);
    }
}
