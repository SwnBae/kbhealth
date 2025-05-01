package kb.health.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    private LocalDateTime createdDate;
    private LocalDateTime lastModifyDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.lastModifyDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifyDate = LocalDateTime.now();
    }
}
