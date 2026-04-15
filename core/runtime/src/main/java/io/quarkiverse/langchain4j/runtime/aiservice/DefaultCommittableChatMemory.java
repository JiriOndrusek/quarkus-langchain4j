package io.quarkiverse.langchain4j.runtime.aiservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;

class DefaultCommittableChatMemory implements CommittableChatMemory {

    private static final Logger log = Logger.getLogger(DefaultCommittableChatMemory.class);

    private final ChatMemory delegate;
    private final List<ChatMessage> newMessages;

    public DefaultCommittableChatMemory(ChatMemory delegate) {
        this.delegate = delegate;
        this.newMessages = new ArrayList<>(delegate.messages());
        log.debugf("DefaultCommittableChatMemory created with %d messages", this.newMessages.size());
    }

    @Override
    public Object id() {
        return delegate.id();
    }

    @Override
    public void add(ChatMessage message) {
        log.debugf("Adding message, current size: %d, message type: %s", newMessages.size(),
                message.getClass().getSimpleName());
        if (message instanceof SystemMessage) {
            Optional<SystemMessage> systemMessage = findSystemMessage(newMessages);
            if (systemMessage.isPresent()) {
                if (systemMessage.get().equals(message)) {
                    return; // do not add the same system message
                } else {
                    newMessages.remove(systemMessage.get()); // need to replace existing system message
                }
            }
            newMessages.add(0, message); // the system message must be in the first position
        } else {
            newMessages.add(message);
        }
        log.debugf("After adding message, size: %d", newMessages.size());
    }

    private static Optional<SystemMessage> findSystemMessage(List<ChatMessage> messages) {
        return messages.stream()
                .filter(message -> message instanceof SystemMessage)
                .map(message -> (SystemMessage) message)
                .findAny();
    }

    @Override
    public List<ChatMessage> messages() {
        log.debugf("messages() called, returning %d messages", newMessages.size());
        return new ArrayList<>(newMessages);
    }

    // the following operations are terminal

    @Override
    public void clear() {
        newMessages.clear();
        delegate.clear();
    }

    @Override
    public void commit() {
        delegate.set(newMessages);
    }
}
