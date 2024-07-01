package campaignms.campaignms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import campaignms.campaignms.models.CampaignInfo;

public interface CampaignInfoRepository extends JpaRepository<CampaignInfo, Long> {
    
}
