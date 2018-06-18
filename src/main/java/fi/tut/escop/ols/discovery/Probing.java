/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.discovery;

import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.controller.SimulatorRESTTemplate;
import fi.tut.escop.ols.elements.Registry;
import java.io.IOException;

/**
 * Class to get the Probing message from the RPL
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class Probing {

    String dc;
    Integer cnt;

    public Probing() {
    }

    public Probing(String dc, Integer cnt) {
        this.dc = dc;
        this.cnt = cnt;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public void reg() {
        Registry.probing.put(cnt, this);
    }

    public void postSwagger() throws IOException {
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            for (String luKey : Registry.lubricationSystem.get(lsKey).getLuList().keySet()) {
                RegisterationMessage regMessageLU = new RegisterationMessage();
                regMessageLU.setCnt(this.cnt);
                regMessageLU.setId(luKey);
                regMessageLU.setUrl(HostPortandConfig.ROOT_URL + "/" + lsKey + "/RTU/" + luKey + "/api/swagger.json");
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                restTemplate.probingResponsePOST(regMessageLU, dc);                
                RegisterationMessage regMessageHMI = new RegisterationMessage();
                regMessageHMI.setCnt(this.cnt);
                regMessageHMI.setId(Registry.lubricationUnit.get(luKey).getHmi().getId());
                regMessageHMI.setUrl(HostPortandConfig.ROOT_URL + "/" + lsKey + "/RTU/" + Registry.lubricationUnit.get(luKey).getHmi().getId() + "/api/swagger.json");                
                restTemplate.probingResponsePOST(regMessageHMI, dc);  
                for (String msKey : Registry.lubricationUnit.get(luKey).getMsList().keySet()) {
                    RegisterationMessage regMessage = new RegisterationMessage();
                    regMessage.setCnt(this.cnt);
                    regMessage.setId(msKey);
                    regMessage.setUrl(HostPortandConfig.ROOT_URL + "/" + lsKey + "/RTU/" + msKey + "/api/swagger.json");
                    SimulatorRESTTemplate restTemp = new SimulatorRESTTemplate();
                    restTemp.probingResponsePOST(regMessage, dc);
                }
            }
        }
    }

}
