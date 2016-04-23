package de.codecentric.iot.rapiro.vision;

import de.codecentric.iot.rapiro.SystemMode;
import de.codecentric.iot.rapiro.vision.model.Block;
import de.codecentric.iot.rapiro.vision.model.ColorBlock;
import mraa.Spi;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Service("visionService")
public class VisionService {

    private static final int PIXY_START_WORD = 0xaa55;
    private static final int PIXY_START_WORD_CC = 0xaa56;
    // Is is the 55 of the second byte of the first word
    // and aa of the first byte of the second word.
    private static final int PIXY_START_WORD_OUT_OF_SYNC = 0x55aa;


    private Spi spi;

    private List<Block> blocks;

    public VisionService() {
        if(SystemMode.isRealMode()) {
            spi = new Spi(0);
            System.out.println("Vision: Running in real mode");
        } else {
            System.out.println("Vision: Running in simulation mode");
        }
    }

    /**
     * Update the vision data
     */
    @Scheduled(fixedRate = 100)
    public void updateVisionData() {
        if(spi != null) {
            int lastWord = 0xffff;
            List<Block> curBlocks = null;
            while(true) {
                int curWord = readWord();

                // Re-Sync, if we are out of sync.
                if (curWord == PIXY_START_WORD_OUT_OF_SYNC) {
                    int secondByteLastWord = lastWord & 0x00FF;
                    // By reading only one byte we should be back in sync.
                    int syncByte = spi.writeByte((short) 0);
                    if (((secondByteLastWord & 0x00FF) == 0x00AA) && ((syncByte == 0x0055) || (syncByte == 0x0056))) {
                        lastWord = PIXY_START_WORD;
                        if(syncByte == 0x0055) {
                            curWord = PIXY_START_WORD;
                        } else {
                            curWord = PIXY_START_WORD_CC;
                        }
                    }
                }

                // If we read the normal start word twice, it's the start of a normal block.
                if (lastWord == PIXY_START_WORD && curWord == PIXY_START_WORD) {
                    if(curBlocks == null) {
                        curBlocks = new ArrayList<>();
                    }
                    curBlocks.add(readNormalBlock());
                }

                // If we read the normal start word followed by the color block start word,
                // it's the start of a color block.
                else if (lastWord == PIXY_START_WORD && curWord == PIXY_START_WORD_CC) {
                    if(curBlocks == null) {
                        curBlocks = new ArrayList<>();
                    }
                    curBlocks.add(readColorBlock());
                }

                // We have finished reading a complete set of blocks.
                else if((curBlocks != null) && !curBlocks.isEmpty()) {
                    blocks = curBlocks;
                    System.out.println("Vision: detected " + blocks.size() + " blocks");
                    return;
                }

                else {
                    lastWord = curWord;
                }
            }
        }
    }

    private int readWord() {
        return Integer.reverseBytes(spi.write_word(0)) >> 16 & 0x0000FFFF;
    }

    private Block readNormalBlock() {
        int checksum = readWord();
        int signatureNumber = readWord();
        int xCenter = readWord();
        int yCenter = readWord();
        int width = readWord();
        int height = readWord();

        if(checksum == signatureNumber + xCenter + yCenter + width + height) {
            return new Block(signatureNumber, xCenter, yCenter, width, height);
        } else {
            System.out.println("Vision: Invalid checksum");
        }
        return null;
    }

    private ColorBlock readColorBlock() {
        return new ColorBlock(0,0,0,0,0,0);
    }

}
