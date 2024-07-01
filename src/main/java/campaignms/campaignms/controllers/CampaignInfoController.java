package campaignms.campaignms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import campaignms.campaignms.models.CampaignInfo;
import campaignms.campaignms.services.CampaignInfoService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignInfoController {
    @Autowired
    private CampaignInfoService campaignInfoService;

    @GetMapping
    public List<CampaignInfo> getAllCampaigns() {
        return campaignInfoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignInfo> getCampaignById(@PathVariable Long id) {
        return campaignInfoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CampaignInfo createCampaign(@RequestBody CampaignInfo campaignInfo) {
        return campaignInfoService.save(campaignInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignInfo> updateCampaign(@PathVariable Long id, @RequestBody CampaignInfo campaignDetails) {
        return campaignInfoService.findById(id)
                .map(campaign -> {
                    campaign.setCampaignName(campaignDetails.getCampaignName());
                    campaign.setCampaignContent(campaignDetails.getCampaignName());
                    return ResponseEntity.ok(campaignInfoService.save(campaign));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        return campaignInfoService.findById(id)
                .map(campaign -> {
                    campaignInfoService.deleteById(id);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}