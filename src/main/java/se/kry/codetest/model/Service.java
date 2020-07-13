package se.kry.codetest.model;

public class Service {
    private String url;
    private String name;
    private String timeAdded;
    private Status status;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Status getStatus() {
        return status;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "{url: " + this.url + ", name: " + this.name + ", status: " + this.status + "}";
    }

    public static class Builder {
        private String url;
        private String name;
        private String timeAdded;
        private Status status;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTimeAdded(String timeAdded) {
            this.timeAdded = timeAdded;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Service build() {
            Service service = new Service();
            service.url = this.url;
            service.name = this.name;
            service.timeAdded = this.timeAdded;
            service.status = this.status;
            return service;
        }
    }

    public enum Status {
        OK,
        FAIL,
        UNKNOWN
    }
}

