package entities;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "log")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogEntries {

    @XmlElement(name = "entry")
    private List<LogEntry> entries = new ArrayList<>();

    public LogEntries() {}

    public LogEntries(List<LogEntry> entries) {
        this.entries = entries;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LogEntry> entries) {
        this.entries = entries;
    }
}