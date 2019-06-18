package wiresegal.share;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WireSegal
 * Created at 11:55 PM on 6/17/19.
 */
@Mod.EventBusSubscriber
@Mod(modid = "sharetome", name = "Tome of Sharing", version = "1.0", dependencies = "required-after:thaumcraft")
public class ShareTome {

    private static ItemKnowledgeTome tome;

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(tome = new ItemKnowledgeTome());
    }


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void modelRegister(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(tome, 0, getRL(tome));
    }

    @SideOnly(Side.CLIENT)
    private static ModelResourceLocation getRL(Item item) {
        ResourceLocation loc = item.getRegistryName();
        if (loc == null)
            return new ModelResourceLocation("missingno");

        return new ModelResourceLocation(loc, "inventory");
    }
}
