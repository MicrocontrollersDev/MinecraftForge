package net.minecraftforge.fluids;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class FluidUtil {

    private FluidUtil() {
    }

    /**
     * Returns true if interaction was successful.
     */
    public static boolean interactWithTank(ItemStack stack, EntityPlayer player, IFluidHandler tank, EnumFacing side) {
        return false;
    }

    /**
     * Fill an empty bucket from the given tank. Uses the FluidContainerRegistry.
     *
     * @param bucket The empty bucket
     * @param tank   The tank to fill the bucket from
     * @param side   Side to access the tank from
     * @return The filled bucket or null if the liquid couldn't be taken from the tank.
     */
    public static ItemStack tryFillBucket(ItemStack bucket, IFluidHandler tank, EnumFacing side) {
        return null;
    }

    /**
     * Takes a filled bucket and tries to empty it into the given tank. Uses the FluidContainerRegistry.
     *
     * @param bucket The filled bucket
     * @param tank   The tank to fill with the bucket
     * @param side   Side to access the tank from
     * @return The empty bucket if successful, null if the tank couldn't be filled.
     */
    public static ItemStack tryEmptyBucket(ItemStack bucket, IFluidHandler tank, EnumFacing side) {
        return null;
    }

    /**
     * Takes an IFluidContainerItem and tries to fill it from the given tank.
     *
     * @param container The IFluidContainerItem Itemstack to fill. WILL BE MODIFIED!
     * @param tank      The tank to fill from
     * @param side      Side to access the tank from
     * @param player    The player that tries to fill the bucket. Needed if the input itemstack has a stacksize > 1 to determine where the filled container goes.
     * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accommodate for anything done in this method. New Itemstacks might have been added to the players inventory.
     */
    public static boolean tryFillFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, EntityPlayer player) {
        return false;
    }

    public static boolean tryEmptyFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, EntityPlayer player) {
        return false;
    }


    private static boolean insertItemIntoo(ItemStack stack, IInventory inventory, World world, BlockPos pos, boolean isCreative) {
        return false;
    }
}
