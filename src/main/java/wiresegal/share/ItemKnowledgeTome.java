package wiresegal.share;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.CommandThaumcraft;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemKnowledgeTome extends Item {
    public ItemKnowledgeTome() {
        setTranslationKey("sharetome:sharing_tome");
        setRegistryName(new ResourceLocation("sharetome", "sharing_tome"));
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack held, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (ItemNBTHelper.verifyExistence(held, "Writer"))
            tooltip.add(I18n.format("sharetome.tooltip.player", ItemNBTHelper.getString(held, "Writer", "")));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        ItemStack held = playerIn.getHeldItem(handIn);

        if (playerIn.isSneaking() && !ItemNBTHelper.verifyExistence(held, "Knowledge")) {
            IPlayerKnowledge known = ThaumcraftCapabilities.getKnowledge(playerIn);

            if (known != null) {
                NBTTagCompound research = new NBTTagCompound();

                for (String key : known.getResearchList()) {
                    ResearchEntry entry = ResearchCategories.getResearch(key);
                    if (entry != null && entry.getStages() != null && known.isResearchComplete(key)) {
                        research.setBoolean(key, true);
                    }
                }

                ItemNBTHelper.setString(held, "Writer", playerIn.getDisplayNameString());
                ItemNBTHelper.setCompound(held, "Knowledge", research);

                if (!worldIn.isRemote)
                    playerIn.sendStatusMessage(new TextComponentTranslation("sharetome.misc.write"), false);
            }
        } else if (!playerIn.isSneaking() && ItemNBTHelper.verifyExistence(held, "Knowledge")) {
            IPlayerKnowledge known = ThaumcraftCapabilities.getKnowledge(playerIn);

            if (known != null) {
                boolean learnedAnything = false;

                NBTTagCompound research = ItemNBTHelper.getCompound(held, "Knowledge");
                for (String key : research.getKeySet()) {
                    ResearchEntry re = ResearchCategories.getResearch(key);
                    if (research.getBoolean(key) && re != null && re.getStages() != null) {
                        if (!known.isResearchComplete(key))
                            learnedAnything = true;
                        CommandThaumcraft.giveRecursiveResearch(playerIn, key);
                    }
                }

                if (playerIn instanceof EntityPlayerMP)
                    known.sync((EntityPlayerMP) playerIn);

                if (!worldIn.isRemote) {
                    if (learnedAnything)
                        playerIn.sendStatusMessage(new TextComponentTranslation("sharetome.misc.give", ItemNBTHelper.getString(held, "Writer", "")), false);
                    else
                        playerIn.sendStatusMessage(new TextComponentTranslation("sharetome.misc.no_change"), false);

                    SoundEvent write = SoundEvent.REGISTRY.getObject(new ResourceLocation("thaumcraft", "write"));
                    SoundEvent learn = SoundEvent.REGISTRY.getObject(new ResourceLocation("thaumcraft", "learn"));
                    if (write != null)
                        worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, write, SoundCategory.PLAYERS, 1F, 1F);
                    if (learn != null)
                        worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, learn, SoundCategory.PLAYERS, 1F, 1F);
                }
            }
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, held);
    }
}
