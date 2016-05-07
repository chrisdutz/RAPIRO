package de.codecentric.iot.rapiro.vision;

import de.codecentric.iot.rapiro.SystemMode;
import de.codecentric.iot.rapiro.vision.model.Block;
import de.codecentric.iot.rapiro.vision.model.ColorBlock;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import mraa.Spi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.messaging.MessageTemplate;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by christoferdutz on 23.03.16.
 */
@Service("visionService")
@RemotingDestination
public class VisionService implements InitializingBean {

    private static final String SERVICE_DESTINATION = "visionEvents";

    private static final int MIN_BLOCK_SIZE = 20;

    private static final int PIXY_START_WORD = 0xaa55;
    private static final int PIXY_START_WORD_COLOR_BLOCK = 0xaa56;
    // Is is the 55 of the second byte of the first word
    // and aa of the first byte of the second word.
    private static final int PIXY_START_WORD_OUT_OF_SYNC = 0x55aa;

    @Autowired
    private MessageBroker broker;

    @Autowired
    private MessageTemplate template;

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
     * Manually create a messaging destination 'visionEvents' for this service.
     */
    @Override
    public void afterPropertiesSet() {
        flex.messaging.services.Service service = broker.getService("messaging-service");
        Destination visionEvents = service.createDestination(SERVICE_DESTINATION);
        service.addDestination(visionEvents);
        service.start();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    private void setBlocks(List<Block> blocks) {
        if(blocks != this.blocks) {
            this.blocks = blocks;
            template.send(SERVICE_DESTINATION, blocks);
        }
    }

    /**
     * Update the vision data
     */
    @Scheduled(fixedRate = 100)
    public void updateVisionData() {
        if(spi != null) {
            int lastWord = 0xffff;
            int readWords = 0;
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
                            curWord = PIXY_START_WORD_COLOR_BLOCK;
                        }
                    }
                }

                // If we read the normal start word twice, it's the start of a normal block.
                if (lastWord == PIXY_START_WORD && curWord == PIXY_START_WORD) {
                    Block block = readNormalBlock();
                    if(block != null) {
                        if(curBlocks == null) {
                            curBlocks = new ArrayList<>();
                        }
                        if((block.getWidth() > MIN_BLOCK_SIZE) && (block.getHeight() > MIN_BLOCK_SIZE)) {
                            curBlocks.add(block);
                        }
                    }
                }

                // If we read the normal start word followed by the color block start word,
                // it's the start of a color block.
                else if (lastWord == PIXY_START_WORD && curWord == PIXY_START_WORD_COLOR_BLOCK) {
                    Block block = readColorBlock();
                    if(block != null) {
                        if(curBlocks == null) {
                            curBlocks = new ArrayList<>();
                        }
                        if((block.getWidth() > MIN_BLOCK_SIZE) && (block.getHeight() > MIN_BLOCK_SIZE)) {
                            curBlocks.add(block);
                        }
                    }
                }

                // We have finished reading a complete set of blocks.
                else if((curBlocks != null) && !curBlocks.isEmpty()) {
                    setBlocks(curBlocks);
                    return;
                }

                // If we haven't read the start of a block for some bytes
                // there probably is nothing, so we have to clear the list
                if(readWords > 30) {
                    setBlocks(null);
                    return;
                }

                // Save the last read word and continue.
                else {
                    readWords++;
                    lastWord = curWord;
                }
            }
        } else {
            // Todo ... dummy.
            setBlocks(Collections.singletonList(new Block(1, 2, 3, 4, 5)));
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
        }
        return null;
    }

    private ColorBlock readColorBlock() {
        int checksum = readWord();
        int signatureNumber = readWord();
        int xCenter = readWord();
        int yCenter = readWord();
        int width = readWord();
        int height = readWord();
        int angle = readWord();

        if(checksum == signatureNumber + xCenter + yCenter + width + height + angle) {
            return new ColorBlock(signatureNumber, xCenter, yCenter, width, height, angle);
        }
        return null;
    }

}
