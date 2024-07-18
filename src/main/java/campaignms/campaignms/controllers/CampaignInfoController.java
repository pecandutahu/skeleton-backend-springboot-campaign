package campaignms.campaignms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.CampaignInfo;
import campaignms.campaignms.models.User;
import campaignms.campaignms.services.CampaignInfoService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns")
@Validated
public class CampaignInfoController {
    @Autowired
    private CampaignInfoService campaignInfoService;

    @GetMapping
    public WebResponse<List<CampaignInfo>> getAllCampaigns(User user) {
        return WebResponse.<List<CampaignInfo>>builder().data(campaignInfoService.findAll()).messages("success").build();
    }

    @GetMapping("/{id}")
    public WebResponse<Optional<CampaignInfo>> getCampaignById(User user, @PathVariable Long id) {
        return WebResponse.<Optional<CampaignInfo>>builder().data(campaignInfoService.findById(id)).messages("success").build();
    }

    @PostMapping
    public WebResponse<CampaignInfo> createCampaign(User user, @Valid @RequestBody CampaignInfo campaignInfo) {
        return WebResponse.<CampaignInfo>builder().data(campaignInfoService.save(campaignInfo)).messages("success").build();
    }

    @PutMapping("/{id}")
    public WebResponse<CampaignInfo> updateCampaign(User user, @PathVariable Long id, @Valid @RequestBody CampaignInfo campaignDetails) {
        Optional<CampaignInfo> campaign = campaignInfoService.findById(id);
        campaign.get().setCampaignName(campaignDetails.getCampaignName());
        campaign.get().setCampaignContent(campaignDetails.getCampaignContent());
        return WebResponse.<CampaignInfo>builder().data(campaignInfoService.save(campaign.get())).messages("success").build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<CampaignInfo> deleteCampaign(User user, @PathVariable Long id) {
        CampaignInfo campaignInfo = campaignInfoService.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not Found"));
        campaignInfoService.deleteById(id);

        return WebResponse.<CampaignInfo>builder().data(campaignInfo).messages("Data deleted successfully").build();
    }
}