package de.brazzy.nikki.model;

class Waypoint implements Serializable{
    public static final long serialVersionUID = 1;

    WaypointFile file;
    Day day;
    Date timestamp;
    BigDecimal latitude;
    BigDecimal longitude;
    BigDecimal elevation;
}
