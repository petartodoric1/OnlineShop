package entities;

import jakarta.xml.bind.annotation.*;
import java.time.LocalDateTime;

@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogEntry {

    @XmlElement
    private String time;

    @XmlElement
    private String action;

    @XmlElement
    private String details;

    public LogEntry() {
    }

    public LogEntry(String action, String details) {
        this.time = LocalDateTime.now().toString();
        this.action = action;
        this.details = details;
    }

    public String getTime() { return time; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
}