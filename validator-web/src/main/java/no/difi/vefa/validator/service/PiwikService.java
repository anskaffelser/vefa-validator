package no.difi.vefa.validator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class PiwikService {

    @Value("${piwik.site}")
    private String piwikSite;
    @Value("${piwik.id}")
    private String piwikId;

    public void update(ModelMap modelMap) {
        if (!piwikSite.isEmpty() && !piwikId.isEmpty()) {
            modelMap.put("piwikSite", piwikSite);
            modelMap.put("piwikId", piwikId);
        }
    }
}
