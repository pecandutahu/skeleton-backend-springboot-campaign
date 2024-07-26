package campaignms.campaignms.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Subject is mandatory")
    private String subject;

    @NotBlank(message = "content is mandatory")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isDeleted = false;

    @Nullable
    private String status;
}
