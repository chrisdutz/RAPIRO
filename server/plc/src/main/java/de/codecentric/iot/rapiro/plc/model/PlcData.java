package de.codecentric.iot.rapiro.plc.model;

public class PlcData {

    private byte input;
    private byte output;

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
