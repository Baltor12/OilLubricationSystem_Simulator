/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson;

import fi.tut.escop.ols.rtuJson.tags.TagsWithParent;
import fi.tut.escop.ols.rtuJson.tags.ChildrenWithTags;
import fi.tut.escop.ols.rtuJson.tags.ElementServiceTags;
import fi.tut.escop.ols.rtuJson.Links.LinkWithParent;
import fi.tut.escop.ols.rtuJson.Links.LinkWithNotifs;
import fi.tut.escop.ols.rtuJson.Links.LinkWithoutParent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.HMI;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.rtuJson.tags.ServiceTagsWithParent;
import java.util.HashMap;

/**
 * Class to represent events and services in the simulator in RTU Json Format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventServiceTree {

    String id;
    Object links;
    Object meta;
    @JsonProperty("class")
    String group;

    HashMap<String, Object> children = new HashMap<>();

    @JsonIgnore
    String myUrl;
    @JsonIgnore
    LubricationUnit lu;
    @JsonIgnore
    MeasuringStation ms;
    @JsonIgnore
    FlowMeter fm;
    @JsonIgnore
    HMI hmi;

    public EventServiceTree() {
    }

    public EventServiceTree(String id, String group) {
        this.id = id;
        this.group = group;
    }

    public EventServiceTree(String id, String group, String myUrl, Object tags) {
        this.id = id;
        this.group = group;
        this.myUrl = myUrl;
        this.meta = tags;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(Object links) {

        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public HashMap<String, Object> getChildren() {
        return children;
    }

    public void setChildren(HashMap<String, Object> children) {
        this.children = children;
    }

    public String getMyUrl() {
        return myUrl;
    }

    public void setMyUrl(String myUrl) {
        this.myUrl = myUrl;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    /**
     * Function to generate the Links without parent link
     *
     * @param myUrl
     * @param id
     */
    public void createLinkWithoutParent(String myUrl, String id) {
        if (id != "") {
            this.links = new LinkWithoutParent(myUrl + "/" + id, myUrl + "/" + id + "/info");
        } else {
            this.links = new LinkWithoutParent(myUrl, myUrl + "/info");
        }
    }

    /**
     * Function to generate the Links with parent link
     *
     * @param myUrl
     * @param id
     */
    public void createLinkWithParent(String myUrl, String id) {
        this.links = new LinkWithParent(myUrl + "/" + id, myUrl + "/" + id + "/info", myUrl);
    }

    /**
     * Function to generate the Links with notifs
     *
     * @param myUrl
     * @param id
     */
    public void createLinkWithNotifs(String myUrl, String id) {
        this.links = new LinkWithNotifs(myUrl + "/" + id, myUrl + "/" + id + "/info", myUrl + "/" + id + "/notifs");
    }

    /**
     * Function to generate the Children elements of the main RTU
     *
     */
    public void createMainChildren(String lsId) {
        LubricationSystem ls = Registry.lubricationSystem.get(lsId);
        for (String luKey : ls.getLuList().keySet()) {
            LubricationUnit lu = Registry.lubricationUnit.get(luKey);
            Children childLU = new Children(lu.getId(), "eScopRTU", this.myUrl + "/" + lu.getId());
            childLU.createLinkWithoutParent(myUrl, lu.getId());
            this.children.put(lu.getId(), childLU);
            for (String msKey : lu.getMsList().keySet()) {
                MeasuringStation ms = Registry.measuringStation.get(msKey);
                Children childMS = new Children(ms.getId(), "eScopRTU", this.myUrl + "/" + ms.getId());
                childMS.createLinkWithoutParent(myUrl, ms.getId());
                this.children.put(ms.getId(), childMS);
            }
        }

    }

    /**
     * Function to generate the Children elements for the individual elements of
     * the system
     *
     */
    public void createElementsChildren() {

        //data
        Children data = new Children("data", "data", this.myUrl + "/data");
        data.createLinkWithoutParent(myUrl, "data");
        this.children.put("data", data);

        //notifs
        Children notifs = new Children("notifs", "notifs", this.myUrl + "/notifs");
        notifs.createLinkWithoutParent(myUrl, "notifs");
        this.children.put("notifs", notifs);

        //events
        Children events = new Children("events", "events", this.myUrl + "/events");
        events.createLinkWithoutParent(myUrl, "events");
        this.children.put("events", events);

        //services
        Children services = new Children("services", "services", this.myUrl + "/services");
        services.createLinkWithoutParent(myUrl, "services");
        this.children.put("services", services);

    }

    public void createElementsData() {
        lu = null;
        ms = null;
        fm = null;
        if ((lu = Registry.lubricationUnit.get(id)) != null) {

            //Input Flow
            Children inFlow = new Children("inFlow", "data", this.myUrl + "/inFlow");
            inFlow.createLinkWithoutParent(myUrl, "inFlow");
            this.children.put("inFlow", inFlow);

            //Output Flow
            Children outFlow = new Children("outFlow", "data", this.myUrl + "/outFlow");
            outFlow.createLinkWithoutParent(myUrl, "outFlow");
            this.children.put("outFlow", outFlow);

            //Level
            Children level = new Children("level", "data", this.myUrl + "/level");
            level.createLinkWithoutParent(myUrl, "level");
            this.children.put("level", level);

            //Temperature
            Children temperature = new Children("temperature", "data", this.myUrl + "/temperature");
            temperature.createLinkWithoutParent(myUrl, "temperature");
            this.children.put("temperature", temperature);

            //Water Content
            Children waterContent = new Children("waterContent", "data", this.myUrl + "/waterContent");
            waterContent.createLinkWithoutParent(myUrl, "waterContent");
            this.children.put("waterContent", waterContent);

            //Particle Counter
            Children particleCount = new Children("particleCount", "data", this.myUrl + "/particleCount");
            particleCount.createLinkWithoutParent(myUrl, "particleCount");
            this.children.put("particleCount", particleCount);

//            //Oil Change
//            Children oilChange = new Children("oilChange", "data", this.myUrl + "/oilChange");
//            oilChange.createLinkWithoutParent(myUrl, "oilChange");
//            this.children.put("oilChange", oilChange);
//
//            //Filter Change
//            Children filterChange = new Children("filterChange", "data", this.myUrl + "/filterChange");
//            filterChange.createLinkWithoutParent(myUrl, "filterChange");
//            this.children.put("filterChange", filterChange);
//
//            //Maintenance
//            Children maintenance = new Children("maintenance", "data", this.myUrl + "/maintenance");
//            maintenance.createLinkWithoutParent(myUrl, "maintenance");
//            this.children.put("maintenance", maintenance);
            //Message
            Children message = new Children("message", "data", this.myUrl + "/message");
            message.createLinkWithoutParent(myUrl, "message");
            this.children.put("message", message);

        } else if ((ms = Registry.measuringStation.get(id)) != null) {
            //Input Flow
            Children inFlow = new Children("inFlow", "data", this.myUrl + "/inFlow");
            inFlow.createLinkWithoutParent(myUrl, "inFlow");
            this.children.put("inFlow", inFlow);

            //Output Flow
            Children outFlow = new Children("outFlow", "data", this.myUrl + "/outFlow");
            outFlow.createLinkWithoutParent(myUrl, "outFlow");
            this.children.put("outFlow", outFlow);

            //Pressure
            Children pressure = new Children("pressure", "data", this.myUrl + "/pressure");
            pressure.createLinkWithoutParent(myUrl, "pressure");
            this.children.put("pressure", pressure);

            //Temperature
            Children temperature = new Children("temperature", "data", this.myUrl + "/temperature");
            temperature.createLinkWithoutParent(myUrl, "temperature");
            this.children.put("temperature", temperature);

            //Flow meter
            for (String fmKey : ms.getFmList().keySet()) {
                String fmId = "";
                fmId = fmKey.substring(fmKey.lastIndexOf("_"));

                //Inflow
                Children fmInFlow = new Children("fm" + fmId + "_InFlow", "data", this.myUrl + "/" + "fm" + fmId + "_InFlow");
                fmInFlow.createLinkWithoutParent(myUrl, "fm" + fmId + "_InFlow");
                this.children.put("fm" + fmId + "InFlow", fmInFlow);

                //Outflow
                Children fmOutFlow = new Children("fm" + fmId + "_OutFlow", "data", this.myUrl + "/" + "fm" + fmId + "_OutFlow");
                fmOutFlow.createLinkWithoutParent(myUrl, "fm" + fmId + "_OutFlow");
                this.children.put("fm" + fmId + "OutFlow", fmOutFlow);
            }

        }
    }

    public void createElementsEvents() {
        lu = null;
        ms = null;
        fm = null;
        hmi =  null;
        if ((lu = Registry.lubricationUnit.get(id)) != null) {

            //Input Flow
            TagsWithParent inFlowTag = new TagsWithParent(id, "LubricationUnit", "inFlow", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags inFlow = new ChildrenWithTags("inFlow_Changed", inFlowTag, "event", this.myUrl + "/inFlow_Changed");
            inFlow.createLinkWithNotifs(myUrl, "inFlow_Changed");
            this.children.put("inFlow_Changed", inFlow);

            //Output Flow
            TagsWithParent outFlowTag = new TagsWithParent(id, "LubricationUnit", "outFlow", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags outFlow = new ChildrenWithTags("outFlow_Changed", outFlowTag, "event", this.myUrl + "/outFlow_Changed");
            outFlow.createLinkWithNotifs(myUrl, "outFlow_Changed");
            this.children.put("outFlow_Changed", outFlow);

            //Level
            TagsWithParent levelTag = new TagsWithParent(id, "LubricationUnit", "level", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags level = new ChildrenWithTags("level_Changed", levelTag, "event", this.myUrl + "/level_Changed");
            level.createLinkWithNotifs(myUrl, "level_Changed");
            this.children.put("level_Changed", level);

            //Temperature
            TagsWithParent temperatureTag = new TagsWithParent(id, "LubricationUnit", "temperature", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags temperature = new ChildrenWithTags("temperature_Changed", temperatureTag, "event", this.myUrl + "/temperature_Changed");
            temperature.createLinkWithNotifs(myUrl, "temperature_Changed");
            this.children.put("temperature_Changed", temperature);

            //Water Content
            TagsWithParent waterContentTag = new TagsWithParent(id, "LubricationUnit", "waterContent", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags waterContent = new ChildrenWithTags("waterContent_Changed", waterContentTag, "event", this.myUrl + "/waterContent_Changed");
            waterContent.createLinkWithNotifs(myUrl, "waterContent_Changed");
            this.children.put("waterContent_Changed", waterContent);

            //Particle Counter
            TagsWithParent particleCountTag = new TagsWithParent(id, "LubricationUnit", "particleCount", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags particleCount = new ChildrenWithTags("particleCount_Changed", particleCountTag, "event", this.myUrl + "/particleCount_Changed");
            particleCount.createLinkWithNotifs(myUrl, "particleCount_Changed");
            this.children.put("particleCount_Changed", particleCount);

            //Filter Clog
            TagsWithParent filterClogTag = new TagsWithParent(id, "LubricationUnit", "filterClog", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags filterClog = new ChildrenWithTags("filterClog_Changed", filterClogTag, "event", this.myUrl + "/filterClog_Changed");
            filterClog.createLinkWithNotifs(myUrl, "filterClog_Changed");
            this.children.put("filterClog_Changed", filterClog);

            //System Started Event
            TagsWithParent startTag = new TagsWithParent(id, "LubricationUnit", "start", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags start = new ChildrenWithTags("systemStart", startTag, "event", this.myUrl + "/systemStart");
            start.createLinkWithNotifs(myUrl, "systemStart");
            this.children.put("systemStart", start);

            //System Stopped Event
            TagsWithParent stopTag = new TagsWithParent(id, "LubricationUnit", "stop", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags stop = new ChildrenWithTags("systemStop", stopTag, "event", this.myUrl + "/systemStop");
            stop.createLinkWithNotifs(myUrl, "systemStop");
            this.children.put("systemStop", stop);

        } else if ((ms = Registry.measuringStation.get(id)) != null) {
            //Input Flow
            TagsWithParent inFlowTag = new TagsWithParent(id, "MeasuringStation", "inFlow", ms.getParentId(), "LubricationUnit");
            ChildrenWithTags inFlow = new ChildrenWithTags("inFlow_Changed", inFlowTag, "event", this.myUrl + "/inFlow_Changed");
            inFlow.createLinkWithNotifs(myUrl, "inFlow_Changed");
            this.children.put("inFlow_Changed", inFlow);

            //Output Flow
            TagsWithParent outFlowTag = new TagsWithParent(id, "MeasuringStation", "outFlow", ms.getParentId(), "LubricationUnit");
            ChildrenWithTags outFlow = new ChildrenWithTags("outFlow_Changed", outFlowTag, "event", this.myUrl + "/outFlow_Changed");
            outFlow.createLinkWithNotifs(myUrl, "outFlow_Changed");
            this.children.put("outFlow_Changed", outFlow);

            //Pressure
            TagsWithParent pressureTag = new TagsWithParent(id, "MeasuringStation", "pressure", ms.getParentId(), "LubricationUnit");
            ChildrenWithTags pressure = new ChildrenWithTags("pressure_Changed", pressureTag, "event", this.myUrl + "/pressure_Changed");
            pressure.createLinkWithNotifs(myUrl, "pressure_Changed");
            this.children.put("pressure_Changed", pressure);

            //Temperature
            TagsWithParent temperatureTag = new TagsWithParent(id, "MeasuringStation", "temperature", ms.getParentId(), "LubricationUnit");
            ChildrenWithTags temperature = new ChildrenWithTags("temperature_Changed", temperatureTag, "event", this.myUrl + "/temperature_Changed");
            temperature.createLinkWithNotifs(myUrl, "temperature_Changed");
            this.children.put("temperature_Changed", temperature);

            //Flow meter
            for (String fmKey : ms.getFmList().keySet()) {
                String fmId = "";
                fmId = fmKey.substring(fmKey.lastIndexOf("_"));
                fm = Registry.flowMeter.get(fmKey);
                //Inflow
                TagsWithParent fmInFlowTag = new TagsWithParent(fmKey, "FlowMeter", "inFlow", fm.getParentId(), "MeasuringStation");
                ChildrenWithTags fmInFlow = new ChildrenWithTags("fm" + fmId + "_InFlow_Changed", fmInFlowTag, "event", this.myUrl + "/" + "fm" + fmId + "_InFlow_Changed");
                fmInFlow.createLinkWithNotifs(myUrl, "fm" + fmId + "_InFlow_Changed");
                this.children.put("fm" + fmId + "_InFlow_Changed", fmInFlow);

                //Outflow
                TagsWithParent fmOutFlowTag = new TagsWithParent(fmKey, "FlowMeter", "outFlow", fm.getParentId(), "MeasuringStation", Double.toString(0.0), Double.toString(fm.getMaxFlow()), Double.toString(fm.getNomFlow()), Double.toString(fm.getNomFlow() * 1.5), Double.toString(fm.getNomFlow() * 0.5), fm.getFlowUnit());
                ChildrenWithTags fmOutFlow = new ChildrenWithTags("fm" + fmId + "_OutFlow_Changed", fmOutFlowTag, "event", this.myUrl + "/" + "fm" + fmId + "_OutFlow_Changed");
                fmOutFlow.createLinkWithNotifs(myUrl, "fm" + fmId + "_OutFlow_Changed");
                this.children.put("fm" + fmId + "_OutFlow_Changed", fmOutFlow);
            }

        } else if ((hmi = Registry.hmi.get(id)) != null) {
            //Oil Changed
            TagsWithParent oilChangedTag = new TagsWithParent(id, "HMI", "oilChanged", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags oilChanged = new ChildrenWithTags("oil_Changed", oilChangedTag, "event", this.myUrl + "/oil_Changed");
            oilChanged.createLinkWithNotifs(myUrl, "oil_Changed");
            this.children.put("oil_Changed", oilChanged);

            //filter Changed
            TagsWithParent filterChangedTag = new TagsWithParent(id, "HMI", "filterChanged", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags filterChanged = new ChildrenWithTags("filter_Changed", filterChangedTag, "event", this.myUrl + "/filter_Changed");
            filterChanged.createLinkWithNotifs(myUrl, "filter_Changed");
            this.children.put("filter_Changed", filterChanged);

            //Maintenance Done
            TagsWithParent maintenanceDoneTag = new TagsWithParent(id, "HMI", "maintenanceDone", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags maintenanceDone = new ChildrenWithTags("maintenance_Done", maintenanceDoneTag, "event", this.myUrl + "/maintenance_Done");
            maintenanceDone.createLinkWithNotifs(myUrl, "maintenance_Done");
            this.children.put("maintenance_Done", maintenanceDone);
            
            //Report
            TagsWithParent reportTag = new TagsWithParent(id, "HMI", "report", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags report = new ChildrenWithTags("report", reportTag, "event", this.myUrl + "/report");
            report.createLinkWithNotifs(myUrl, "report");
            this.children.put("report", report);
        }
    }

    public void createElementsNotifs() {
        for (String subKey : Registry.eventSubscribers.keySet()) {
            EventSubscriberInputs subscriber = Registry.eventSubscribers.get(subKey);
            this.children.put(subscriber.getId(), subscriber);
        }
    }

    public void createElementsIDNotifs(String eleId) {
        for (String subKey : Registry.eventSubscribers.keySet()) {
            EventSubscriberInputs subscriber = Registry.eventSubscribers.get(subKey);
            if (subscriber.getEventId().equals(eleId)) {
                this.children.put(subscriber.getId(), subscriber);
            }
        }
    }

    public void createElementsServices() {
        lu = null;
        ms = null;
        fm = null;
        if ((lu = Registry.lubricationUnit.get(id)) != null) {

            //Tank Capacity
            ServiceTagsWithParent tankCapTag = new ServiceTagsWithParent(id, "LubricationUnit", "tankCapacity", "query", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags tankCap = new ChildrenWithTags("tankCapacity", tankCapTag, "query", this.myUrl + "/tankCapacity");
            tankCap.createLinkWithoutParent(myUrl, "tankCapacity");
            this.children.put("tankCapacity", tankCap);

            //Filter Capacity
            ServiceTagsWithParent filterCapTag = new ServiceTagsWithParent(id, "LubricationUnit", "filterCapacity", "query", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags filterCap = new ChildrenWithTags("filterCapacity", filterCapTag, "query", this.myUrl + "/filterCapacity");
            filterCap.createLinkWithoutParent(myUrl, "filterCapacity");
            this.children.put("filterCapacity", filterCap);

            //Tank Level
            ServiceTagsWithParent levelTag = new ServiceTagsWithParent(id, "LubricationUnit", "level", "query", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags levelCap = new ChildrenWithTags("level", levelTag, "query", this.myUrl + "/level");
            levelCap.createLinkWithoutParent(myUrl, "level");
            this.children.put("level", levelCap);

            //Filter Clog
            ServiceTagsWithParent filterClogTag = new ServiceTagsWithParent(id, "LubricationUnit", "filterClog", "query", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags filterClog = new ChildrenWithTags("filterClog", filterClogTag, "query", this.myUrl + "/filterClog");
            filterClog.createLinkWithoutParent(myUrl, "filterClog");
            this.children.put("filterClog", filterClog);

            //Maximum Flow
            ServiceTagsWithParent maxFlowTag = new ServiceTagsWithParent(id, "LubricationUnit", "maxOilFlow", "query", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags maxFlowCap = new ChildrenWithTags("maxOilFlow", maxFlowTag, "query", this.myUrl + "/maxOilFlow");
            maxFlowCap.createLinkWithoutParent(myUrl, "maxOilFlow");
            this.children.put("maxOilFlow", maxFlowCap);

            //Oil Allocation
            ServiceTagsWithParent oilAllocTag = new ServiceTagsWithParent(id, "LubricationUnit", "oilAllocation", "query", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags oilAlloc = new ChildrenWithTags("oilAllocation", oilAllocTag, "data", this.myUrl + "/oilAllocation");
            oilAlloc.createLinkWithoutParent(myUrl, "oilAllocation");
            this.children.put("oilAllocation", oilAlloc);

            //Start
            ServiceTagsWithParent startTag = new ServiceTagsWithParent(id, "LubricationUnit", "start", "process", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags run = new ChildrenWithTags("start", startTag, "process", this.myUrl + "/start");
            run.createLinkWithNotifs(myUrl, "start");
            this.children.put("start", run);

            //Stop
            ServiceTagsWithParent stopTag = new ServiceTagsWithParent(id, "LubricationUnit", "stop", "process", lu.getParentId(), "LubricationSystem");
            ChildrenWithTags shutdown = new ChildrenWithTags("stop", stopTag, "process", this.myUrl + "/stop");
            shutdown.createLinkWithNotifs(myUrl, "stop");
            this.children.put("stop", shutdown);

        }
        HMI hmi;
        if ((hmi = Registry.hmi.get(id)) != null) {

            //Oil Change
            ServiceTagsWithParent oilChangeTag = new ServiceTagsWithParent(id, "HMI", "oilChange", "operation", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags oilChange = new ChildrenWithTags("oilChange", oilChangeTag, "operation", this.myUrl + "/oilChange");
            oilChange.createLinkWithoutParent(myUrl, "oilChange");
            this.children.put("oilChange", oilChange);

            //Filter Change
            ServiceTagsWithParent filterChangeTag = new ServiceTagsWithParent(id, "HMI", "filterChange", "operation", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags filterChange = new ChildrenWithTags("filterChange", filterChangeTag, "operation", this.myUrl + "/filterChange");
            filterChange.createLinkWithoutParent(myUrl, "filterChange");
            this.children.put("filterChange", filterChange);

            //Message
            ServiceTagsWithParent messageTag = new ServiceTagsWithParent(id, "HMI", "message", "operation", hmi.getParentId(), "LubricationUnit");
            ChildrenWithTags message = new ChildrenWithTags("message", messageTag, "operation", this.myUrl + "/message");
            message.createLinkWithoutParent(myUrl, "message");
            this.children.put("message", message);
        }

    }
}
