package io.github.helpdesk.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ticket")
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public UUID getId() {
        return id;
    }

    public Ticket setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Ticket setTitle(String title) {
        this.title = title;
        return this;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Ticket setStatus(TicketStatus status) {
        this.status = status;
        return this;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Ticket setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public Ticket setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Ticket setCategory(Category category) {
        this.category = category;
        return this;
    }

}
