package kb.health.domain.record;

import jakarta.persistence.*;
import kb.health.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseRecord {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //점수 처리는 추후..
    private int score;

    private LocalDateTime recordedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.recordedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
