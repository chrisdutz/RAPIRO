package test.vision;

import com.pi4j.util.NativeLibraryLoader;
import com.pi4j.wiringpi.Spi;
import test.vision.model.Block;
import test.vision.model.ColorBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by christoferdutz on 13.10.16.
 */
public class RaspberryVisionTest {

    private static final byte[] byteBuffer = new byte[1];
    private static final byte[] wordBuffer = new byte[2];

    public RaspberryVisionTest() {
        NativeLibraryLoader.load("libpi4j.so");
        int res = Spi.wiringPiSPISetupMode(0, 10000000, 0);
        if(res == -1) {
            System.out.println("Error in: Spi.wiringPiSPISetupMode(0, 10000000, 0)");
        }
    }

    public Block[] getFrame() {
        List<Block> blocks = new ArrayList<>();
        State state = State.BEFORE_FRAME;
        int checksum;
        System.out.println("-------------------------------------");
        while(true) {
            switch (state) {
                case BEFORE_FRAME:
                    while(readByte() != (byte) 0xAA);
                    state = State.FIRST_AA_BYTE_READ;
                    break;
                case FIRST_AA_BYTE_READ:
                    if(readByte() == (byte) 0x55) {
                        state = State.FIRST_55_BYTE_READ;
                    } else {
                        state = State.BEFORE_FRAME;
                    }
                    break;
                case FIRST_55_BYTE_READ:
                    int word = readWord();
                    if(word == 0xAA55) {
                        state = State.NORMAL_BLOCK_SYNC_WORD_READ;
                    } else if(word == 0xAA56) {
                        state = State.COLOR_BLOCK_SYNC_WORD_READ;
                    } else {
                        if(blocks.size() > 0) {
                            return blocks.toArray(new Block[0]);
                        }
                        state = State.BEFORE_FRAME;
                    }
                    break;
                case NORMAL_BLOCK_SYNC_WORD_READ:
                    checksum = readWord();

                    Block block = new Block();
                    block.setSignature(readWord());
                    block.setX(readWord());
                    block.setY(readWord());
                    block.setWidth(readWord());
                    block.setHeight(readWord());

                    if(block.getChecksum() == checksum) {
                        blocks.add(block);
                    }
                    state = State.FIRST_55_BYTE_READ;
                    break;
                case COLOR_BLOCK_SYNC_WORD_READ:
                    checksum = readWord();

                    ColorBlock colorBlock = new ColorBlock();
                    colorBlock.setSignature(readWord());
                    colorBlock.setX(readWord());
                    colorBlock.setY(readWord());
                    colorBlock.setWidth(readWord());
                    colorBlock.setHeight(readWord());
                    colorBlock.setAngle(readWord());

                    if(colorBlock.getChecksum() == checksum) {
                        blocks.add(colorBlock);
                        System.out.println("Read color block: " + colorBlock.getSignature());
                    } else {
                        System.out.println("Checksum error.");
                    }
                    state = State.FIRST_55_BYTE_READ;
                    break;
            }
        }
    }

    public byte readByte() {
        byte[] response = readBuffer(byteBuffer);
        return response[0];
    }

    public int readWord() {
        byte[] response = readBuffer(wordBuffer);
        return ((response[0] & 0xff) << 8) | (response[1] & 0xff);
    }

    private byte[] readBuffer(byte[] buffer) {
        // Set the buffer to "0x00" for every byte.
        Arrays.fill(buffer, (byte) 0 );

        // Read buffer.length bytes.
        int res = Spi.wiringPiSPIDataRW(0, buffer);

        // Check the return code.
        if(res <= -1) {
            System.out.println("Error in: Spi.wiringPiSPIDataRW(0, buffer)");
        }

        // Return the buffer.
        return buffer;
    }

    public static void main(String[] args) {
        RaspberryVisionTest test = new RaspberryVisionTest();
        while(true) {
            Block[] blocks = test.getFrame();
            System.out.println(Arrays.toString(blocks));
        }
    }

    private enum State {
        BEFORE_FRAME,
        FIRST_AA_BYTE_READ,
        FIRST_55_BYTE_READ,
        NORMAL_BLOCK_SYNC_WORD_READ,
        COLOR_BLOCK_SYNC_WORD_READ
    }

}
