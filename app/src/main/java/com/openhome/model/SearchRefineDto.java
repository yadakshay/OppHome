package com.openhome.model;

import java.util.List;

/**
 * Created by Bhargav on 3/12/2017.
 */

public class SearchRefineDto {

    private List<String> location;

    private String propertyType;

    private Price price;

    private Bedrooms bedrooms;

    private Bathrooms bathrooms;

    public class Price {
        private String start;
        private String end;

        public Price(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public class Bedrooms {
        private String start;
        private String end;

        public Bedrooms(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public class Bathrooms {
        private String start;
        private String end;

        public Bathrooms(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Bedrooms getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Bedrooms bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Bathrooms getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Bathrooms bathrooms) {
        this.bathrooms = bathrooms;
    }
}
