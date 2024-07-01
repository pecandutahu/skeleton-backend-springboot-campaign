package campaignms.campaignms.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FilterDef(name = "deletedEmailLogFilter", defaultCondition = "is_deleted = false")
@Filter(name = "deletedEmailLogFilter")
public class EmailMassLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailMasLogId;

    private String email;
    private String subject;
    private String content;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isDeleted = false;
}
