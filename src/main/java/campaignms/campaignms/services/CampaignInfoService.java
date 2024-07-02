package campaignms.campaignms.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import campaignms.campaignms.exceptions.ResourceNotFoundException;
import campaignms.campaignms.models.CampaignInfo;
import campaignms.campaignms.repositories.CampaignInfoRepository;
import jakarta.persistence.EntityManager;

@Service
public class CampaignInfoService {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CampaignInfoRepository campaignInfoRepository;

    public List<CampaignInfo> findAll() {
        entityManager.unwrap(Session.class).enableFilter("deletedCampaignFilter");
        return campaignInfoRepository.findAll();
    }

    public Optional<CampaignInfo> findById(Long id) {
        entityManager.unwrap(Session.class).enableFilter("deletedCampaignFilter");
        Optional<CampaignInfo> campaignInfo = campaignInfoRepository.findById(id);
        if(!campaignInfo.isPresent()) {
            throw new ResourceNotFoundException("Campaign info not found");
        }
        return campaignInfoRepository.findById(id);
    }

    public CampaignInfo save(CampaignInfo campaignInfo) {
        return campaignInfoRepository.save(campaignInfo);
    }

    public void deleteById(Long id) {
        campaignInfoRepository.findById(id).ifPresent(campaign -> {
            campaign.setDeleted(true);
            campaignInfoRepository.save(campaign);
        });
    }
}
