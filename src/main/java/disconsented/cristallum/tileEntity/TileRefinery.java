/*The MIT License (MIT)

Copyright (c) 2015 Disconsented, James Kerr

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package disconsented.cristallum.tileEntity;

import disconsented.cristallum.EnumSection;
import disconsented.cristallum.item.ItemCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedList;

public class TileRefinery extends TileEntity implements ITickable{
    private LinkedList<ItemStack> inventory = new LinkedList();
    //private ItemStack[] inventory = new ItemStack[5];
    //Current crystal stack that is being processed
    private ItemStack processing;
    //How much RF is used per second to process a crystal
    private static final int rfPerTick = 0;
    //How many ticks it takes to process
    private static final int ticksToProcess = 20;
    private int rf = 0;
    private static final int rfMax = 20000;
    private int ticksElapsed = 0;
    public static final String name = "refinery";

    public TileRefinery(){}

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }



    @Override
    public void update() {
        if(worldObj.isRemote){
            return;
        }
        if(processing == null){//Checking that we are currently processing something
            if(inventory.peekLast() != null){//Do we have anything currently in the inventory
                processing = inventory.pollLast();//Moving one ItemStack into processing
            } else {
                //reset ticks just in case
                ticksElapsed = 0;
                return;//Can't do anything
            }
        } else {
            if(rf >= rfPerTick){//Do we have enough to process
                rf -= rfPerTick;//Removing the required RF
                ticksElapsed++;//Increasing progress
            } else {
                return;//Can't work
            }

            if(ticksElapsed >= ticksToProcess){//Are we done?
                ticksElapsed = 0;
                Item item = Item.itemRegistry.getObject(new ResourceLocation(processing.getTagCompound().getString(ItemCrystal.TAG)));
                getWorld().spawnEntityInWorld(new EntityItem(getWorld(), getPos().getX(), getPos().getY()-2, getPos().getZ(), new ItemStack(item)));
                if(processing.stackSize > 1){
                    processing.stackSize--;
                    //Drop one into world
                } else {
                    processing = null;
                }
            }
        }
    }

    public boolean addItem(ItemStack itemStack){
        if(inventory.size() < 5){
            return inventory.offer(itemStack);
        } else {
            return false;
        }
    }
}