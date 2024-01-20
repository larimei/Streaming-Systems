public class SensorEvent {
    int sensorId;
    double speed;
    public SensorEvent(int sensorId, double speed) {
        this.sensorId = sensorId;
        this.speed = speed;
    }
    public int getSensorId() {
        return sensorId;
    }
    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }
    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
