package io.quarkiverse.langchain4j.deployment.items;

import java.util.function.Predicate;

import org.jboss.jandex.ClassInfo;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * Allows extensions to exclude specific interfaces from being implicitly registered as AI services.
 * <p>
 * When Quarkus LangChain4j detects interfaces with {@code @UserMessage}, {@code @SystemMessage},
 * or other AI service annotations, it automatically generates implementations for them.
 * This build item allows other extensions (e.g. Apache Camel) to prevent that auto-registration
 * for interfaces they manage themselves.
 */
public final class AiServiceExcludedClassBuildItem extends MultiBuildItem {

    private final Predicate<ClassInfo> predicate;

    public AiServiceExcludedClassBuildItem(Predicate<ClassInfo> predicate) {
        this.predicate = predicate;
    }

    public Predicate<ClassInfo> getPredicate() {
        return predicate;
    }
}
