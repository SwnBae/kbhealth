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

    /**
     * 스코어 테스트용
     */
//    @PrePersist
//    public void prePersist() {
//        if (this.createdDate == null) {  // createdDate가 null일 경우에만 설정
//            this.createdDate = LocalDateTime.now();
//        }
//        if (this.lastModifyDate == null) {  // lastModifyDate가 null일 경우에만 설정
//            this.lastModifyDate = LocalDateTime.now();
//        }
//    }

//    @PreUpdate
//    public void preUpdate() {
//        this.lastModifyDate = LocalDateTime.now();
//    }
}
