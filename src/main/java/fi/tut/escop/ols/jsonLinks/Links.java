/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.jsonLinks;

import fi.tut.escop.ols.elements.Registry;

/**
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Links {
    
    String id;
    String self;
    String parent;
    String info;

    public Links() {
    }

    public Links(String id, String self, String parent, String info) {
        this.id = id;
        this.self = self;
        this.parent = parent;
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void regLS() {
        Registry.linkLS.put(id, this);
    }
    
    public void regLU() {
        Registry.linkLU.put(id, this);
    }
    
    public void regMS() {
        Registry.linkMS.put(id, this);
    }
    
    public void regFM() {
        Registry.linkFM.put(id, this);
    }
}
