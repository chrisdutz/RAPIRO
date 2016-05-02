package de.codecentric.iot.rapiro.voice.utils;

import mraa.I2c;

/**
 * Created by christoferdutz on 23.04.16.
 */
public class WM8958 {

    final static byte WM8958_I2C_ADDR = 0x1A;

    final static byte WM8958_LEFT_LINE_IN_REGISTER = 0x00;
    final static byte WM8958_RIGHT_LINE_IN_REGISTER = 0x02;
    final static byte WM8958_BOTH_LINE_IN_REGISTER = 0x01;
    final static byte WM8958_LEFT_LINE_OUT_REGISTER = 0x04;
    final static byte WM8958_RIGHT_LINE_OUT_REGISTER = 0x06;
    final static byte WM8958_BOTH_LINE_OUT_REGISTER = 0x05;
    final static byte WM8958_ANALOG_AUDIO_PATH_CONTROL_REGISTER = 0x08;
    final static byte WM8958_DIGITAL_AUDIO_PATH_CONTROL_REGISTER = 0x0A;
    final static byte WM8958_POWER_DOWN_CONTROL_REGISTER = 0x0C;
    final static byte WM8958_DIGITAL_AUDIO_INTERFACE_FORMAT_REGISTER = 0x0E;
    final static byte WM8958_SAMPLING_CONTROL_REGISTER = 0x10;
    final static byte WM8958_ACTIVE_CONTROL_REGISTER = 0x12;
    final static byte WM8958_RESET_REGISTER = 0x1E;


    private I2c soundChipCommunication;

    public WM8958() {
        soundChipCommunication = new I2c(0);
        soundChipCommunication.address(WM8958_I2C_ADDR);
    }

    public void setLineInVolume(double volume) {
        assert volume >= 0 && volume <= 1;
        // Convert the 0.0 .. 1.0 value into a 4 bit byte value 1 = 0x15
        byte hexVolume = (byte) ((double) 0x7f * volume);
        sendCommand(WM8958_BOTH_LINE_IN_REGISTER, hexVolume);
    }

    public void setLineOutVolume(double volume) {
        assert volume >= 0 && volume <= 1;
        // Convert the 0.0 .. 1.0 value into a 4 bit byte value 1 = 0x15
        byte hexVolume = (byte) ((double) 0x7f * volume);
        sendCommand(WM8958_BOTH_LINE_OUT_REGISTER, hexVolume);
    }

    public void reset() {
        // reset codec
        sendCommand(WM8958_RESET_REGISTER, (byte) 0x00);
        // disable DAC and output powerdown ("Disable powerdown" = "Turn the off-state off" = "turn on" ... Mic and line in remain off)
        // TODO: Here we want to turn the mic on too (0x01)
        sendCommand(WM8958_POWER_DOWN_CONTROL_REGISTER, (byte) 0x01);
        // set volume for headphone output (both channels) (0x7f = Full Volume, 0xFF = Full Volume with Zero Cross Detect enabled)
        sendCommand(WM8958_BOTH_LINE_OUT_REGISTER, (byte) 0x7f);
        // set volume for microphone input (both channels) (0x7f = Full Volume)
        sendCommand(WM8958_BOTH_LINE_IN_REGISTER, (byte) 0x7f);
        // analog audio path control (DAC enabled, mic muted)
        // TODO: 0x15 (Enable microphone boost, un-mute mic input, select mic as input to dac)
        sendCommand(WM8958_ANALOG_AUDIO_PATH_CONTROL_REGISTER, (byte) 0x15);
        // digital audio path control
        sendCommand(WM8958_DIGITAL_AUDIO_PATH_CONTROL_REGISTER, (byte) 0x00);
        // set sample rate (48000Hz, assuming 12.288MHz codec clock)
        sendCommand(WM8958_SAMPLING_CONTROL_REGISTER, (byte) 0x00);
        // digital audio interface format set (DSP mode, 16 bit (0x83)) (was 24 bit (0x8b))
        sendCommand(WM8958_DIGITAL_AUDIO_INTERFACE_FORMAT_REGISTER, (byte) 0x83);
        // activate interface
        sendCommand(WM8958_ACTIVE_CONTROL_REGISTER, (byte) 0x01);
    }

    private void sendCommand(byte register, byte value) {
        soundChipCommunication.write(new byte[] {register, value});
    }

}
