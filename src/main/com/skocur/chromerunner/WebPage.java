package com.skocur.chromerunner;

import javafx.beans.property.SimpleStringProperty;
import org.openqa.selenium.WebDriver;

public class WebPage {

    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty url;
    private final SimpleStringProperty instances;

    public WebPage(String _id, String _name, String _url, String _instances)
    {
        this.id = new SimpleStringProperty(_id);
        this.name = new SimpleStringProperty(_name);
        this.url = new SimpleStringProperty(_url);
        this.instances = new SimpleStringProperty(_instances);
    }

    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getUrl() {
        return url.get();
    }

    public String getInstances() {
        return instances.get();
    }
}
