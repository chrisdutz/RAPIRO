package de.codecentric.iot.rapiro.vision.actors;

import de.codecentric.iot.rapiro.akka.actors.AbstractActor;
import de.codecentric.iot.rapiro.vision.adapter.VisionAdapter;
import de.codecentric.iot.rapiro.vision.model.Block;
import de.codecentric.iot.rapiro.vision.model.ColorBlock;
import de.codecentric.iot.rapiro.vision.model.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Actor that reads in data from the PixyCams SPI interface until a full
 * frame of block data has been read and dispatches each Block into
 *
 * Created by christoferdutz on 14.10.16.
 */
@Scope("prototype")
@Component("visionActor")
public class VisionActor extends AbstractActor<Scene> {

    private static final Logger LOG = LoggerFactory.getLogger(VisionActor.class);

    @Autowired
    private VisionAdapter visionAdapter;

    @Override
    public void onNext(Scene element) {
        super.onNext(element);
    }

    @Override
    @SuppressWarnings("all")
    protected List<Scene> getItems() {
        try {
            Set<Block> blocks = null;
            State state = State.BEFORE_FRAME;
            int checksum;
            while (true) {
                switch (state) {
                    case BEFORE_FRAME:
                        // Just read in bytes till the first 0xAA byte is read.
                        while (visionAdapter.readByte() != (byte) 0xAA);
                        state = State.FIRST_AA_BYTE_READ;
                        break;
                    case FIRST_AA_BYTE_READ:
                        if (visionAdapter.readByte() == (byte) 0x55) {
                            state = State.FIRST_55_BYTE_READ;
                        } else {
                            state = State.BEFORE_FRAME;
                        }
                        break;
                    case FIRST_55_BYTE_READ:
                        int word = visionAdapter.readWord();
                        if (word == 0xAA55) {
                            state = State.NORMAL_BLOCK_SYNC_WORD_READ;
                        } else if (word == 0xAA56) {
                            state = State.COLOR_BLOCK_SYNC_WORD_READ;
                        } else {
                            if ((blocks != null) && !blocks.isEmpty()) {
                                Scene scene = new Scene();
                                scene.setTime(Calendar.getInstance());
                                scene.setBlocks(blocks);
                                return Collections.singletonList(scene);
                            }
                            state = State.BEFORE_FRAME;
                        }
                        break;
                    case NORMAL_BLOCK_SYNC_WORD_READ:
                        checksum = visionAdapter.readWord();

                        Block block = new Block();
                        block.setSignature(visionAdapter.readWord());
                        block.setX(visionAdapter.readWord());
                        block.setY(visionAdapter.readWord());
                        block.setWidth(visionAdapter.readWord());
                        block.setHeight(visionAdapter.readWord());

                        if (block.getChecksum() == checksum) {
                            blocks = addBlock(blocks, block);
                        } else {
                            LOG.warn("Checksum error.");
                        }
                        state = State.FIRST_55_BYTE_READ;
                        break;
                    case COLOR_BLOCK_SYNC_WORD_READ:
                        checksum = visionAdapter.readWord();

                        ColorBlock colorBlock = new ColorBlock();
                        colorBlock.setSignature(visionAdapter.readWord());
                        colorBlock.setX(visionAdapter.readWord());
                        colorBlock.setY(visionAdapter.readWord());
                        colorBlock.setWidth(visionAdapter.readWord());
                        colorBlock.setHeight(visionAdapter.readWord());
                        colorBlock.setAngle(visionAdapter.readWord());

                        if (colorBlock.getChecksum() == checksum) {
                            blocks = addBlock(blocks, colorBlock);
                        } else {
                            LOG.warn("Checksum error.");
                        }
                        state = State.FIRST_55_BYTE_READ;
                        break;
                }
            }
        } catch (Exception e) {
            LOG.warn("An error occurred in getItems()", e);
            throw e;
        }
    }

    private Set<Block> addBlock(Set<Block> blocks, Block block) {
        if(blocks == null) {
            blocks = new HashSet<>();
        }
        if(includeBlock(block)) {
            blocks.add(block);
        }
        return blocks;
    }

    private boolean includeBlock(Block block) {
        return (block.getWidth() > 20) && (block.getHeight() > 20);
    }

    private enum State {
        BEFORE_FRAME,
        FIRST_AA_BYTE_READ,
        FIRST_55_BYTE_READ,
        NORMAL_BLOCK_SYNC_WORD_READ,
        COLOR_BLOCK_SYNC_WORD_READ
    }
}
