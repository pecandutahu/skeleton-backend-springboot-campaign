package campaignms.campaignms.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="campaign_info")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

@FilterDef(name = "deletedCampaignFilter", defaultCondition = "is_deleted = false")
@Filter(name = "deletedCampaignFilter")
public class CampaignInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignId;

    @NotBlank(message = "Campaign name is mandatory")
    private String campaignName;

    @NotBlank(message = "Campaign content is mandatory")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String campaignContent;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isDeleted = false;
    

}
