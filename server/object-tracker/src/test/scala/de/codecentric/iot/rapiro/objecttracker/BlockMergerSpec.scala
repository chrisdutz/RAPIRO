package de.codecentric.iot.rapiro.objecttracker

import de.codecentric.iot.rapiro.vision.model.Block
import org.scalatest._

/**
  * Created by christoferdutz on 25.10.16.
  */
class BlockMergerSpec extends FlatSpec with Matchers {
  val blockMerger:BlockMerger = new BlockMerger()

  "A Block" should "be added to an empty list without modification" in {
    val block: Block = new Block(1, 50, 50, 40, 40)
    val mergedBlocks: List[Block] = blockMerger.mergeBlocks(block, List[Block]())
    mergedBlocks.size should be (1)
    mergedBlocks.head should be (block)
  }

  it should "be added to a list without modification, if it doesn't intersect with any other block" in {
    val existingBlock: Block = new Block(1, 150, 50, 40, 40)
    val block: Block = new Block(1, 50, 50, 40, 40)
    val mergedBlocks: List[Block] = blockMerger.mergeBlocks(block, List[Block](existingBlock))
    mergedBlocks.size should be (2)
  }

  it should "be merged with existing blocks, if it intersects with any other block" in {
    val existingBlock: Block = new Block(1, 60, 30, 40, 40)
    val block: Block = new Block(1, 50, 50, 40, 40)
    val mergedBlocks: List[Block] = blockMerger.mergeBlocks(block, List[Block](existingBlock))
    mergedBlocks.size should be (1)
    mergedBlocks.head.getX should be (55)
    mergedBlocks.head.getY should be (40)
    mergedBlocks.head.getWidth should be (50)
    mergedBlocks.head.getHeight should be (60)
  }

  it should "not merge blocks which overlap, but have different signatures" in {
    val existingBlock: Block = new Block(1, 60, 30, 40, 40)
    val block: Block = new Block(2, 50, 50, 40, 40)
    val mergedBlocks: List[Block] = blockMerger.mergeBlocks(block, List[Block](existingBlock))
    mergedBlocks.size should be (2)
  }

  it should "be merged with two existing blocks, if it intersects with two other blocks" in {
    val existingBlock1: Block = new Block(1, 35, 30, 30, 20)
    val existingBlock2: Block = new Block(1, 90, 20, 40, 20)
    val block: Block = new Block(1, 60, 35, 40, 30)
    val mergedBlocks: List[Block] = blockMerger.mergeBlocks(block, List[Block](existingBlock1, existingBlock2))
    mergedBlocks.size should be (1)
    mergedBlocks.head.getX should be (65)
    mergedBlocks.head.getY should be (30)
    mergedBlocks.head.getWidth should be (90)
    mergedBlocks.head.getHeight should be (40)
  }

}
