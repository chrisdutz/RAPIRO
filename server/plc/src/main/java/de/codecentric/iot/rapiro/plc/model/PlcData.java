package de.codecentric.iot.rapiro.plc.model;

public class PlcData {

    private boolean connected;
    private byte input;
    private byte output;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public byte getInput() {
        return input;
    }

    public void setInput(byte input) {
        this.input = input;
    }

    public byte getOutput() {
        return output;
    }

    public void setOutput(byte output) {
        this.output = output;
    }
}
