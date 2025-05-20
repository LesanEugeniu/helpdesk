package io.github.helpdesk.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_message")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean seen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @PrePersist
    public void prePersist() {
        seen = false;
    }

    public Long getId() {
        return id;
    }

    public ChatMessage setId(Long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ChatMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isSeen() {
        return seen;
    }

    public ChatMessage setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public ChatMessage setTicket(Ticket ticket) {
        this.ticket = ticket;
        return this;
    }

    public User getSender() {
        return sender;
    }

    public ChatMessage setSender(User sender) {
        this.sender = sender;
        return this;
    }
}
